package net.freertr.clnt;

import net.freertr.addr.addrIP;
import net.freertr.ip.ipFwd;
import net.freertr.ip.ipFwdEcho;
import net.freertr.pipe.pipeDiscard;
import net.freertr.pipe.pipeSide;
import net.freertr.prt.prtIcmptun;
import net.freertr.util.bits;

/**
 * path mtu discovery
 *
 * @author matecsaba
 */
public class clntPmtudWrk {

    private final addrIP trg;

    private final ipFwd fwd;

    private final addrIP src;

    private final pipeSide pip;

    /**
     * filler byte
     */
    public int data = 0;

    /**
     * timeout for reply
     */
    public int timeout = 1000;

    /**
     * max time to run
     */
    public int timemax = 15000;

    /**
     * timeout divider
     */
    public int timediv = 10;

    /**
     * sgt to use
     */
    public int sgt = 0;

    /**
     * tos to use
     */
    public int tos = 0;

    /**
     * flow to use
     */
    public int flow = 0;

    /**
     * alert to use
     */
    public int alrt = -1;

    /**
     * ttl to use
     */
    public int ttl = 255;

    /**
     * delay to use
     */
    public int delay = 1000;

    /**
     * minimum value
     */
    public int min = 1400;

    /**
     * maximum value
     */
    public int max = 1600;

    /**
     * last good value
     */
    public int last = -1;

    /**
     * perform the discovery
     *
     * @param con pipe to use
     * @param rem address to test
     * @param vrf forwarder to use
     * @param sou source ip to use
     */
    public clntPmtudWrk(pipeSide con, addrIP rem, ipFwd vrf, addrIP sou) {
        pip = pipeDiscard.needAny(con);
        trg = rem;
        fwd = vrf;
        src = sou;
    }

    public String toString() {
        return "" + last;
    }

    /**
     * do the process
     *
     * @return null if error, otherwise guessed overhead, then succeeded payload
     * succeeded happened
     */
    public int[] doer() {
        int ovrh = prtIcmptun.adjustSize(trg);
        if (fwd == null) {
            pip.linePut("vrf not specified");
            return null;
        }
        if (trg == null) {
            return null;
        }
        pip.linePut("pmduding " + trg + ", src=" + src + ", vrf=" + fwd.vrfName + ", ovr=" + ovrh + ", len=" + min + ".." + max + ", tim=" + timeout + ", tdiv=" + timediv + ", tmax=" + timemax + ", gap=" + delay + ", ttl=" + ttl + ", tos=" + tos + ", sgt=" + sgt + ", flow=" + flow + ", fill=" + data + ", alrt=" + alrt);
        for (;;) {
            if (pip.isClosed() != 0) {
                pip.linePut("stopped");
                return null;
            }
            if (pip.ready2rx() > 0) {
                pip.linePut("keypress");
                return null;
            }
            if ((max - min) < 2) {
                break;
            }
            int mid = min + ((max - min) / 2);
            pip.strPut("trying (" + min + ".." + max + ") " + mid + " ");
            int size = mid - ovrh;
            ipFwdEcho ping = fwd.echoSendReq(src, trg, size, true, alrt, ttl, sgt, tos, flow, data, false);
            if (ping == null) {
                pip.linePut("noroute");
                return null;
            }
            for (int i = 0; i < timediv; i++) {
                if (ping.notif.totalNotifies() < 1) {
                    ping.notif.sleep(timeout / timediv);
                    continue;
                }
                break;
            }
            boolean res = false;
            for (int o = 0; o < ping.res.size(); o++) {
                if (ping.res.get(o).err != null) {
                    continue;
                }
                res = true;
                break;
            }
            if (res) {
                pip.linePut("ok");
                min = mid;
                last = mid;
            } else {
                pip.linePut("bad");
                max = mid;
            }
            if (delay > 0) {
                bits.sleep(delay);
            }
        }
        pip.linePut("finished with min=" + min + " max=" + max + " last=" + last + " guess=" + (last + ovrh));
        int[] res = new int[2];
        res[0] = ovrh;
        res[1] = last;
        return res;
    }

}
