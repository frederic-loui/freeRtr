package org.freertr.prt;

import org.freertr.addr.addrIP;
import org.freertr.addr.addrMac;
import org.freertr.addr.addrType;
import org.freertr.cry.cryEncrGeneric;
import org.freertr.cry.cryHashGeneric;
import org.freertr.ifc.ifcDn;
import org.freertr.ifc.ifcNull;
import org.freertr.ifc.ifcUp;
import org.freertr.ip.ipFwd;
import org.freertr.ip.ipFwdIface;
import org.freertr.ip.ipPrt;
import org.freertr.pack.packHolder;
import org.freertr.sec.secTransform;
import org.freertr.util.bits;
import org.freertr.util.counter;
import org.freertr.util.logger;
import org.freertr.util.state;

/**
 * integrated network layer security protocol client
 *
 * @author matecsaba
 */
public class prtInlsp implements ipPrt, ifcDn {

    /**
     * protocol number
     */
    public final static int prot = 52;

    /**
     * size of header
     */
    public final static int size = 8;

    /**
     * upper layer
     */
    public ifcUp upper = new ifcNull();

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
     * sa id
     */
    public int said;

    /**
     * preshared key
     */
    public String preshared;

    /**
     * transform set to use
     */
    public secTransform transform;

    /**
     * counter
     */
    public counter cntr = new counter();

    private ipFwd lower;

    private addrIP remote;

    private ipFwdIface sendingIfc;

    private cryEncrGeneric cphrTx;

    private cryEncrGeneric cphrRx;

    private cryHashGeneric hashTx;

    private cryHashGeneric hashRx;

    private byte[] cphrKey = null;

    private int cphrSiz;

    private int hashSiz;

    /**
     * initialize context
     *
     * @param parent forwarder of encapsulated packets
     */
    public prtInlsp(ipFwd parent) {
        lower = parent;
    }

    /**
     * set target of tunnel
     *
     * @param ifc interface to source from
     * @param trg ip address of remote
     * @return false if successful, true if error happened
     */
    public boolean setEndpoints(ipFwdIface ifc, addrIP trg) {
        byte[] buf1 = preshared.getBytes();
        byte[] buf2 = new byte[0];
        for (; buf2.length < 1024;) {
            cryHashGeneric hsh = transform.getHash();
            hsh.init();
            hsh.update(buf1);
            buf1 = hsh.finish();
            if (buf1.length < 1) {
                buf1 = preshared.getBytes();
            }
            buf2 = bits.byteConcat(buf2, buf1);
        }
        cphrTx = transform.getEncr();
        cphrRx = transform.getEncr();
        byte[] res = buf2;
        buf1 = new byte[cphrTx.getKeySize()];
        buf2 = new byte[cphrTx.getBlockSize()];
        int pos = buf1.length + buf2.length;
        bits.byteCopy(res, 0, buf1, 0, buf1.length);
        bits.byteCopy(res, buf1.length, buf2, 0, buf2.length);
        cphrKey = buf1;
        cphrSiz = buf2.length;
        hashSiz = transform.getHash().getHashSize();
        buf1 = new byte[hashSiz];
        buf2 = new byte[hashSiz];
        bits.byteCopy(res, pos, buf1, 0, buf1.length);
        bits.byteCopy(res, pos, buf2, 0, buf2.length);
        hashTx = transform.getHmac(buf1);
        hashRx = transform.getHmac(buf2);
        if (sendingIfc != null) {
            lower.protoDel(this, sendingIfc, remote);
        }
        remote = trg;
        sendingIfc = ifc;
        return lower.protoAdd(this, sendingIfc, remote);
    }

    public String toString() {
        return "inlsp to " + remote;
    }

    /**
     * get hw address
     *
     * @return hw address
     */
    public addrType getHwAddr() {
        return addrMac.getRandom();
    }

    /**
     * set filter
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
        lower.protoDel(this, sendingIfc, remote);
    }

    /**
     * flap interface
     */
    public void flapped() {
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
        return sendingIfc.mtu - size;
    }

    /**
     * get bandwidth
     *
     * @return bandwidth
     */
    public long getBandwidth() {
        return sendingIfc.bandwidth;
    }

    /**
     * send packet
     *
     * @param pck packet
     */
    public void sendPack(packHolder pck) {
        pck.merge2beg();
        if (sendingIfc == null) {
            return;
        }
        synchronized (cphrTx) {
            int i = pck.msbGetW(0); // ethertype
            pck.getSkip(2);
            int len = pck.dataSize();
            int o = prtTmux.ethtyp2proto(i);
            if (o < 0) {
                return;
            }
            i = pck.dataSize() % cphrSiz;
            if (i > 0) {
                i = cphrSiz - i;
                pck.putFill(0, i, 0); // padding
                pck.putSkip(i);
                pck.merge2end();
            }
            hashTx.init();
            pck.hashData(hashTx, 0, pck.dataSize());
            byte[] hsh = hashTx.finish();
            byte[] buf = new byte[cphrSiz];
            for (i = 0; i < buf.length; i++) {
                buf[i] = (byte) bits.randomB();
            }
            cphrTx.init(cphrKey, buf, true);
            pck.encrData(cphrTx, 0, pck.dataSize());
            pck.putCopy(buf, 0, 0, buf.length);
            pck.putSkip(buf.length);
            pck.merge2beg();
            pck.putCopy(hsh, 0, 0, hsh.length);
            pck.putSkip(hsh.length);
            pck.merge2beg();
            pck.putByte(0, o); // protocol
            pck.putByte(1, 0); // version
            pck.msbPutW(2, len); // length
            pck.msbPutW(4, said); // sa id
            pck.msbPutW(6, 0); // reserved
            pck.putSkip(size);
            pck.merge2beg();
        }
        cntr.tx(pck);
        pck.putDefaults();
        if (sendingTTL >= 0) {
            pck.IPttl = sendingTTL;
        }
        if (sendingTOS >= 0) {
            pck.IPtos = sendingTOS;
        }
        if (sendingDFN >= 0) {
            pck.IPdf = sendingDFN == 1;
        }
        if (sendingFLW >= 0) {
            pck.IPtos = sendingFLW;
        }
        pck.IPprt = prot;
        pck.IPsrc.setAddr(sendingIfc.addr);
        pck.IPtrg.setAddr(remote);
        lower.protoPack(sendingIfc, null, pck);
    }

    /**
     * get protocol number
     *
     * @return number
     */
    public int getProtoNum() {
        return prot;
    }

    /**
     * close interface
     *
     * @param iface interface
     */
    public void closeUp(ipFwdIface iface) {
        upper.closeUp();
    }

    /**
     * set state
     *
     * @param iface interface
     * @param stat state
     */
    public void setState(ipFwdIface iface, state.states stat) {
        if (iface.ifwNum != sendingIfc.ifwNum) {
            return;
        }
        upper.setState(stat);
    }

    /**
     * received packet
     *
     * @param rxIfc interface
     * @param pck packet
     */
    public synchronized void recvPack(ipFwdIface rxIfc, packHolder pck) {
        int o = prtTmux.proto2ethtyp(pck.getByte(0)); // protocol
        if (o < 0) {
            logger.info("got bad protocol from " + remote);
            cntr.drop(pck, counter.reasons.badProto);
            return;
        }
        if (pck.getByte(1) != 0) { // version
            logger.info("got bad version from " + remote);
            cntr.drop(pck, counter.reasons.badVer);
            return;
        }
        int len = pck.msbGetW(2); // length
        if (pck.msbGetW(4) != said) { // said
            logger.info("got bad id from " + remote);
            cntr.drop(pck, counter.reasons.badID);
            return;
        }
        pck.getSkip(size);
        int siz = pck.dataSize();
        if (siz < (hashSiz + cphrSiz)) {
            logger.info("got too small from " + remote);
            cntr.drop(pck, counter.reasons.badLen);
            return;
        }
        if (((siz - hashSiz) % cphrSiz) != 0) {
            logger.info("got bad padding from " + remote);
            cntr.drop(pck, counter.reasons.badLen);
            return;
        }
        byte[] sum = new byte[hashSiz];
        pck.getCopy(sum, 0, 0, hashSiz);
        pck.getSkip(hashSiz);
        siz -= hashSiz;
        byte[] buf = new byte[cphrSiz];
        pck.getCopy(buf, 0, 0, buf.length);
        pck.getSkip(buf.length);
        cphrRx.init(cphrKey, buf, false);
        pck.encrData(cphrRx, 0, siz);
        siz -= cphrSiz;
        hashRx.init();
        pck.hashData(hashRx, 0, siz);
        if (bits.byteComp(sum, 0, hashRx.finish(), 0, hashSiz) != 0) {
            logger.info("got bad hash from " + remote);
            cntr.drop(pck, counter.reasons.badSum);
            return;
        }
        if (len > pck.dataSize()) {
            logger.info("got truncated from " + remote);
            cntr.drop(pck, counter.reasons.badLen);
            return;
        }
        pck.setDataSize(len);
        pck.msbPutW(0, o); // ethertype
        pck.putSkip(2);
        pck.merge2beg();
        cntr.rx(pck);
        upper.recvPack(pck);
    }

    /**
     * alert packet
     *
     * @param rxIfc interface
     * @param pck packet
     * @return false on success, true on error
     */
    public boolean alertPack(ipFwdIface rxIfc, packHolder pck) {
        return true;
    }

    /**
     * error packet
     *
     * @param err error code
     * @param rtr address
     * @param rxIfc interface
     * @param pck packet
     */
    public void errorPack(counter.reasons err, addrIP rtr, ipFwdIface rxIfc, packHolder pck) {
    }

}
