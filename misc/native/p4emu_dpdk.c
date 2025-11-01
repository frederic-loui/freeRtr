#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <dpdk/rte_config.h>
#include <dpdk/rte_common.h>
#include <dpdk/rte_version.h>
#include <dpdk/rte_eal.h>
#include <dpdk/rte_ethdev.h>
#include <dpdk/rte_mbuf.h>
#include <dpdk/rte_ring.h>



#include "p4emu_hdr.h"
#include "utils.h"
#include "types.h"


struct rte_mempool *mbuf_pool[RTE_MAX_NUMA_NODES];

struct rte_ring *tx_ring[RTE_MAX_ETHPORTS];

int port2pool[RTE_MAX_ETHPORTS];

void sendPack(unsigned char *bufD, int bufS, int port) {
    struct rte_mbuf *mbuf = rte_pktmbuf_alloc(mbuf_pool[port2pool[port]]);
    if (mbuf == NULL) return;
    char * pack = rte_pktmbuf_append(mbuf, bufS);
    if (pack == NULL) goto err;
    memcpy(pack, bufD, bufS);
    if (rte_ring_mp_enqueue(tx_ring[port], mbuf) == 0) return;
err:
    rte_pktmbuf_free(mbuf);
}


void setMtu(int port, int mtu) {
    rte_eth_dev_set_mtu(port, mtu);
}


void setState(int port, int sta) {
    if (sta == 1) rte_eth_dev_set_link_up(port);
    else rte_eth_dev_set_link_down(port);
}


int getState(int port) {
    struct rte_eth_link link;
    if (rte_eth_link_get_nowait(port, &link) != 0) return 1;
#if RTE_VERSION < RTE_VERSION_NUM(22, 11, 0, 0)
    if (link.link_status == ETH_LINK_UP) return 1;
#else
    if (link.link_status == RTE_ETH_LINK_UP) return 1;
#endif
    return 0;
}


void getStats(int port, unsigned char*buf, unsigned char*pre, int*len) {
    struct rte_eth_stats stat;
    if (rte_eth_stats_get(port, &stat) != 0) return;
    *len += snprintf((char*)&buf[*len], 128, "%s ipackets %li\r\n", (char*)pre, (long)stat.ipackets);
    *len += snprintf((char*)&buf[*len], 128, "%s opackets %li\r\n", (char*)pre, (long)stat.opackets);
    *len += snprintf((char*)&buf[*len], 128, "%s ibytes %li\r\n", (char*)pre, (long)stat.ibytes);
    *len += snprintf((char*)&buf[*len], 128, "%s obytes %li\r\n", (char*)pre, (long)stat.obytes);
    *len += snprintf((char*)&buf[*len], 128, "%s imissed %li\r\n", (char*)pre, (long)stat.imissed);
    *len += snprintf((char*)&buf[*len], 128, "%s ierrors %li\r\n", (char*)pre, (long)stat.ierrors);
    *len += snprintf((char*)&buf[*len], 128, "%s oerrors %li\r\n", (char*)pre, (long)stat.oerrors);
    *len += snprintf((char*)&buf[*len], 128, "%s nombuf %li\r\n", (char*)pre, (long)stat.rx_nombuf);
    for (int i = 0;; i++) {
        uint64_t xstat_id;
        uint64_t xstat_val;
        struct rte_eth_xstat_name xstat_nam;
        xstat_id = i;
        if (rte_eth_xstats_get_by_id(port, &xstat_id, &xstat_val, 1) != 1) return;
        if (rte_eth_xstats_get_names_by_id(port, &xstat_nam, 1, &xstat_id) != 1) return;
        *len += snprintf((char*)&buf[*len], 128, "%s %s %li\r\n", (char*)pre, xstat_nam.name, (long)xstat_val);
    }
}


void err(char*buf) {
    printf("%s\n", buf);
    _exit(1);
}



int burst_size;

int burst_sleep;




struct lcore_conf {
    int rx_num;
    int rx_list[RTE_MAX_ETHPORTS];
    int tx_num;
    int tx_list[RTE_MAX_ETHPORTS];
    int justProcessor;
} __rte_cache_aligned;
struct lcore_conf lcore_conf[RTE_MAX_LCORE];
struct rte_ring *lcore_ring[RTE_MAX_LCORE];
int lcore_procs;





#if RTE_VERSION < RTE_VERSION_NUM(21, 11, 0, 0)
#define RTE_MBUF_F_RX_VLAN_STRIPPED PKT_RX_VLAN_STRIPPED
#endif

#define mbuf2mybuf(mbuf)                                        \
    bufS = rte_pktmbuf_pkt_len(mbuf);                           \
    bufP = rte_pktmbuf_mtod(mbuf, void *);                      \
    if ((mbuf->ol_flags & RTE_MBUF_F_RX_VLAN_STRIPPED) != 0) {  \
        memcpy(&bufD[preBuff], bufP, 12);                       \
        put16msb(bufD, preBuff + 12, ETHERTYPE_VLAN);           \
        put16msb(bufD, preBuff + 14, mbuf->vlan_tci);           \
        memcpy(&bufD[preBuff + 16], bufP + 12, bufS - 12);      \
        bufS += 4;                                              \
    } else {                                                    \
        memcpy(&bufD[preBuff], bufP, bufS);                     \
    }                                                           \
    rte_pktmbuf_free(mbuf);




static int doPacketLoop(__rte_unused void *arg) {
    unsigned char * bufP;
    int bufS;
    int port;
    int pkts;
    int done;
    int seq;
    int num;
    int i;
    int lcore = rte_lcore_id();
    struct lcore_conf *myconf = &lcore_conf[lcore];
    printf("lcore %i started with %i rx and %i tx ports and %i processing!\n", lcore, myconf->rx_num, myconf->tx_num, myconf->justProcessor);
    if ((myconf->rx_num + myconf->tx_num + myconf->justProcessor) < 1) return 0;
    struct rte_mbuf **mbufs = malloc(burst_size * sizeof(struct rte_mbuf*));
    if (mbufs == NULL) err("error allocating mbufptrs");
    struct packetContext ctx;
    if (initContext(&ctx) != 0) err("error initializing context");
    unsigned char *bufD = ctx.bufD;

    if (lcore_procs < 1) {
        for (;;) {
            pkts = 0;
            for (seq = 0; seq < myconf->tx_num; seq++) {
                port = myconf->tx_list[seq];
                num = rte_ring_count(tx_ring[port]);
                if (num > burst_size) num = burst_size;
                num = rte_ring_sc_dequeue_bulk(tx_ring[port], (void**)mbufs, num, NULL);
                if (num < 1) continue;
                pkts += num;
                done = rte_eth_tx_burst(port, 0, mbufs, num);
                for (; done < num; done++) rte_pktmbuf_free(mbufs[done]);
            }
            for (seq = 0; seq < myconf->rx_num; seq++) {
                port = ctx.port = myconf->rx_list[seq];
                ctx.stat = ifaceStat[port];
                num = rte_eth_rx_burst(port, 0, mbufs, burst_size);
                pkts += num;
                if (port == cpuPort) {
                    for (i = 0; i < num; i++) {
                        mbuf2mybuf(mbufs[i]);
                        processCpuPack(&ctx, bufS);
                    }
                    continue;
                }
                for (i = 0; i < num; i++) {
                    mbuf2mybuf(mbufs[i]);
                    processDataPacket(&ctx, bufS, port);
                }
            }
            if ((pkts < 1) && (burst_sleep > 0)) usleep(burst_sleep);
        }
    } else if (myconf->justProcessor > 0) {
        seq = myconf->justProcessor - 1;
        for (;;) {
            num = rte_ring_count(lcore_ring[seq]);
            if (num > burst_size) num = burst_size;
            num = rte_ring_sc_dequeue_bulk(lcore_ring[seq], (void**)mbufs, num, NULL);
            if (num < 1) {
                if (burst_sleep > 0) usleep(burst_sleep);
                continue;
            }
            for (i = 0; i < num; i++) {
                port = ctx.port = mbufs[i]->port;
                ctx.stat = ifaceStat[port];
                mbuf2mybuf(mbufs[i]);
                processDataPacket(&ctx, bufS, port);
            }
        }
    } else {
        for (;;) {
            pkts = 0;
            for (seq = 0; seq < myconf->tx_num; seq++) {
                port = myconf->tx_list[seq];
                num = rte_ring_count(tx_ring[port]);
                if (num > burst_size) num = burst_size;
                num = rte_ring_sc_dequeue_bulk(tx_ring[port], (void**)mbufs, num, NULL);
                if (num < 1) continue;
                pkts += num;
                done = rte_eth_tx_burst(port, 0, mbufs, num);
                for (; done < num; done++) rte_pktmbuf_free(mbufs[done]);
            }
            for (seq = 0; seq < myconf->rx_num; seq++) {
                port = myconf->rx_list[seq];
                num = rte_eth_rx_burst(port, 0, mbufs, burst_size);
                pkts += num;
                if (port == cpuPort) {
                    for (i = 0; i < num; i++) {
                        mbuf2mybuf(mbufs[i]);
                        processCpuPack(&ctx, bufS);
                    }
                    continue;
                }
                for (i = 0; i < num; i++) {
                    bufP = rte_pktmbuf_mtod(mbufs[i], void *);
                    port = hashDataPacket(bufP) % lcore_procs;
                    if (rte_ring_mp_enqueue(lcore_ring[port], mbufs[i]) != 0) rte_pktmbuf_free(mbufs[i]);
                }
            }
            if ((pkts < 1) && (burst_sleep > 0)) usleep(burst_sleep);
        }
    }
    err("packet thread exited");
    return 0;
}







int main(int argc, char **argv) {

    int ret = rte_eal_init(argc, argv);
    if (ret < 0) err("error with eal initialization");

    argc -= ret;
    argv += ret;

    dataPorts = rte_eth_dev_count_avail();
    if (dataPorts < 2) err("at least 2 ports needed");
    int cores = rte_lcore_count();
    if (cores < 1) err("at least 1 cores needed");
    printf("%i cores and %i ports detected...\n", cores, dataPorts);
    if (dataPorts > maxPorts) dataPorts = maxPorts;

    if (argc < 4) err("using: dp [dpdk options] -- <host> <rport> <cpuport> [port rxcore txcore] [-1 fwdcore fwdcore] [-2 mbufsiz 0] [-3 mbufnum 0] [-4 mbufcache 0] [-5 desctx 0] [-6 descrx 0] [-7 ringrx 0] [-8 ringfwd 0] [-9 brstsiz 0] [-10 brstslp 0]");
    printf("dpdk version: %s\n", rte_version());
    if (initTables() != 0) err("error initializing tables");
    int port = atoi(argv[2]);
    struct sockaddr_in addr;
    memset(&addr, 0, sizeof (addr));
    if (inet_aton(argv[1], &addr.sin_addr) == 0) err("bad addr address");
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    printf("connecting %s %i.\n", inet_ntoa(addr.sin_addr), port);
    commandSock = socket(AF_INET, SOCK_STREAM, 0);
    if (commandSock < 0) err("unable to open socket");
    if (connect(commandSock, (struct sockaddr*)&addr, sizeof(addr)) < 0) err("failed to connect socket");
    cpuPort = atoi(argv[3]);
    printf("cpu port is #%i of %i...\n", cpuPort, dataPorts);

    int port2rx[RTE_MAX_ETHPORTS];
    int port2tx[RTE_MAX_ETHPORTS];
    memset(&port2rx, 0, sizeof(port2rx));
    memset(&port2tx, 0, sizeof(port2tx));
    memset(&lcore_conf, 0, sizeof(lcore_conf));
    int mbuf_size = RTE_MBUF_DEFAULT_DATAROOM;
    int mbuf_num = 8191;
    int mbuf_cache = 250;
    int desc_rx = 1024;
    int desc_tx = 1024;
    int ring_tx = 512;
    int ring_fwd = 512;
    burst_size = 32;
    burst_sleep = 100;
    for (int i = 4;; i += 3) {
        if ((i+2) >= argc) break;
        int p = atoi(argv[i + 0]);
        int r = atoi(argv[i + 1]);
        int t = atoi(argv[i + 2]);
        if (p <= -10) {
            burst_sleep = r;
            continue;
        }
        if (p <= -9) {
            burst_size = r;
            continue;
        }
        if (p <= -8) {
            ring_fwd = r;
            continue;
        }
        if (p <= -7) {
            ring_tx = r;
            continue;
        }
        if (p <= -6) {
            desc_rx = r;
            continue;
        }
        if (p <= -5) {
            desc_tx = r;
            continue;
        }
        if (p <= -4) {
            mbuf_cache = r;
            continue;
        }
        if (p <= -3) {
            mbuf_num = r;
            continue;
        }
        if (p <= -2) {
            mbuf_size = r;
            continue;
        }
        if (r > cores) continue;
        if (t > cores) continue;
        if (p <= -1) {
            lcore_conf[r].justProcessor++;
            lcore_conf[t].justProcessor++;
            continue;
        }
        if (p >= dataPorts) continue;
        port2rx[p] = r;
        port2tx[p] = t;
    }
    lcore_procs = 0;
    for (int i = 0; i < RTE_MAX_LCORE; i++) {
        if (lcore_conf[i].justProcessor < 1) continue;
        lcore_procs++;
        lcore_conf[i].justProcessor = lcore_procs;
    }
    for (int i = 0; i < dataPorts; i++) {
        int r = port2rx[i];
        int t = port2tx[i];
        lcore_conf[r].rx_list[lcore_conf[r].rx_num] = i;
        lcore_conf[r].rx_num++;
        lcore_conf[t].tx_list[lcore_conf[t].tx_num] = i;
        lcore_conf[t].tx_num++;
    }

    printf("there will be %i forwarding only cores...\n", lcore_procs);
    printf("there will be %i mbufs, each %i bytes, %i cached...\n", mbuf_num, mbuf_size, mbuf_cache);
    printf("there will be %i rx and %i tx descriptors, %i tx and %i fwd mbufs...\n", desc_rx, desc_tx, ring_tx, ring_fwd);
    printf("there will be %i bursts and maybe %i sleeps...\n", burst_size, burst_sleep);
    ret = rte_socket_count();
    for (int i = 0; i < ret; i++) {
        unsigned char buf[128];
        sprintf((char*)&buf[0], "dpdk-pool%i", i);
        printf("opening mempool on socket %i...\n", i);
        mbuf_pool[i] = rte_pktmbuf_pool_create((char*)&buf[0], mbuf_num * dataPorts, mbuf_cache, 0, (mbuf_size + (RTE_MBUF_DEFAULT_BUF_SIZE - RTE_MBUF_DEFAULT_DATAROOM)), i);
        if (mbuf_pool[i] == NULL) err("cannot create mbuf pool");
    }

    for (int i = 0; i < RTE_MAX_LCORE; i++) {
        if (lcore_conf[i].justProcessor < 1) continue;
        int o = lcore_conf[i].justProcessor - 1;
        int sock = rte_lcore_to_socket_id(i);
        printf("opening forwarder %i on lcore %i on socket %i...\n", o, i, sock);
        unsigned char buf[128];
        sprintf((char*)&buf[0], "dpdk-pack%i", i);
        lcore_ring[o] = rte_ring_create((char*)&buf[0], ring_fwd, sock, RING_F_SC_DEQ);
        if (lcore_ring[o] == NULL) err("error allocating pack ring");
    }

    RTE_ETH_FOREACH_DEV(port) {
        ret = rte_eth_dev_is_valid_port(port);
        if (ret != 1) err("not valid port");
        unsigned char buf[1024];
        ret = rte_eth_dev_get_name_by_port(port, (char*)&buf[0]);
        if (ret != 0) strcpy((char*)&buf[0], "unknown");
        int sock = rte_eth_dev_socket_id(port);
        if (sock < 0) {
            printf("error getting socket id\n");
            sock = 0;
        }
        port2pool[port] = sock;
        printf("opening port %i named %s on socket %i on lcore %i for rx and %i for tx...\n", port, (char*)&buf[0], sock, port2rx[port], port2tx[port]);
        initIface(port, (char*)&buf[0]);
        sprintf((char*)&buf[0], "dpdk-port%i", port);

        struct rte_eth_conf port_conf;
        memset(&port_conf, 0, sizeof(port_conf));
        unsigned short nb_rxd = desc_rx;
        unsigned short nb_txd = desc_tx;
        struct rte_eth_dev_info dev_info;
        memset(&dev_info, 0, sizeof(dev_info));
        struct rte_eth_txconf txconf;
        memset(&txconf, 0, sizeof(txconf));
        struct rte_eth_rxconf rxconf;
        memset(&rxconf, 0, sizeof(rxconf));
        struct rte_ether_addr macaddr;
        memset(&macaddr, 0, sizeof(macaddr));
        int maxlen;

        ret = rte_eth_dev_info_get(port, &dev_info);
        if (ret != 0) printf("error getting device info\n");

        printf("devinfo: pmd=%s, mtu=%i..%i, bufs=%i, pktl=%i, rxque=%o, txque=%i, rxcapa=%08x, txcapa=%08x...\n", dev_info.driver_name, dev_info.min_mtu, dev_info.max_mtu, dev_info.min_rx_bufsize, dev_info.max_rx_pktlen, dev_info.max_rx_queues, dev_info.max_tx_queues, (int)dev_info.rx_offload_capa, (int)dev_info.tx_offload_capa);

#if RTE_VERSION < RTE_VERSION_NUM(22, 11, 0, 0)
        if (dev_info.tx_offload_capa & DEV_TX_OFFLOAD_MBUF_FAST_FREE) {
            port_conf.txmode.offloads |= DEV_TX_OFFLOAD_MBUF_FAST_FREE;
        }
        if (dev_info.tx_offload_capa & DEV_TX_OFFLOAD_MULTI_SEGS) {
            port_conf.txmode.offloads |= DEV_TX_OFFLOAD_MULTI_SEGS;
        }
        if (dev_info.rx_offload_capa & DEV_RX_OFFLOAD_SCATTER) {
            port_conf.rxmode.offloads |= DEV_RX_OFFLOAD_SCATTER;
        }
#else
        if (dev_info.tx_offload_capa & RTE_ETH_TX_OFFLOAD_MBUF_FAST_FREE) {
            port_conf.txmode.offloads |= RTE_ETH_TX_OFFLOAD_MBUF_FAST_FREE;
        }
        if (dev_info.tx_offload_capa & RTE_ETH_TX_OFFLOAD_MULTI_SEGS) {
            port_conf.txmode.offloads |= RTE_ETH_TX_OFFLOAD_MULTI_SEGS;
        }
        if (dev_info.rx_offload_capa & RTE_ETH_RX_OFFLOAD_SCATTER) {
            port_conf.rxmode.offloads |= RTE_ETH_RX_OFFLOAD_SCATTER;
        }
#endif
#if RTE_VERSION < RTE_VERSION_NUM(21, 11, 0, 0)
        if (dev_info.rx_offload_capa & DEV_RX_OFFLOAD_JUMBO_FRAME) {
            port_conf.rxmode.offloads |= DEV_RX_OFFLOAD_JUMBO_FRAME;
            maxlen = mbuf_size;
            port_conf.rxmode.max_rx_pkt_len = maxlen;
        } else {
            maxlen = RTE_ETHER_MAX_LEN;
            port_conf.rxmode.max_rx_pkt_len = maxlen;
        }
#else
        maxlen = dev_info.max_mtu;
        if (maxlen > mbuf_size) maxlen = mbuf_size;
        //port_conf.rxmode.mtu = maxlen;
#endif
        printf("configuring port: offloads rx=%08x, tx=%08x, pktlen=%i\n", (int)port_conf.rxmode.offloads, (int)port_conf.txmode.offloads, maxlen);
        ret = rte_eth_dev_configure(port, 1, 1, &port_conf);
        if (ret != 0) printf("error configuring port\n");

        ret = rte_eth_dev_adjust_nb_rx_tx_desc(port, &nb_rxd, &nb_txd);
        if (ret != 0) printf("error adjusting descriptors\n");

        rxconf = dev_info.default_rxconf;
        rxconf.offloads = port_conf.rxmode.offloads;
        ret = rte_eth_rx_queue_setup(port, 0, nb_rxd, sock, &rxconf, mbuf_pool[sock]);
        if (ret != 0) printf("error setting up rx queue\n");

        txconf = dev_info.default_txconf;
        txconf.offloads = port_conf.txmode.offloads;
        ret = rte_eth_tx_queue_setup(port, 0, nb_txd, sock, &txconf);
        if (ret != 0) printf("error setting up tx queue\n");

        tx_ring[port] = rte_ring_create((char*)&buf[0], ring_tx, sock, RING_F_SC_DEQ);
        if (tx_ring[port] == NULL) err("error allocating tx ring");

        ret = rte_eth_dev_start(port);
        if (ret != 0) printf("error starting port\n");

        ret = rte_eth_macaddr_get(port, &macaddr);
        if (ret != 0) printf("error getting mac\n");

        ret = rte_eth_promiscuous_enable(port);
        if (ret != 0) printf("error setting promiscuous mode\n");
    }

    doNegotiate("dpdk");
    pthread_t threadSock;
    if (pthread_create(&threadSock, NULL, (void*) & doSockLoop, NULL)) err("error creating socket thread");
    pthread_t threadStat;
    if (pthread_create(&threadStat, NULL, (void*) & doStatLoop, NULL)) err("error creating status thread");

    printf("starting lcores...\n");
#if RTE_VERSION < RTE_VERSION_NUM(20, 11, 0, 0)
    rte_eal_mp_remote_launch(&doPacketLoop, NULL, CALL_MASTER);
#else
    rte_eal_mp_remote_launch(&doPacketLoop, NULL, CALL_MAIN);
#endif

    doMainLoop();

}
