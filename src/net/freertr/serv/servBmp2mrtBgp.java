package net.freertr.serv;

import net.freertr.addr.addrIP;
import net.freertr.pack.packHolder;
import net.freertr.pipe.pipeSide;
import net.freertr.prt.prtGenConn;
import net.freertr.rtr.rtrBgpMon;
import net.freertr.rtr.rtrBgpNeigh;
import net.freertr.rtr.rtrBgpSpeak;
import net.freertr.rtr.rtrBgpUtil;
import net.freertr.tab.tabRouteUtil;
import net.freertr.util.bits;

/**
 * bmp server connection
 *
 * @author matecsaba
 */
public class servBmp2mrtBgp extends servBmp2mrtLstn {

    /**
     * create instance
     *
     * @param pip pipe to use
     * @param prnt parent to use
     * @param id connection to use
     */
    public servBmp2mrtBgp(pipeSide pip, servBmp2mrt prnt, prtGenConn id) {
        super(pip, prnt, id);
    }

    protected void doStart() {
        int safi;
        if (conn.peerAddr.isIPv4()) {
            safi = rtrBgpUtil.safiIp4uni;
        } else {
            safi = rtrBgpUtil.safiIp6uni;
        }
        rtrBgpNeigh nei = new rtrBgpNeigh(null, conn.peerAddr.copyBytes());
        nei.localAs = lower.listenAsn;
        nei.addrFams = safi;
        rtrBgpSpeak spk = new rtrBgpSpeak(null, nei, pipe);
        packHolder pck = new packHolder(true, true);
        byte[] buf = new byte[4];
        bits.msbPutD(buf, 0, nei.localAs);
        rtrBgpUtil.placeCapability(pck, false, rtrBgpUtil.capa32bitAsNum, buf);
        buf = new byte[4];
        bits.msbPutD(buf, 0, nei.addrFams);
        rtrBgpUtil.placeCapability(pck, false, rtrBgpUtil.capaMultiProto, buf);
        pck.merge2beg();
        pck.putByte(0, rtrBgpUtil.version);
        pck.msbPutW(1, tabRouteUtil.asNum16bit(nei.localAs));
        pck.msbPutW(3, nei.holdTimer / 1000);
        buf = conn.iface.addr.getBytes();
        pck.putCopy(buf, 0, 5, buf.length);
        pck.putByte(9, pck.dataSize());
        pck.putSkip(10);
        pck.merge2beg();
        spk.packSend(pck, rtrBgpUtil.msgOpen);
        spk.sendKeepAlive();
    }

    protected void doDump(int as, addrIP from, addrIP peer, int typ, packHolder pck) {
        if (typ != rtrBgpMon.typMon) {
            return;
        }
        pck = pck.copyBytes(true, true);
        pck.pipeSend(pipe, 0, pck.dataSize(), 1);
    }

}