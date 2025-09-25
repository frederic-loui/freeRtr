package org.freertr.clnt;

import org.freertr.addr.addrEmpty;
import org.freertr.addr.addrIP;
import org.freertr.addr.addrType;
import org.freertr.auth.autherChap;
import org.freertr.cfg.cfgAll;
import org.freertr.cfg.cfgIfc;
import org.freertr.cfg.cfgVrf;
import org.freertr.ifc.ifcDn;
import org.freertr.ifc.ifcNull;
import org.freertr.ifc.ifcUp;
import org.freertr.ip.ipFwdIface;
import org.freertr.pack.packHolder;
import org.freertr.pack.packL2f;
import org.freertr.prt.prtGenConn;
import org.freertr.prt.prtServP;
import org.freertr.prt.prtUdp;
import org.freertr.serv.servL2f;
import org.freertr.user.userFormat;
import org.freertr.util.bits;
import org.freertr.util.counter;
import org.freertr.util.debugger;
import org.freertr.util.logger;
import org.freertr.util.notifier;
import org.freertr.util.state;

/**
 * layer two forwarding protocol (rfc2341) client
 *
 * @author matecsaba
 */
public class clntL2f implements Runnable, prtServP, ifcDn {

    /**
     * create instance
     */
    public clntL2f() {
    }

    /**
     * upper layer
     */
    public ifcUp upper = new ifcNull();

    /**
     * preferred ip protocol version
     */
    public int prefer = 0;

    /**
     * target of tunnel
     */
    public String target = null;

    /**
     * vrf of target
     */
    public cfgVrf vrf = null;

    /**
     * source interface
     */
    public cfgIfc srcIfc = null;

    /**
     * hostname
     */
    public String hostname;

    /**
     * password
     */
    public String password;

    /**
     * sending ttl value, -1 means maps out
     */
    public int sendingTTL = 255;

    /**
     * sending tos value, -1 means maps out
     */
    public int sendingTOS = -1;

    /**
     * sending df value, -1 means maps out
     */
    public int sendingDFN = -1;

    /**
     * sending flow value, -1 means maps out
     */
    public int sendingFLW = -1;

    /**
     * counter
     */
    public counter cntr = new counter();

    private boolean working = true;

    private prtGenConn conn;

    private int seqTx;

    private int keyRem;

    private int keyLoc;

    private int tunLoc;

    private int tunRem;

    private int multi;

    private notifier notif;

    private packL2f pckRx;

    public String toString() {
        return "l2f to " + target;
    }

    /**
     * get hw address
     *
     * @return hw address
     */
    public addrType getHwAddr() {
        return new addrEmpty();
    }

    /**
     * set filter mode
     *
     * @param promisc promiscous mode
     */
    public void setFilter(boolean promisc) {
    }

    /**
     * get state
     *
     * @return state
     */
    public state.states getState() {
        return state.states.up;
    }

    /**
     * close interface
     */
    public void closeDn() {
        clearState();
    }

    /**
     * flapped interface
     */
    public void flapped() {
        clearState();
    }

    /**
     * set upper layer
     *
     * @param server upper layer
     */
    public void setUpper(ifcUp server) {
        upper = server;
        upper.setParent(this);
    }

    /**
     * get counter
     *
     * @return counter
     */
    public counter getCounter() {
        return cntr;
    }

    /**
     * get mtu size
     *
     * @return mtu size
     */
    public int getMTUsize() {
        return 1400;
    }

    /**
     * get bandwidth
     *
     * @return bandwidth
     */
    public long getBandwidth() {
        return 8000000;
    }

    /**
     * send packet
     *
     * @param pck packet
     */
    public void sendPack(packHolder pck) {
        if (multi == 0) {
            return;
        }
        pck.merge2beg();
        packL2f pckPrt = new packL2f();
        pckPrt.proto = packL2f.prtPpp;
        pckPrt.client = tunRem;
        pckPrt.key = keyRem;
        pckPrt.multi = multi;
        pckPrt.createHeader(pck);
        cntr.tx(pck);
        pck.putDefaults();
        conn.send2net(pck);
    }

    /**
     * start connection
     */
    public void workStart() {
        new Thread(this).start();
    }

    /**
     * stop connection
     */
    public void workStop() {
        working = false;
        clearState();
    }

    public void run() {
        for (;;) {
            if (!working) {
                break;
            }
            try {
                clearState();
                workDoer();
            } catch (Exception e) {
                logger.traceback(e);
            }
            clearState();
            bits.sleep(1000);
        }
    }

    private void workDoer() {
        addrIP trg = clntDns.justResolv(target, prefer);
        if (trg == null) {
            return;
        }
        prtUdp udp = vrf.getUdp(trg);
        servL2f srv = new servL2f();
        ipFwdIface fwdIfc = null;
        if (srcIfc != null) {
            fwdIfc = srcIfc.getFwdIfc(trg);
        }
        conn = udp.packetConnect(this, fwdIfc, 0, trg, srv.srvPort(), srv.srvName(), -1, null, -1, -1);
        if (conn == null) {
            return;
        }
        conn.timeout = 180000;
        conn.sendFLW = sendingFLW;
        conn.sendTOS = sendingTOS;
        conn.sendDFN = sendingDFN;
        conn.sendTTL = sendingTTL;
        tunLoc = bits.randomW();
        byte[] chlLoc = null;
        if (password != null) {
            chlLoc = new byte[16];
            for (int i = 0; i < chlLoc.length; i++) {
                chlLoc[i] = (byte) bits.randomB();
            }
        }
        packHolder pckBin = new packHolder(true, true);
        packL2f pckTx = new packL2f();
        pckBin.clear();
        pckTx.createConf(pckBin, hostname == null ? cfgAll.hostName : hostname, chlLoc, tunLoc);
        pckTx.seq = seqTx++;
        pckTx.client = tunRem;
        pckTx.key = keyRem;
        pckTx.createHeader(pckBin);
        if (debugger.clntL2fTraf) {
            logger.debug("tx " + pckTx.dump());
        }
        if (wait4msg(pckBin, packL2f.typConf)) {
            return;
        }
        byte[] chlRem = pckRx.valChal;
        tunRem = pckRx.valClid;
        byte[] res = null;
        if (chlLoc != null) {
            if (chlRem == null) {
                return;
            }
            res = autherChap.calcAuthHash(tunRem, password, chlRem);
            keyRem = packL2f.calcKey(res);
        }
        pckTx = new packL2f();
        pckBin.clear();
        pckTx.createOpen(pckBin, res);
        pckTx.seq = seqTx++;
        pckTx.client = tunRem;
        pckTx.key = keyRem;
        pckTx.createHeader(pckBin);
        if (debugger.clntL2fTraf) {
            logger.debug("tx " + pckTx.dump());
        }
        if (wait4msg(pckBin, packL2f.typOpen)) {
            return;
        }
        if (chlLoc != null) {
            if (pckRx.valResp == null) {
                return;
            }
            res = autherChap.calcAuthHash(tunLoc, password, chlLoc);
            if (res.length != pckRx.valResp.length) {
                return;
            }
            if (bits.byteComp(res, 0, pckRx.valResp, 0, res.length) != 0) {
                return;
            }
            keyLoc = packL2f.calcKey(res);
        }
        multi = bits.randomW();
        pckTx = new packL2f();
        pckBin.clear();
        pckTx.createOpen(pckBin, null);
        pckTx.seq = seqTx++;
        pckTx.client = tunRem;
        pckTx.multi = multi;
        pckTx.key = keyRem;
        pckTx.createHeader(pckBin);
        if (debugger.clntL2fTraf) {
            logger.debug("tx " + pckTx.dump());
        }
        if (wait4msg(pckBin, packL2f.typOpen)) {
            return;
        }
        if (pckRx.key != keyLoc) {
            return;
        }
        int keep = 0;
        for (;;) {
            if (conn.txBytesFree() < 0) {
                return;
            }
            notif.sleep(1000);
            switch (pckRx.type) {
                case packL2f.typClose:
                    return;
                case packL2f.typEchoRes:
                    keep = 0;
                    continue;
            }
            keep++;
            if (keep < 5) {
                continue;
            }
            if (keep > 10) {
                return;
            }
            pckTx = new packL2f();
            pckBin.clear();
            pckTx.valResp = new byte[1];
            pckTx.valResp[0] = (byte) bits.randomB();
            pckTx.createEchoReq(pckBin, pckTx.valResp);
            pckTx.seq = bits.randomB();
            pckTx.client = tunRem;
            pckTx.key = keyRem;
            pckTx.createHeader(pckBin);
            cntr.tx(pckBin);
            conn.send2net(pckBin);
            if (debugger.clntL2fTraf) {
                logger.debug("tx " + pckTx.dump());
            }
        }
    }

    private boolean wait4msg(packHolder pck, int typ) {
        for (int cnt = 0; cnt < cfgAll.l2fRetry; cnt++) {
            if (pck != null) {
                if (cnt > 0) {
                    if (debugger.clntL2fTraf) {
                        logger.debug("retransmit");
                    }
                }
                conn.send2net(pck.copyBytes(true, true));
            }
            notif.sleep(cfgAll.l2fTimer);
            if (pckRx.type == typ) {
                return false;
            }
            if (conn.txBytesFree() < 0) {
                return true;
            }
        }
        return true;
    }

    private void clearState() {
        if (conn != null) {
            conn.setClosing();
        }
        seqTx = 0;
        keyRem = 0;
        keyLoc = 0;
        tunLoc = 0;
        tunRem = 0;
        multi = 0;
        notif = new notifier();
        pckRx = new packL2f();
    }

    /**
     * close interface
     *
     * @param ifc interface
     */
    public void closedInterface(ipFwdIface ifc) {
    }

    /**
     * accept connection
     *
     * @param id connection
     * @return false on success, true on error
     */
    public boolean datagramAccept(prtGenConn id) {
        return true;
    }

    /**
     * connection ready
     *
     * @param id connection
     */
    public void datagramReady(prtGenConn id) {
    }

    /**
     * close connection
     *
     * @param id connection
     */
    public void datagramClosed(prtGenConn id) {
    }

    /**
     * work connection
     *
     * @param id connection
     */
    public void datagramWork(prtGenConn id) {
    }

    /**
     * received error
     *
     * @param id connection
     * @param pck packet
     * @param rtr reporting router
     * @param err error happened
     * @param lab error label
     * @return false on success, true on error
     */
    public boolean datagramError(prtGenConn id, packHolder pck, addrIP rtr, counter.reasons err, int lab) {
        return false;
    }

    /**
     * notified that state changed
     *
     * @param id id number to reference connection
     * @param stat state
     * @return return false if successful, true if error happened
     */
    public boolean datagramState(prtGenConn id, state.states stat) {
        return false;
    }

    /**
     * received packet
     *
     * @param id connection
     * @param pckBin packet
     * @return false on success, true on error
     */
    public boolean datagramRecv(prtGenConn id, packHolder pckBin) {
        pckRx = new packL2f();
        if (pckRx.parseHeader(pckBin)) {
            cntr.drop(pckBin, counter.reasons.badHdr);
            return false;
        }
        if (pckRx.proto != packL2f.prtMgmt) {
            if (pckRx.multi != multi) {
                cntr.drop(pckBin, counter.reasons.badID);
                return false;
            }
            cntr.rx(pckBin);
            upper.recvPack(pckBin);
            return false;
        }
        switch (pckRx.type) {
            case packL2f.typConf:
                if (pckRx.parseConf(pckBin)) {
                    return false;
                }
                break;
            case packL2f.typOpen:
                if (pckRx.parseOpen(pckBin)) {
                    return false;
                }
                break;
            case packL2f.typClose:
                if (pckRx.parseClose(pckBin)) {
                    return false;
                }
                break;
            case packL2f.typEchoReq:
                pckRx.parseEcho(pckBin);
                break;
            case packL2f.typEchoRes:
                pckRx.parseEcho(pckBin);
                break;
            default:
                return false;
        }
        if (debugger.clntL2fTraf) {
            logger.debug("rx " + pckRx.dump());
        }
        notif.wakeup();
        return false;
    }

    /**
     * get show
     *
     * @return state
     */
    public userFormat getShow() {
        userFormat res = new userFormat("|", "category|value");
        res.add("conn|" + conn);
        res.add("upper|" + upper);
        res.add("cntr|" + cntr);
        res.add("tunloc|" + tunLoc);
        res.add("tunrem|" + tunRem);
        return res;
    }

}
