package org.freertr.rtr;

import java.util.List;
import org.freertr.addr.addrIP;
import org.freertr.addr.addrIPv4;
import org.freertr.addr.addrIPv6;
import org.freertr.addr.addrPrefix;
import org.freertr.cfg.cfgAll;
import org.freertr.cfg.cfgPrfxlst;
import org.freertr.cfg.cfgRoump;
import org.freertr.cfg.cfgRouplc;
import org.freertr.ip.ipFwdIface;
import org.freertr.pack.packHolder;
import org.freertr.prt.prtGenConn;
import org.freertr.tab.tabListing;
import org.freertr.tab.tabPrfxlstN;
import org.freertr.tab.tabRoute;
import org.freertr.tab.tabRouteAttr;
import org.freertr.tab.tabRouteEntry;
import org.freertr.tab.tabRtrmapN;
import org.freertr.tab.tabRtrplcN;
import org.freertr.user.userHelp;
import org.freertr.util.bits;
import org.freertr.util.cmds;
import org.freertr.util.debugger;
import org.freertr.util.logger;

/**
 * olsr interface
 *
 * @author matecsaba
 */
public class rtrOlsrIface implements Comparable<rtrOlsrIface> {

    /**
     * hello interval
     */
    public int helloTimer = 5000;

    /**
     * hello hold time
     */
    public int helloHold = 3 * helloTimer;

    /**
     * advertisement interval
     */
    public int advertTimer = 30000;

    /**
     * advertisement hold time
     */
    public int advertHold = 3 * advertTimer;

    /**
     * time to wait between packets
     */
    public int interPackTime = 20;

    /**
     * willingness to forward
     */
    public int willingness = 7;

    /**
     * default distance
     */
    public int distance = 140;

    /**
     * default metric in
     */
    public int metricIn = 1;

    /**
     * default metric out
     */
    public int metricOut = 0;

    /**
     * bfd enabled
     */
    public boolean bfdTrigger;

    /**
     * not advertise routes learned from interface back
     */
    public boolean splitHorizon = true;

    /**
     * link quality mode
     */
    public boolean lqMode = true;

    /**
     * advertise default route
     */
    public boolean defOrigin = false;

    /**
     * suppress interface address
     */
    public boolean suppressAddr = false;

    /**
     * unsuppress interface address
     */
    public boolean unsuppressAddr = false;

    /**
     * check neighbor address is connected
     */
    public boolean connectedCheck = true;

    /**
     * ingress prefix list
     */
    public tabListing<tabPrfxlstN, addrIP> prflstIn;

    /**
     * egress prefix list
     */
    public tabListing<tabPrfxlstN, addrIP> prflstOut;

    /**
     * ingress route map
     */
    public tabListing<tabRtrmapN, addrIP> roumapIn;

    /**
     * egress route map
     */
    public tabListing<tabRtrmapN, addrIP> roumapOut;

    /**
     * ingress route policy
     */
    public tabListing<tabRtrplcN, addrIP> roupolIn;

    /**
     * egress route policy
     */
    public tabListing<tabRtrplcN, addrIP> roupolOut;

    /**
     * the interface this works on
     */
    protected final ipFwdIface iface;

    /**
     * the udp connection it uses to multicast
     */
    protected prtGenConn conn;

    /**
     * the lower layer
     */
    protected final rtrOlsr lower;

    /**
     * packet sequence number
     */
    protected int pckSeq;

    /**
     * message sequence number
     */
    protected int msgSeq;

    /**
     * neighbor sequence number
     */
    protected int neiSeq;

    /**
     * last advertised time
     */
    protected long advTim;

    /**
     * prefixes advertised on interface
     */
    public tabRoute<addrIP> advert;

    /**
     * create one instance
     *
     * @param parent the olsr protocol
     * @param ifc the ip interface to work on
     */
    public rtrOlsrIface(rtrOlsr parent, ipFwdIface ifc) {
        lower = parent;
        iface = ifc;
        pckSeq = bits.randomW();
        msgSeq = bits.randomW();
        neiSeq = bits.randomW();
        advert = new tabRoute<addrIP>("olsr");
    }

    /**
     * unregister from udp
     */
    public void unregister2udp() {
        lower.udpCore.listenStop(iface, rtrOlsr.port, null, 0);
        conn.setClosing();
    }

    /**
     * register to udp
     */
    public void register2udp() {
        addrIP adr = new addrIP();
        if (iface.addr.isIPv4()) {
            adr.fromString("255.255.255.255");
        } else {
            adr.fromString("ff02::6d");
        }
        lower.udpCore.packetListen(lower, iface, rtrOlsr.port, null, 0, "olsr", -1, null, -1, -1);
        conn = lower.udpCore.packetConnect(lower, iface, rtrOlsr.port, adr, rtrOlsr.port, "olsr", -1, null, -1, -1);
        if (conn == null) {
            return;
        }
        conn.timeout = 0;
    }

    public String toString() {
        return "olsr on " + iface;
    }

    public int compareTo(rtrOlsrIface o) {
        if (iface.ifwNum < o.iface.ifwNum) {
            return -1;
        }
        if (iface.ifwNum > o.iface.ifwNum) {
            return +1;
        }
        return 0;
    }

    /**
     * get configuration
     *
     * @param l list to add
     * @param beg beginning
     */
    public void routerGetConfig(List<String> l, String beg) {
        l.add(cmds.tabulator + beg + "enable");
        cmds.cfgLine(l, !bfdTrigger, cmds.tabulator, beg + "bfd", "");
        cmds.cfgLine(l, !splitHorizon, cmds.tabulator, beg + "split-horizon", "");
        cmds.cfgLine(l, !lqMode, cmds.tabulator, beg + "lq-mode", "");
        cmds.cfgLine(l, !defOrigin, cmds.tabulator, beg + "default-originate", "");
        cmds.cfgLine(l, !suppressAddr, cmds.tabulator, beg + "suppress-prefix", "");
        cmds.cfgLine(l, !unsuppressAddr, cmds.tabulator, beg + "unsuppress-prefix", "");
        cmds.cfgLine(l, !connectedCheck, cmds.tabulator, beg + "verify-source", "");
        l.add(cmds.tabulator + beg + "distance " + distance);
        l.add(cmds.tabulator + beg + "metric-in " + metricIn);
        l.add(cmds.tabulator + beg + "metric-out " + metricOut);
        l.add(cmds.tabulator + beg + "willingness " + willingness);
        l.add(cmds.tabulator + beg + "packet-timer " + interPackTime);
        l.add(cmds.tabulator + beg + "hello-timer " + helloTimer);
        l.add(cmds.tabulator + beg + "hello-hold " + helloHold);
        l.add(cmds.tabulator + beg + "advertise-timer " + advertTimer);
        l.add(cmds.tabulator + beg + "advertise-hold " + advertHold);
        cmds.cfgLine(l, prflstIn == null, cmds.tabulator, beg + "prefix-list-in", "" + prflstIn);
        cmds.cfgLine(l, prflstOut == null, cmds.tabulator, beg + "prefix-list-out", "" + prflstOut);
        cmds.cfgLine(l, roumapIn == null, cmds.tabulator, beg + "route-map-in", "" + roumapIn);
        cmds.cfgLine(l, roumapOut == null, cmds.tabulator, beg + "route-map-out", "" + roumapOut);
        cmds.cfgLine(l, roupolIn == null, cmds.tabulator, beg + "route-policy-in", "" + roupolIn);
        cmds.cfgLine(l, roupolOut == null, cmds.tabulator, beg + "route-policy-out", "" + roupolOut);
    }

    /**
     * do configuration
     *
     * @param a command
     * @param cmd parameters
     */
    public void routerDoConfig(String a, cmds cmd) {
        if (a.equals("bfd")) {
            bfdTrigger = true;
            return;
        }
        if (a.equals("default-originate")) {
            defOrigin = true;
            return;
        }
        if (a.equals("suppress-prefix")) {
            suppressAddr = true;
            return;
        }
        if (a.equals("unsuppress-prefix")) {
            unsuppressAddr = true;
            return;
        }
        if (a.equals("verify-source")) {
            connectedCheck = true;
            return;
        }
        if (a.equals("split-horizon")) {
            splitHorizon = true;
            return;
        }
        if (a.equals("lq-mode")) {
            lqMode = true;
            return;
        }
        if (a.equals("distance")) {
            distance = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("willingness")) {
            willingness = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("metric-in")) {
            metricIn = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("metric-out")) {
            metricOut = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("packet-timer")) {
            interPackTime = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("hello-timer")) {
            helloTimer = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("hello-hold")) {
            helloHold = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("advertise-timer")) {
            advertTimer = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("advertise-hold")) {
            advertHold = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("prefix-list-in")) {
            cfgPrfxlst ntry = cfgAll.prfxFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such prefix list");
                return;
            }
            prflstIn = ntry.prflst;
            return;
        }
        if (a.equals("prefix-list-out")) {
            cfgPrfxlst ntry = cfgAll.prfxFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such prefix list");
                return;
            }
            prflstOut = ntry.prflst;
            return;
        }
        if (a.equals("route-map-in")) {
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return;
            }
            roumapIn = ntry.roumap;
            return;
        }
        if (a.equals("route-map-out")) {
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return;
            }
            roumapOut = ntry.roumap;
            return;
        }
        if (a.equals("route-policy-in")) {
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return;
            }
            roupolIn = ntry.rouplc;
            return;
        }
        if (a.equals("route-policy-out")) {
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return;
            }
            roupolOut = ntry.rouplc;
            return;
        }
        cmd.badCmd();
    }

    /**
     * undo configuration
     *
     * @param a command
     * @param cmd parameters
     */
    public void routerUnConfig(String a, cmds cmd) {
        if (a.equals("bfd")) {
            bfdTrigger = false;
            return;
        }
        if (a.equals("default-originate")) {
            defOrigin = false;
            return;
        }
        if (a.equals("suppress-prefix")) {
            suppressAddr = false;
            return;
        }
        if (a.equals("unsuppress-prefix")) {
            unsuppressAddr = false;
            return;
        }
        if (a.equals("verify-source")) {
            connectedCheck = false;
            return;
        }
        if (a.equals("split-horizon")) {
            splitHorizon = false;
            return;
        }
        if (a.equals("lq-mode")) {
            lqMode = false;
            return;
        }
        if (a.equals("prefix-list-in")) {
            prflstIn = null;
            return;
        }
        if (a.equals("prefix-list-out")) {
            prflstOut = null;
            return;
        }
        if (a.equals("route-map-in")) {
            roumapIn = null;
            return;
        }
        if (a.equals("route-map-out")) {
            roumapOut = null;
            return;
        }
        if (a.equals("route-policy-in")) {
            roupolIn = null;
            return;
        }
        if (a.equals("route-policy-out")) {
            roupolOut = null;
            return;
        }
        cmd.badCmd();
    }

    /**
     * get help text
     *
     * @param l list to update
     */
    public static void routerGetHelp(userHelp l) {
        l.add(null, false, 4, new int[]{-1}, "enable", "enable/disable olsr updates");
        l.add(null, false, 4, new int[]{-1}, "bfd", "enable bfd triggered down");
        l.add(null, false, 4, new int[]{-1}, "default-originate", "send default route to peer");
        l.add(null, false, 4, new int[]{-1}, "suppress-prefix", "do not advertise interface");
        l.add(null, false, 4, new int[]{-1}, "unsuppress-prefix", "do advertise interface");
        l.add(null, false, 4, new int[]{-1}, "verify-source", "check source address of updates");
        l.add(null, false, 4, new int[]{-1}, "split-horizon", "dont advertise back on rx interface");
        l.add(null, false, 4, new int[]{-1}, "lq-mode", "advertise with lq packets");
        l.add(null, false, 4, new int[]{5}, "distance", "administrative distance of routes");
        l.add(null, false, 5, new int[]{-1}, "<num>", "set administrative distance");
        l.add(null, false, 4, new int[]{5}, "willingness", "willingness to forward");
        l.add(null, false, 5, new int[]{-1}, "<num>", "set willingness");
        l.add(null, false, 4, new int[]{5}, "metric-in", "interface incoming metric");
        l.add(null, false, 5, new int[]{-1}, "<num>", "metric");
        l.add(null, false, 4, new int[]{5}, "metric-out", "interface outgoing metric");
        l.add(null, false, 5, new int[]{-1}, "<num>", "metric");
        l.add(null, false, 4, new int[]{5}, "packet-timer", "inter packet gap time");
        l.add(null, false, 5, new int[]{-1}, "<num>", "time in ms");
        l.add(null, false, 4, new int[]{5}, "hello-timer", "time between hellos");
        l.add(null, false, 5, new int[]{-1}, "<num>", "time in ms");
        l.add(null, false, 4, new int[]{5}, "hello-hold", "hello hold time");
        l.add(null, false, 5, new int[]{-1}, "<num>", "time in ms");
        l.add(null, false, 4, new int[]{5}, "advertise-timer", "time between advertisements");
        l.add(null, false, 5, new int[]{-1}, "<num>", "time in ms");
        l.add(null, false, 4, new int[]{5}, "advertise-hold", "advertisement hold time");
        l.add(null, false, 5, new int[]{-1}, "<num>", "time in ms");
        l.add(null, false, 4, new int[]{5}, "route-map-in", "process prefixes in ingress updates");
        l.add(null, false, 5, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 4, new int[]{5}, "route-map-out", "process prefixes in egress updates");
        l.add(null, false, 5, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 4, new int[]{5}, "route-policy-in", "process prefixes in ingress updates");
        l.add(null, false, 5, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 4, new int[]{5}, "route-policy-out", "process prefixes in egress updates");
        l.add(null, false, 5, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 4, new int[]{5}, "prefix-list-in", "filter prefixes in ingress updates");
        l.add(null, false, 5, new int[]{-1}, "<name:pl>", "name of prefix list");
        l.add(null, false, 4, new int[]{5}, "prefix-list-out", "filter prefixes in egress updates");
        l.add(null, false, 5, new int[]{-1}, "<name:pl>", "name of prefix list");
    }

    private void putMessage(packHolder pck, int typ, int tim, int ttl, int hop, byte[] buf) {
        int as;
        if (iface.addr.isIPv4()) {
            pck.putAddr(4, iface.addr.toIPv4());
            as = addrIPv4.size;
        } else {
            pck.putAddr(4, iface.addr.toIPv6());
            as = addrIPv6.size;
        }
        pck.putByte(0, typ);
        pck.putByte(1, rtrOlsr.tim2mant(tim));
        pck.msbPutW(2, 8 + as + buf.length);
        pck.putByte(as + 4, ttl);
        pck.putByte(as + 5, hop);
        pck.msbPutW(as + 6, msgSeq);
        pck.putSkip(as + 8);
        pck.putCopy(buf, 0, 0, buf.length);
        pck.putSkip(buf.length);
        pck.merge2end();
        msgSeq++;
    }

    private void putPacket(packHolder pck) {
        pck.msbPutW(0, pck.dataSize() + 4);
        pck.msbPutW(2, pckSeq);
        pck.putSkip(4);
        pck.merge2beg();
        pckSeq++;
    }

    private void putUpdate(tabRouteEntry<addrIP> ntry, packHolder pck) {
        byte[] buf;
        if (ntry.prefix.network.isIPv4()) {
            buf = new byte[addrIPv4.size * 2];
            addrPrefix<addrIPv4> a4 = addrPrefix.ip2ip4(ntry.prefix);
            a4.network.toBuffer(buf, 0); // address
            a4.mask.toBuffer(buf, addrIPv4.size); // netmask
        } else {
            buf = new byte[addrIPv6.size * 2];
            addrPrefix<addrIPv6> a6 = addrPrefix.ip2ip6(ntry.prefix);
            a6.network.toBuffer(buf, 0); // address
            a6.mask.toBuffer(buf, addrIPv6.size); // netmask
        }
        putMessage(pck, rtrOlsr.typHna, advertHold, 255, ntry.best.metric + metricOut, buf);
    }

    /**
     * send one hello over a connection
     *
     * @param conn connection to use
     */
    protected void sendOutHello(prtGenConn conn) {
        if (debugger.rtrOlsrTraf) {
            logger.debug("tx " + conn);
        }
        packHolder pck = new packHolder(true, true);
        boolean ipv4 = iface.addr.isIPv4();
        int i;
        for (i = 0; i < lower.neighs.size(); i++) {
            rtrOlsrNeigh ntry = lower.neighs.get(i);
            if (ntry == null) {
                continue;
            }
            if (compareTo(ntry.iface) != 0) {
                continue;
            }
            if (ipv4) {
                pck.putAddr(0, ntry.conn.peerAddr.toIPv4());
                pck.putSkip(addrIPv4.size);
            } else {
                pck.putAddr(0, ntry.conn.peerAddr.toIPv6());
                pck.putSkip(addrIPv6.size);
            }
            if (lqMode) {
                pck.putByte(0, 255); // lq
                pck.putByte(1, 255); // nlq
                pck.msbPutW(2, 0); // pad
                pck.putSkip(4);
            }
        }
        pck.merge2end();
        byte[] buf1 = pck.getCopy();
        byte[] buf2;
        pck.clear();
        pck.putCopy(buf1, 0, 0, buf1.length);
        pck.putSkip(buf1.length);
        pck.merge2end();
        pck.msbPutW(0, 0); // pad
        pck.putByte(2, rtrOlsr.tim2mant(helloTimer));
        pck.putByte(3, willingness);
        pck.putByte(4, 10); // link type
        pck.putByte(5, 0); // pad
        pck.msbPutW(6, buf1.length + 4); // size
        pck.putSkip(8);
        pck.merge2beg();
        buf2 = pck.getCopy();
        pck.clear();
        if (lqMode) {
            i = rtrOlsr.typLqHello;
        } else {
            i = rtrOlsr.typHello;
        }
        putMessage(pck, i, helloHold, 1, 0, buf2);
        putPacket(pck);
        conn.send2net(pck);
        bits.sleep(interPackTime);
        pck.clear();
        pck.putCopy(buf1, 0, 0, buf1.length);
        pck.putSkip(buf1.length);
        pck.merge2end();
        pck.msbPutW(0, neiSeq);
        pck.msbPutW(2, 0); // pad
        pck.putSkip(4);
        pck.merge2beg();
        buf2 = pck.getCopy();
        pck.clear();
        if (lqMode) {
            i = rtrOlsr.typLqTc;
        } else {
            i = rtrOlsr.typTc;
        }
        putMessage(pck, i, helloHold, 255, 0, buf2);
        putPacket(pck);
        conn.send2net(pck);
    }

    /**
     * send one update over a connection
     *
     * @param conn connection to use
     */
    protected void sendOutUpdates(prtGenConn conn) {
        if (debugger.rtrOlsrTraf) {
            logger.debug("tx " + conn);
        }
        packHolder pck = new packHolder(true, true);
        tabRoute<addrIP> tab1 = new tabRoute<addrIP>("copy");
        if (defOrigin) {
            if (iface.addr.isIPv4()) {
                tab1.add(tabRoute.addType.better, addrPrefix.ip4toIP(addrPrefix.defaultRoute4()), new addrIP());
            } else {
                tab1.add(tabRoute.addType.better, addrPrefix.ip6toIP(addrPrefix.defaultRoute6()), new addrIP());
            }
        }
        tab1.mergeFrom(tabRoute.addType.better, lower.routerComputedU, tabRouteAttr.distanLim);
        if (splitHorizon) {
            tab1.delIface(conn.iface);
        }
        tab1.mergeFrom(tabRoute.addType.better, lower.routerRedistedU, tabRouteAttr.distanLim);
        tabRoute<addrIP> tab2 = new tabRoute<addrIP>("copy");
        tabRoute.addUpdatedTable(tabRoute.addType.better, rtrBgpUtil.sfiUnicast, 0, tab2, tab1, true, roumapOut, roupolOut, prflstOut);
        advert = tab2;
        int entries = 0;
        for (int i = 0; i < tab2.size(); i++) {
            tabRouteEntry<addrIP> ntry = tab2.get(i);
            if (ntry == null) {
                continue;
            }
            if ((ntry.best.metric + metricOut) >= 0xff) {
                continue;
            }
            putUpdate(ntry, pck);
            entries++;
            if (pck.dataSize() < 512) {
                continue;
            }
            putPacket(pck);
            conn.send2net(pck);
            pck.clear();
            entries = 0;
            bits.sleep(interPackTime);
        }
        if (entries > 0) {
            putPacket(pck);
            conn.send2net(pck);
        }
    }

    /**
     * do some work round
     */
    public synchronized void doWork() {
        if (conn == null) {
            return;
        }
        conn.workInterval = helloTimer;
        conn.timeout = 0;
        sendOutHello(conn);
        long tim = bits.getTime();
        if ((tim - advTim) < advertTimer) {
            return;
        }
        advTim = tim;
        bits.sleep(interPackTime);
        sendOutUpdates(conn);
    }

}
