package org.freertr.clnt;

import org.freertr.addr.addrIP;
import org.freertr.cfg.cfgAll;
import org.freertr.pipe.pipeLine;
import org.freertr.pipe.pipeSetting;
import org.freertr.pipe.pipeSide;
import org.freertr.serv.servGeneric;
import org.freertr.serv.servIrc;
import org.freertr.user.userExec;
import org.freertr.user.userRead;
import org.freertr.util.bits;
import org.freertr.util.cmds;
import org.freertr.util.logger;

/**
 * internet relay chat (rfc2812) client
 *
 * @author matecsaba
 */
public class clntIrc implements Runnable {

    private String hostName;

    private String chanName;

    private pipeSide pipe;

    private boolean running;

    /**
     * create logger
     *
     * @param hst host where to log
     * @param chan name of channel
     */
    public clntIrc(String hst, String chan) {
        hostName = hst;
        chanName = chan;
        running = hostName != null;
    }

    public String toString() {
        if (hostName == null) {
            return "";
        }
        return hostName + " " + chanName;
    }

    /**
     * stop logging
     */
    public void logStop() {
        running = false;
        if (pipe != null) {
            pipe.setClose();
        }
        pipe = null;
    }

    /**
     * start logging
     */
    public void logStart() {
        if (!running) {
            return;
        }
        new Thread(this).start();
    }

    /**
     * log one message
     *
     * @param s message to log
     */
    public void logMsg(String s) {
        if (pipe == null) {
            return;
        }
        byte[] buf = str2lin("PRIVMSG " + chanName + " :" + s);
        if (pipe == null) {
            return;
        }
        pipe.nonBlockPut(buf, 0, buf.length);
    }

    public void run() {
        for (;;) {
            if (!running) {
                break;
            }
            try {
                workDoer();
            } catch (Exception e) {
                logger.traceback(e);
            }
            bits.sleep(1000);
        }
    }

    private static byte[] str2lin(String s) {
        byte[] buf = new byte[1];
        buf[0] = 10;
        return bits.byteConcat(s.getBytes(), buf);
    }

    private void workDoer() {
        if (pipe != null) {
            pipe.setClose();
        }
        addrIP trg = clntDns.justResolv(hostName, 0);
        if (trg == null) {
            return;
        }
        clntProxy prx = cfgAll.getClntPrx(logger.logProxy);
        if (prx == null) {
            return;
        }
        pipe = prx.doConnect(servGeneric.protoTcp, trg, new servIrc().srvPort(), "irc");
        if (pipe == null) {
            return;
        }
        pipe.setTime(120000);
        pipe.lineRx = pipeSide.modTyp.modeCRtryLF;
        pipe.lineTx = pipeSide.modTyp.modeLF;
        pipe.linePut("CAP LS");
        pipe.linePut("NICK " + cfgAll.hostName);
        pipe.linePut("CAP END");
        pipe.linePut("JOIN " + chanName);
        logger.warn("neighbor " + hostName + " up");
        int cnt = 0;
        for (;;) {
            bits.sleep(1000);
            if (!running) {
                break;
            }
            if (pipe.isClosed() != 0) {
                break;
            }
            int i = pipe.ready2rx();
            String cmd = "";
            if (i > 0) {
                cmd = pipe.lineGet(0x11);
            }
            if (cmd.length() > 0) {
                doCmd(cmd);
            }
            cnt = (cnt + 1) % 30;
            if (cnt == 0) {
                pipe.linePut("PING :" + hostName);
            }
        }
        logger.warn("neighbor " + hostName + " down");
    }

    private void doCmd(String cmd) {
        cmds c = new cmds("irc", cmd);
        c.word();
        c.word();
        c.word();
        pipeLine pipE = new pipeLine(32768, false);
        pipeSide pip = pipE.getSide();
        userRead rdr = new userRead(pip, null);
        pip.settingsPut(pipeSetting.height, 0);
        userExec exe = new userExec(pip, rdr);
        pip.lineTx = pipeSide.modTyp.modeCRLF;
        pip.lineRx = pipeSide.modTyp.modeCRtryLF;
        pip.setTime(120000);
        cmd = c.getRemaining();
        if (cmd.length() < 1) {
            return;
        }
        cmd = cmd.substring(1, cmd.length());
        String s = exe.repairCommand(cmd);
        if (s.length() < 1) {
            return;
        }
        try {
            exe.executeCommand(s);
        } catch (Exception e) {
            logger.traceback(e);
        }
        pip = pipE.getSide();
        pip.lineTx = pipeSide.modTyp.modeCRLF;
        pip.lineRx = pipeSide.modTyp.modeCRtryLF;
        pip.setTime(1000);
        pip.setClose();
        for (;;) {
            if (pip.ready2rx() < 1) {
                break;
            }
            String a = pip.lineGet(1);
            if (a.length() < 1) {
                continue;
            }
            byte[] buf = str2lin("PRIVMSG " + chanName + " :\"" + a + "\"");
            pipe.blockingPut(buf, 0, buf.length);
        }
    }

}
