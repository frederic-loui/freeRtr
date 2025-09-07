package org.freertr.pack;

import org.freertr.addr.addrIP;
import org.freertr.ip.ipFwdIface;
import org.freertr.pipe.pipeLine;
import org.freertr.pipe.pipeSide;
import org.freertr.prt.prtGen;
import org.freertr.util.bits;

/**
 * real time (control) protocol (rfc3550) packet
 *
 * @author matecsaba
 */
public class packRtp {

    /**
     * create instance
     */
    public packRtp() {
    }

    private pipeSide connData;

    private pipeSide connCtrl;

    private long lastCtrl;

    /**
     * packets sent
     */
    public int packTx;

    /**
     * bytes sent
     */
    public int byteTx;

    /**
     * packets received
     */
    public int packRx;

    /**
     * bytes received
     */
    public int byteRx;
    
    /**
     * type sent
     */
    public int typeTx;
    
    /**
     * type received
     */
    public int typeRx;
    
    /**
     * sync sent
     */
    public int syncTx;
    
    /**
     * sync received
     */
    public int syncRx;

    /**
     * create connection
     *
     * @param handler connection handler
     * @param sample pipeline to clone from
     * @param locI local interface, null means pick up one
     * @param locP local port, 0 means pick up one
     * @param remA remote address
     * @param remP remote port
     * @return false on success, true on error
     */
    public boolean startConnect(prtGen handler, pipeLine sample, ipFwdIface locI, int locP, addrIP remA, int remP) {
        locP &= 0xffffe;
        remP &= 0xffffe;
        connData = handler.streamConnect(sample, locI, locP, remA, remP, "rtp", -1, null, -1, -1);
        connCtrl = handler.streamConnect(sample, locI, locP + 1, remA, remP + 1, "rtcp", -1, null, -1, -1);
        if ((connData == null) || (connCtrl == null)) {
            setClose();
            return true;
        }
        connData.setTime(300000);
        connCtrl.setTime(300000);
        lastCtrl = bits.getTime();
        return false;
    }

    /**
     * close this side
     */
    public void setClose() {
        if (connData != null) {
            connData.setClose();
        }
        if (connCtrl != null) {
            connCtrl.setClose();
        }
    }

    /**
     * test if the any side of pipe was closed
     *
     * @return 0=no, 1=this side, 1=other side, 3=both sides
     */
    public int isClosed() {
        if ((connData == null) || (connCtrl == null)) {
            return 3;
        }
        return connData.isClosed();
    }

    /**
     * send one packet
     *
     * @param pck packet holder
     */
    public void sendPack(packHolder pck) {
        long tim = bits.getTime();
        pck.merge2beg();
        packTx++;
        byteTx += pck.dataSize();
        pck.putByte(0, 0x80);
        pck.putByte(1, pck.RTPtyp);
        pck.msbPutW(2, packTx - 1);
        pck.msbPutD(4, byteTx);
        pck.msbPutD(8, pck.RTPsrc);
        pck.putSkip(12);
        pck.merge2beg();
        pck.pipeSend(connData, 0, pck.dataSize(), 2);
        if ((tim - lastCtrl) < 30000) {
            return;
        }
        lastCtrl = tim;
        int id = pck.RTPsrc;
        pck.clear();
        pck.putByte(0, 0x80);
        pck.putByte(1, 200);
        pck.msbPutW(2, 6); // (size/4)-1
        pck.msbPutD(4, id);
        pck.msbPutQ(8, packNtp.encode(tim));
        pck.msbPutD(16, byteTx);
        pck.msbPutD(20, packTx);
        pck.msbPutD(24, byteTx);
        pck.putSkip(28);
        pck.merge2beg();
        pck.pipeSend(connCtrl, 0, pck.dataSize(), 2);
    }

    /**
     * receive one packet
     *
     * @param pck buffer to use
     * @param blocking blocking mode
     * @return bytes received
     */
    public int recvPack(packHolder pck, boolean blocking) {
        for (;;) {
            pck.clear();
            if (pck.pipeRecv(connCtrl, 0, -1, 142) < 1) {
                break;
            }
        }
        pck.clear();
        int i;
        if (blocking) {
            i = 143;
        } else {
            i = 142;
        }
        i = pck.pipeRecv(connData, 0, -1, i);
        if (i < 1) {
            return i;
        }
        int ver = pck.getByte(0);
        pck.RTPtyp = pck.getByte(1);
        packRx = pck.msbGetW(2);
        byteRx = pck.msbGetD(4);
        pck.RTPsrc = pck.msbGetD(8);
        if ((ver & 0xf0) != 0x80) {
            return pipeLine.tryLater;
        }
        pck.getSkip(12 + ((ver & 0xf) * 4));
        i = pck.dataSize();
        if (i < 1) {
            return pipeLine.tryLater;
        }
        return i;
    }

}
