package user;

import cfg.cfgAll;
import cfg.cfgInit;
import java.util.ArrayList;
import java.util.List;
import pipe.pipeSide;
import util.bits;
import util.cmds;
import util.debugger;
import util.extMrkLng;
import util.extMrkLngEntry;
import util.logger;
import util.verCore;

/**
 * netconf (rfc6241) handler
 *
 * @author matecsaba
 */
public class userNetconf {

    /**
     * header separator
     */
    public final static String headerEnd = "]]>]]>";

    /**
     * get-filter
     */
    public final static String getFilter = "/?xml/rpc/get/filter/";

    /**
     * get-config
     */
    public final static String getConfig = "/?xml/rpc/get-config/filter/config";

    /**
     * reply-data
     */
    public final static String replyData = "/rpc-reply/data";

    /**
     * namespace
     */
    public final static String namespace = "xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\"";

    /**
     * port
     */
    public final static int port = 830;

    private final pipeSide conn;

    private final boolean privi;

    private final boolean form;

    private final boolean echo;

    private final int sessId;

    private int currVer; // 10, 11

    private boolean need2run;

    /**
     * create handler
     *
     * @param pipe pipe to use
     * @param prv privileged
     * @param frm format response
     * @param ech echo input
     */
    public userNetconf(pipeSide pipe, boolean prv, boolean frm, boolean ech) {
        conn = pipe;
        privi = prv;
        form = frm;
        echo = ech;
        sessId = bits.randomD();
        need2run = true;
    }

    /**
     * do hello
     *
     * @return true on error, false on success
     */
    public boolean doHello() {
        currVer = 10;
        extMrkLng x = new extMrkLng();
        x.data.add(new extMrkLngEntry("/hello", "xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"", ""));
        x.data.add(new extMrkLngEntry("/hello/capabilities", "", ""));
        x.data.add(new extMrkLngEntry("/hello/capabilities/capability", "", "urn:ietf:params:netconf:base:1.0"));
        x.data.add(new extMrkLngEntry("/hello/capabilities", "", ""));
        x.data.add(new extMrkLngEntry("/hello/capabilities/capability", "", "urn:ietf:params:netconf:base:1.1"));
        x.data.add(new extMrkLngEntry("/hello/capabilities", "", ""));
        x.data.add(new extMrkLngEntry("/hello/capabilities/capability", "", "urn:ietf:params:netconf:capability:writable-running:1.0"));
        x.data.add(new extMrkLngEntry("/hello/capabilities", "", ""));
        x.data.add(new extMrkLngEntry("/hello/capabilities/capability", "", "urn:ietf:params:netconf:capability:startup:1.0"));
        x.data.add(new extMrkLngEntry("/hello/capabilities", "", ""));
        for (int i = 0; i < cfgInit.sensors.size(); i++) {
            userSensor ntry = cfgInit.sensors.get(i);
            x.data.add(new extMrkLngEntry("/hello/capabilities/capability", "", verCore.homeUrl + "yang/" + ntry.prefix + "?module=" + ntry.prefix));
            x.data.add(new extMrkLngEntry("/hello/capabilities", "", ""));
        }
        x.data.add(new extMrkLngEntry("/hello/session-id", "", "" + sessId));
        x.data.add(new extMrkLngEntry("/hello", "", ""));
        doSend(x);
        x = doRead();
        if (x == null) {
            return true;
        }
        for (int i = 0; i < x.data.size(); i++) {
            extMrkLngEntry ntry = x.data.get(i);
            String a = getName(ntry);
            if (!a.equals("/?xml/hello/capabilities/capability")) {
                continue;
            }
            if (ntry.value.equals("urn:ietf:params:netconf:base:1.1")) {
                currVer = 11;
                continue;
            }
        }
        if (debugger.userNetconfEvnt) {
            logger.debug("ver: " + currVer);
        }
        return false;
    }

    /**
     * do request m
     *
     *
     * @param req request
     * @return response, null if error
     */
    public extMrkLng doRequest(extMrkLng req) {
        extMrkLng rep = new extMrkLng();
        String rpc = "";
        int mod = 1;
        for (int p = 0; p < req.data.size(); p++) {
            extMrkLngEntry ntry = req.data.get(p);
            String a = getName(ntry);
            if (a.equals("/?xml/rpc")) {
                rpc += ntry.param;
            }
            if (a.startsWith(getConfig)) {
                String n = getName(req.data.get(p + 1));
                if (mod == 1) {
                    if (n.length() > a.length()) {
                        continue;
                    }
                } else {
                    if (n.length() < a.length()) {
                        continue;
                    }
                }
                mod = 3 - mod;
                if (mod != 2) {
                    continue;
                }
                List<String> cfg = cfgAll.getShRun(false);
                List<userFilter> sec = userFilter.text2section(cfg);
                n = a.substring(getConfig.length(), a.length());
                List<userFilter> res = new ArrayList<userFilter>();
                for (; n.length() > 0;) {
                    a = extMrkLng.unescId(n).replaceAll("/", " ");
                    res = userFilter.getSection(sec, a.trim(), false);
                    if (res.size() > 0) {
                        break;
                    }
                    int i = n.lastIndexOf("/");
                    if (i < 0) {
                        n = "";
                        break;
                    }
                    n = n.substring(0, i);
                }
                userFilter.section2xml(rep, "/rpc-reply/data" + n, res);
                rep.data.add(new extMrkLngEntry("/rpc-reply", "", ""));
                continue;
            }
            if (a.startsWith(getFilter)) {
                String n = getName(req.data.get(p + 1));
                if (mod == 1) {
                    if (n.length() > a.length()) {
                        continue;
                    }
                } else {
                    if (n.length() < a.length()) {
                        continue;
                    }
                }
                mod = 3 - mod;
                if (mod != 2) {
                    continue;
                }
                userSensor tl = getSensor(a.substring(getFilter.length(), a.length()));
                if (tl == null) {
                    continue;
                }
                rep.data.add(new extMrkLngEntry(replyData, "", ""));
                a = tl.path;
                int o = a.indexOf("/");
                rep.data.add(new extMrkLngEntry(replyData + "/" + a.substring(0, o), "xmlns=\"" + verCore.homeUrl + "yang/" + tl.prefix + "\"", ""));
                tl.getReportNetConf(rep, replyData + "/");
                rep.data.add(new extMrkLngEntry(replyData, "", ""));
                rep.data.add(new extMrkLngEntry("/rpc-reply", "", ""));
                continue;
            }
            if (a.equals("/?xml/rpc/close-session")) {
                rep.data.add(new extMrkLngEntry("/rpc-reply/ok", "", ""));
                rep.data.add(new extMrkLngEntry("/rpc-reply", "", ""));
                need2run = false;
                continue;
            }
        }
        rep.data.add(0, new extMrkLngEntry("/rpc-reply", rpc, ""));
        rep.data.add(new extMrkLngEntry("/rpc-reply", "", ""));
        return rep;
    }

    private static userSensor getSensor(String a) {
        for (int i = 0; i < cfgInit.sensors.size(); i++) {
            userSensor tl = cfgInit.sensors.get(i);
            if (a.startsWith(tl.path)) {
                return tl;
            }
        }
        return null;
    }

    private static String getName(extMrkLngEntry ntry) {
        return ntry.name.replaceAll("/nc:", "/");
    }

    private static void dumpXml(cmds cmd, extMrkLng x) {
        List<String> l = x.show();
        for (int i = 0; i < l.size(); i++) {
            cmd.error(l.get(i));
        }
    }

    /**
     * read request
     *
     * @return request, null if error
     */
    public extMrkLng doRead() {
        String s = "";
        if (currVer < 11) {
            for (;;) {
                if (conn.isClosed() != 0) {
                    return null;
                }
                String a = conn.strGet(1);
                if (a == null) {
                    continue;
                }
                if (a.length() < 1) {
                    continue;
                }
                if (echo) {
                    conn.strPut(a);
                }
                s += a;
                if (s.endsWith(headerEnd)) {
                    break;
                }
            }
            if (debugger.userNetconfEvnt) {
                logger.debug("rx: " + s);
            }
            s = s.substring(0, s.length() - headerEnd.length());
        } else {
            pipeSide.modTyp sav = conn.lineRx;
            for (;;) {
                if (conn.isClosed() != 0) {
                    return null;
                }
                conn.lineRx = pipeSide.modTyp.modeLF;
                String a = conn.lineGet(echo ? 0x32 : 1);
                conn.lineRx = sav;
                if (a.length() < 1) {
                    continue;
                }
                if (a.equals("##")) {
                    break;
                }
                if (!a.startsWith("#")) {
                    continue;
                }
                int i = bits.str2num(a.substring(1, a.length()));
                a = conn.strGet(i);
                if (debugger.userNetconfEvnt) {
                    logger.debug("rx: " + a);
                }
                if (a == null) {
                    continue;
                }
                s += a.replaceAll("\r", "\n");
            }
        }
        extMrkLng x = new extMrkLng();
        if (x.fromString(s)) {
            return null;
        }
        return x;
    }

    private void doSend(String a) {
        if (debugger.userNetconfEvnt) {
            logger.debug("tx: " + a);
        }
        if (currVer > 10) {
            conn.linePut("");
            conn.linePut("#" + (a.length() + 1));
        }
        conn.linePut(a);
    }

    /**
     * send response
     *
     * @param x xml
     */
    public void doSend(extMrkLng x) {
        pipeSide.modTyp sav = conn.lineTx;
        conn.lineTx = pipeSide.modTyp.modeLF;
        doSend(extMrkLng.header);
        if (form) {
            List<String> l = x.toXMLlst();
            for (int i = 0; i < l.size(); i++) {
                doSend(l.get(i));
            }
        } else {
            doSend(x.toXMLstr());
        }
        if (currVer > 10) {
            conn.linePut("");
            conn.linePut("##");
        } else {
            conn.strPut(headerEnd);
        }
        conn.lineTx = sav;
    }

    /**
     * do work
     */
    public void doClose() {
        extMrkLng x = new extMrkLng();
        x.data.add(new extMrkLngEntry("/rpc", namespace + " message-id=\"" + bits.randomD() + "\"", ""));
        x.data.add(new extMrkLngEntry("/rpc/close-session", "", ""));
        doSend(x);
        doRead();
    }

    /**
     * do work
     */
    public void doServer() {
        if (doHello()) {
            return;
        }
        for (;;) {
            if (!need2run) {
                break;
            }
            extMrkLng x = doRead();
            if (x == null) {
                break;
            }
            x = doRequest(x);
            doSend(x);
        }
    }

    /**
     * do work
     *
     * @param cmd console
     * @param mod mode
     * @param path path
     * @param ns namespace
     * @return true on error, false on success
     */
    public boolean doClient(cmds cmd, String mod, String path, String ns) {
        extMrkLng x = new extMrkLng();
        x.data.add(new extMrkLngEntry("/rpc", namespace + " message-id=\"" + bits.randomD() + "\"", ""));
        int i = path.indexOf("/");
        x.data.add(new extMrkLngEntry("/rpc/" + mod + "/filter/" + path.substring(0, i), "xmlns=\"" + ns + "\"", ""));
        x.data.add(new extMrkLngEntry("/rpc/" + mod + "/filter/" + path, "", ""));
        cmd.error("request");
        dumpXml(cmd, x);
        doSend(x);
        x = doRead();
        if (x == null) {
            return true;
        }
        cmd.error("reply");
        dumpXml(cmd, x);
        return false;
    }

}
