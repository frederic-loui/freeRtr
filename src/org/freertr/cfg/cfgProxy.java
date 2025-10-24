package org.freertr.cfg;

import java.util.ArrayList;
import java.util.List;
import org.freertr.auth.authLocal;
import org.freertr.clnt.clntProxy;
import org.freertr.enc.encBase64;
import org.freertr.serv.servGeneric;
import org.freertr.tab.tabGen;
import org.freertr.user.userFilter;
import org.freertr.user.userHelp;
import org.freertr.util.bits;
import org.freertr.util.cmds;

/**
 * proxy profile configuration
 *
 * @author matecsaba
 */
public class cfgProxy implements Comparable<cfgProxy>, cfgGeneric {

    /**
     * name of connection map
     */
    public String name;

    /**
     * description of access list
     */
    public String description;

    /**
     * proxy configuration
     */
    public final clntProxy proxy;

    /**
     * defaults text
     */
    public final static userFilter[] defaultF = {
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "description", null),
        new userFilter("proxy-profile .*", cmds.tabulator + "protocol local", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "security", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "pubkey", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "username", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "password", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "recursive", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "vrf", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "tos", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "ttl", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "source", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "target", null),
        new userFilter("proxy-profile .*", cmds.tabulator + cmds.negated + cmds.tabulator + "port", null),
        new userFilter("proxy-profile .*", cmds.tabulator + "prefer none", null)
    };

    public int compareTo(cfgProxy o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }

    public String toString() {
        return "proxy " + name;
    }

    /**
     * create new profile
     *
     * @param nam name of interface
     */
    public cfgProxy(String nam) {
        name = nam;
        proxy = new clntProxy(nam);
    }

    public void getHelp(userHelp l) {
        l.add(null, false, 1, new int[]{3, -1}, "description", "specify description");
        l.add(null, false, 3, new int[]{3, -1}, "<str>", "text");
        l.add(null, false, 1, new int[]{2}, "rename", "rename this proxy");
        l.add(null, false, 2, new int[]{-1}, "<str>", "set new name");
        l.add(null, false, 1, new int[]{2}, "protocol", "specify protocol to use");
        l.add(null, false, 2, new int[]{-1}, "local", "select local vrf");
        l.add(null, false, 2, new int[]{-1}, "socks4", "select socks v4");
        l.add(null, false, 2, new int[]{-1}, "socks5", "select socks v5");
        l.add(null, false, 2, new int[]{-1}, "websock", "select websocket");
        l.add(null, false, 2, new int[]{-1}, "http", "select http connect");
        l.add(null, false, 2, new int[]{-1}, "hostos", "select host os stack");
        l.add(null, false, 1, new int[]{2}, "security", "select security protocol");
        servGeneric.getSecProts(l, 2, new int[]{-1});
        l.add(null, false, 1, new int[]{2}, "pubkey", "public key to expect");
        l.add(null, false, 2, new int[]{2, -1}, "<str>", "public key");
        l.add(null, false, 1, new int[]{2}, "username", "username to send");
        l.add(null, false, 2, new int[]{-1}, "<str>", "username");
        l.add(null, false, 1, new int[]{2}, "password", "password to send");
        l.add(null, false, 2, new int[]{-1}, "<str>", "password");
        l.add(null, false, 1, new int[]{2}, "recursive", "name of profile to use");
        l.add(null, false, 2, new int[]{-1}, "<name:prx>", "profile name");
        l.add(null, false, 1, new int[]{2}, "vrf", "name of vrf to find target in");
        l.add(null, false, 2, new int[]{-1}, "<name:vrf>", "vrf name");
        l.add(null, false, 1, new int[]{2}, "source", "name of source interface");
        l.add(null, false, 2, new int[]{-1}, "<name:ifc>", "interface name");
        l.add(null, false, 1, new int[]{2}, "target", "specify address of proxy");
        l.add(null, false, 2, new int[]{-1}, "<str>", "name or address");
        l.add(null, false, 1, new int[]{2}, "port", "specify port of proxy");
        l.add(null, false, 2, new int[]{-1}, "<num>", "port number");
        l.add(null, false, 1, new int[]{2}, "tos", "specify tos value");
        l.add(null, false, 2, new int[]{-1}, "<num>", "value");
        l.add(null, false, 1, new int[]{2}, "ttl", "specify ttl value");
        l.add(null, false, 2, new int[]{-1}, "<num>", "value");
        l.add(null, false, 1, new int[]{2}, "prefer", "prefer ip protocol");
        l.add(null, false, 2, new int[]{-1}, "none", "default");
        l.add(null, false, 2, new int[]{-1}, "ipv4", "ipv4");
        l.add(null, false, 2, new int[]{-1}, "ipv6", "ipv6");
    }

    public String getPrompt() {
        return "proxy";
    }

    public List<String> getShRun(int filter) {
        List<String> l = new ArrayList<String>();
        l.add("proxy-profile " + name);
        cmds.cfgLine(l, description == null, cmds.tabulator, "description", "" + description);
        l.add(cmds.tabulator + "protocol " + clntProxy.type2string(proxy.prxProto));
        if (proxy.pubkey == null) {
            l.add(cmds.tabulator + "no pubkey");
        } else {
            l.add(cmds.tabulator + "pubkey " + encBase64.encodeBytes(proxy.pubkey));
        }
        cmds.cfgLine(l, proxy.secProto == 0, cmds.tabulator, "security", servGeneric.proto2string(proxy.secProto));
        cmds.cfgLine(l, proxy.username == null, cmds.tabulator, "username", proxy.username);
        cmds.cfgLine(l, proxy.password == null, cmds.tabulator, "password", authLocal.passwdEncode(proxy.password, (filter & 2) != 0));
        if (proxy.lowProxy == null) {
            l.add(cmds.tabulator + "no recursive");
        } else {
            l.add(cmds.tabulator + "recursive " + proxy.lowProxy.name);
        }
        if (proxy.vrf == null) {
            l.add(cmds.tabulator + "no vrf");
        } else {
            l.add(cmds.tabulator + "vrf " + proxy.vrf.name);
        }
        if (proxy.srcIfc == null) {
            l.add(cmds.tabulator + "no source");
        } else {
            l.add(cmds.tabulator + "source " + proxy.srcIfc.name);
        }
        cmds.cfgLine(l, proxy.target == null, cmds.tabulator, "target", proxy.target);
        cmds.cfgLine(l, proxy.port == 0, cmds.tabulator, "port", "" + proxy.port);
        cmds.cfgLine(l, proxy.typOsrv < 0, cmds.tabulator, "tos", "" + proxy.typOsrv);
        cmds.cfgLine(l, proxy.tim2liv < 0, cmds.tabulator, "ttl", "" + proxy.tim2liv);
        String a;
        if (proxy.prefer == 0) {
            a = "none";
        } else {
            a = "ipv" + proxy.prefer;
        }
        l.add(cmds.tabulator + "prefer " + a);
        l.add(cmds.tabulator + cmds.finish);
        l.add(cmds.comment);
        if ((filter & 1) == 0) {
            return l;
        }
        return userFilter.filterText(l, defaultF);
    }

    public void doCfgStr(cmds cmd) {
        String s = cmd.word();
        if (s.equals("description")) {
            description = cmd.getRemaining();
            return;
        }
        if (s.equals("rename")) {
            s = cmd.word();
            cfgPrfxlst v = cfgAll.prfxFind(s, false);
            if (v != null) {
                cmd.error("already exists");
                return;
            }
            name = s;
            proxy.name = s;
            return;
        }
        if (s.equals("protocol")) {
            proxy.prxProto = clntProxy.string2type(cmd.word());
            return;
        }
        if (s.equals("security")) {
            proxy.secProto = servGeneric.string2proto(cmd.word());
            return;
        }
        if (s.equals("pubkey")) {
            proxy.pubkey = encBase64.decodeBytes(cmd.getRemaining());
            return;
        }
        if (s.equals("username")) {
            proxy.username = cmd.getRemaining();
            return;
        }
        if (s.equals("password")) {
            proxy.password = authLocal.passwdDecode(cmd.getRemaining());
            return;
        }
        if (s.equals("vrf")) {
            cfgVrf vrf = cfgAll.vrfFind(cmd.word(), false);
            if (vrf == null) {
                cmd.error("no such vrf");
                return;
            }
            proxy.vrf = vrf;
            return;
        }
        if (s.equals("source")) {
            cfgIfc ifc = cfgAll.ifcFind(cmd.word(), 0);
            if (ifc == null) {
                cmd.error("no such interface");
                return;
            }
            proxy.srcIfc = ifc;
            return;
        }
        if (s.equals("target")) {
            proxy.target = cmd.getRemaining();
            return;
        }
        if (s.equals("port")) {
            proxy.port = bits.str2num(cmd.getRemaining());
            return;
        }
        if (s.equals("tos")) {
            proxy.typOsrv = bits.str2num(cmd.getRemaining());
            return;
        }
        if (s.equals("ttl")) {
            proxy.tim2liv = bits.str2num(cmd.getRemaining());
            return;
        }
        if (s.equals("recursive")) {
            cfgProxy prx = cfgAll.proxyFind(cmd.word(), false);
            if (prx == null) {
                cmd.error("no such profile");
                return;
            }
            proxy.lowProxy = prx.proxy;
            return;
        }
        if (s.equals("prefer")) {
            s = cmd.word();
            if (s.equals("ipv4")) {
                proxy.prefer = 4;
            }
            if (s.equals("ipv6")) {
                proxy.prefer = 6;
            }
            if (s.equals("none")) {
                proxy.prefer = 0;
            }
            return;
        }
        if (!s.equals(cmds.negated)) {
            cmd.badCmd();
            return;
        }
        s = cmd.word();
        if (s.equals("description")) {
            description = null;
            return;
        }
        if (s.equals("protocol")) {
            proxy.prxProto = clntProxy.proxyType.local;
            return;
        }
        if (s.equals("security")) {
            proxy.secProto = 0;
            return;
        }
        if (s.equals("pubkey")) {
            proxy.pubkey = null;
            return;
        }
        if (s.equals("username")) {
            proxy.username = null;
            return;
        }
        if (s.equals("password")) {
            proxy.password = null;
            return;
        }
        if (s.equals("vrf")) {
            proxy.vrf = null;
            return;
        }
        if (s.equals("source")) {
            proxy.srcIfc = null;
            return;
        }
        if (s.equals("target")) {
            proxy.target = null;
            return;
        }
        if (s.equals("tos")) {
            proxy.typOsrv = -1;
            return;
        }
        if (s.equals("ttl")) {
            proxy.tim2liv = -1;
            return;
        }
        if (s.equals("port")) {
            proxy.port = 0;
            return;
        }
        if (s.equals("recursive")) {
            proxy.lowProxy = null;
            return;
        }
        cmd.badCmd();
    }

    /**
     * get vrf
     *
     * @return vrf
     */
    public cfgVrf getVrf() {
        return proxy.vrf;
    }

    /**
     * get interface
     *
     * @return interface
     */
    public cfgIfc getIfc() {
        return proxy.srcIfc;
    }

}
