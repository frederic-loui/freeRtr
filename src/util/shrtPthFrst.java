package util;

import addr.addrIP;
import cfg.cfgAll;
import ip.ipMpls;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import tab.tabGen;
import tab.tabLabel;
import tab.tabLabelBier;
import tab.tabLabelBierN;
import tab.tabRouteAttr;
import tab.tabRouteEntry;
import tab.tabRouteIface;
import user.userFormat;

/**
 * dijkstra's shortest path first
 *
 * @param <Ta> type of nodes
 * @author matecsaba
 */
public class shrtPthFrst<Ta extends Comparator<? super Ta>> {

    /**
     * beginning of graph
     */
    public final static String graphBeg = "echo \"graph net {";

    /**
     * ending of graph
     */
    public final static String graphEnd = "}\" | dot -Tpng > net.png";

    private final tabGen<shrtPthFrstNode<Ta>> nodes;

    private final List<shrtPthFrstLog> log;

    private final int count;

    private final long tim1;

    private long tim2;

    private long tim3;

    private shrtPthFrstNode<Ta> spfRoot;

    /**
     * log size
     */
    public final syncInt logSize;

    /**
     * bidir check
     */
    public final syncInt bidir;

    /**
     * consider ecmp
     */
    public final syncInt ecmp;

    /**
     * consider hops in ecmp
     */
    public final syncInt hops;

    /**
     * construct spf
     *
     * @param old old spf
     */
    public shrtPthFrst(shrtPthFrst<Ta> old) {
        nodes = new tabGen<shrtPthFrstNode<Ta>>();
        tim1 = bits.getTime();
        if (old == null) {
            log = new ArrayList<shrtPthFrstLog>();
            count = 1;
            logSize = new syncInt(0);
            bidir = new syncInt(0);
            hops = new syncInt(0);
            ecmp = new syncInt(0);
            return;
        }
        log = old.log;
        logSize = old.logSize;
        bidir = old.bidir;
        hops = old.hops;
        ecmp = old.ecmp;
        count = old.count + 1;
        shrtPthFrstLog ntry = new shrtPthFrstLog();
        ntry.when = old.tim1;
        ntry.tim = (int) (old.tim3 - old.tim1);
        ntry.unreach = old.listUnreachables();
        ntry.topo = old.listTopoSum().hashCode();
        log.add(ntry);
        int max = logSize.get();
        for (; log.size() > max;) {
            log.remove(0);
        }
    }

    /**
     * copy topology
     *
     * @return copy
     */
    public shrtPthFrst<Ta> copyBytes() {
        shrtPthFrst<Ta> res = new shrtPthFrst<Ta>(this);
        for (int o = 0; o < nodes.size(); o++) {
            shrtPthFrstNode<Ta> nod = nodes.get(o);
            for (int i = 0; i < nod.conn.size(); i++) {
                shrtPthFrstConn<Ta> con = nod.conn.get(i);
                res.addConn(nod.name, con.target.name, con.metric, con.realHop, con.stub, con.ident);
            }
            res.addSegRouB(nod.name, nod.srBeg);
            res.addSegRouI(nod.name, nod.srIdx);
            res.addBierB(nod.name, nod.brBeg);
            res.addBierI(nod.name, nod.brIdx, true);
        }
        return res;
    }

    /**
     * add one connection
     *
     * @param from source node
     * @param to target node
     * @param metric metric
     * @param realHop true if hop, false if network
     * @param stub stub adjacency
     * @param ident link id
     */
    public void addConn(Ta from, Ta to, int metric, boolean realHop, boolean stub, Object ident) {
        if (metric < 0) {
            metric = 0;
        }
        shrtPthFrstNode<Ta> ntry = new shrtPthFrstNode<Ta>(to);
        shrtPthFrstNode<Ta> old = nodes.add(ntry);
        if (old != null) {
            ntry = old;
        }
        shrtPthFrstConn<Ta> c = new shrtPthFrstConn<Ta>();
        c.metric = metric;
        c.target = ntry;
        c.realHop = realHop;
        c.stub = stub;
        c.ident = ident;
        ntry = new shrtPthFrstNode<Ta>(from);
        old = nodes.add(ntry);
        if (old != null) {
            ntry = old;
        }
        ntry.conn.add(c);
    }

    /**
     * add next hop
     *
     * @param met metric of interface
     * @param nod node to add
     * @param hop hop to add
     * @param ifc interface number
     * @return true on error, false on success
     */
    public boolean addNextHop(int met, Ta nod, addrIP hop, tabRouteIface ifc) {
        shrtPthFrstNode<Ta> ntry = new shrtPthFrstNode<Ta>(nod);
        ntry = nodes.find(ntry);
        if (ntry == null) {
            return true;
        }
        if (ntry.uplinks == null) {
            return true;
        }
        if (met > ntry.nxtMet) {
            return false;
        }
        if (met < ntry.nxtMet) {
            for (int i = 0; i < ntry.uplinks.size(); i++) {
                shrtPthFrstRes<Ta> upl = ntry.uplinks.get(i);
                upl.nxtHop = null;
                upl.iface = null;
            }
            ntry.nxtMet = met;
        }
        for (int i = 0; i < ntry.uplinks.size(); i++) {
            shrtPthFrstRes<Ta> upl = ntry.uplinks.get(i);
            if (upl.hops > 1) {
                continue;
            }
            if (upl.iface != null) {
                continue;
            }
            upl.nxtHop = hop;
            upl.iface = ifc;
            return false;
        }
        return true;
    }

    /**
     * add segment routing base
     *
     * @param nod node to add
     * @param beg base label
     */
    public void addSegRouB(Ta nod, int beg) {
        if (beg < 1) {
            return;
        }
        shrtPthFrstNode<Ta> ntry = new shrtPthFrstNode<Ta>(nod);
        shrtPthFrstNode<Ta> old = nodes.add(ntry);
        if (old != null) {
            ntry = old;
        }
        if (ntry.srBeg != 0) {
            return;
        }
        ntry.srBeg = beg;
    }

    /**
     * add segment routing index
     *
     * @param nod node to add
     * @param idx node index
     */
    public void addSegRouI(Ta nod, int idx) {
        if (idx < 1) {
            return;
        }
        shrtPthFrstNode<Ta> ntry = new shrtPthFrstNode<Ta>(nod);
        shrtPthFrstNode<Ta> old = nodes.add(ntry);
        if (old != null) {
            ntry = old;
        }
        ntry.srIdx = idx;
    }

    /**
     * add bier base
     *
     * @param nod node to add
     * @param beg base label
     */
    public void addBierB(Ta nod, int beg) {
        if (beg < 1) {
            return;
        }
        shrtPthFrstNode<Ta> ntry = new shrtPthFrstNode<Ta>(nod);
        shrtPthFrstNode<Ta> old = nodes.add(ntry);
        if (old != null) {
            ntry = old;
        }
        if (ntry.brBeg != 0) {
            return;
        }
        ntry.brBeg = beg;
    }

    /**
     * add bier index
     *
     * @param nod node to add
     * @param idx node index
     * @param pri primary index
     */
    public void addBierI(Ta nod, int idx, boolean pri) {
        if (idx < 1) {
            return;
        }
        shrtPthFrstNode<Ta> ntry = new shrtPthFrstNode<Ta>(nod);
        shrtPthFrstNode<Ta> old = nodes.add(ntry);
        if (old != null) {
            ntry = old;
        }
        if (pri) {
            ntry.brIdx = idx;
        }
        ntry.brLst.add(new shrtPthFrstIdx(idx));
    }

    /**
     * find shortest path
     *
     * @param from starting node
     * @param to target node, null to every node
     * @return false on success, true on error
     */
    public boolean doCalc(Ta from, Ta to) {
        tim2 = bits.getTime();
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            ntry.uplink = null;
            ntry.uplinks = null;
            ntry.result = null;
            ntry.metric = Integer.MAX_VALUE;
            ntry.nxtMet = Integer.MAX_VALUE;
            ntry.visited = false;
        }
        shrtPthFrstNode<Ta> ntry = nodes.find(new shrtPthFrstNode<Ta>(from));
        if (ntry == null) {
            return true;
        }
        spfRoot = ntry;
        tabGen<shrtPthFrstNode<Ta>> lst = new tabGen<shrtPthFrstNode<Ta>>();
        ntry.metric = 0;
        ntry.visited = true;
        lst.add(ntry);
        boolean frst = true;
        boolean bid = bidir.get() != 0;
        boolean ecm = ecmp.get() != 0;
        boolean hps = hops.get() != 0;
        for (;;) {
            if (lst.size() < 1) {
                tim3 = bits.getTime();
                return true;
            }
            ntry = lst.get(0);
            for (int i = 1; i < lst.size(); i++) {
                shrtPthFrstNode<Ta> cur = lst.get(i);
                if (cur.metric < ntry.metric) {
                    ntry = cur;
                }
            }
            if (to != null) {
                if (to.compare(to, ntry.name) == 0) {
                    tim3 = bits.getTime();
                    return false;
                }
            }
            lst.del(ntry);
            ntry.visited = true;
            for (int i = 0; i < ntry.conn.size(); i++) {
                shrtPthFrstConn<Ta> c = ntry.conn.get(i);
                if (c == null) {
                    continue;
                }
                if ((!frst) && c.stub) {
                    continue;
                }
                if (bid) {
                    if (c.target.findConn(ntry) == null) {
                        continue;
                    }
                }
                int o = ntry.metric + c.metric;
                if (c.target.metric < o) {
                    continue;
                }
                int p;
                if (frst) {
                    p = 0;
                } else {
                    p = ntry.uplink.hops;
                }
                if (c.realHop) {
                    p++;
                }
                shrtPthFrstRes<Ta> upl = new shrtPthFrstRes<Ta>(ntry, p);
                if (c.target.metric != o) {
                    c.target.uplinks = new ArrayList<shrtPthFrstRes<Ta>>();
                    c.target.uplinks.add(upl);
                    c.target.uplink = upl;
                    c.target.metric = o;
                    lst.add(c.target);
                    continue;
                }
                if (hps && (upl.hops > c.target.uplink.hops)) {
                    continue;
                }
                if (ecm) {
                    c.target.uplinks.add(upl);
                }
                if (upl.compare(c.target.uplink, upl) < 0) {
                    continue;
                }
                if (!ecm) {
                    c.target.uplinks.clear();
                    c.target.uplinks.add(upl);
                }
                if (hps && (upl.hops < c.target.uplink.hops)) {
                    c.target.uplinks.clear();
                    c.target.uplinks.add(upl);
                }
                c.target.uplink = upl;
            }
            frst = false;
        }
    }

    /**
     * find next hops
     *
     * @param which node id
     * @return list of next hops
     */
    public List<shrtPthFrstRes<Ta>> findNextHop(Ta which) {
        List<shrtPthFrstRes<Ta>> res = new ArrayList<shrtPthFrstRes<Ta>>();
        shrtPthFrstNode<Ta> old = nodes.find(new shrtPthFrstNode<Ta>(which));
        if (old == null) {
            return res;
        }
        if (old.result != null) {
            return old.result;
        }
        List<shrtPthFrstRes<Ta>> ned = new ArrayList<shrtPthFrstRes<Ta>>();
        ned.add(new shrtPthFrstRes<Ta>(old, -1));
        for (;;) {
            if (ned.size() < 1) {
                break;
            }
            shrtPthFrstRes<Ta> cur = ned.remove(0);
            if (cur.nodeH.uplinks == null) {
                continue;
            }
            for (int i = 0; i < cur.nodeH.uplinks.size(); i++) {
                shrtPthFrstRes<Ta> upl = cur.nodeH.uplinks.get(i);
                int hops = cur.hops;
                if (hops < 0) {
                    hops = upl.hops;
                }
                if (upl.iface == null) {
                    ned.add(new shrtPthFrstRes<Ta>(upl.nodeH, hops));
                    continue;
                }
                shrtPthFrstRes<Ta> out = new shrtPthFrstRes<Ta>(cur.nodeH, hops);
                out.iface = upl.iface;
                out.nxtHop = upl.nxtHop;
                out.srBeg = cur.nodeH.srBeg;
                out.brBeg = cur.nodeH.brBeg;
                res.add(out);
            }
        }
        old.result = res;
        return res;
    }

    /**
     * get metric to node
     *
     * @param which node to query
     * @return metric to node, negative on error
     */
    public int getMetric(Ta which) {
        shrtPthFrstNode<Ta> ntry = nodes.find(new shrtPthFrstNode<Ta>(which));
        if (ntry == null) {
            return -1;
        }
        return ntry.metric;
    }

    /**
     * get segment routing base
     *
     * @param which node to query
     * @return label, -1=not found
     */
    public int getSegRouB(Ta which) {
        shrtPthFrstNode<Ta> ntry = nodes.find(new shrtPthFrstNode<Ta>(which));
        if (ntry == null) {
            return -1;
        }
        return ntry.srBeg;
    }

    /**
     * get bier base
     *
     * @param which node to query
     * @return label, -1=not found
     */
    public int getBierB(Ta which) {
        shrtPthFrstNode<Ta> ntry = nodes.find(new shrtPthFrstNode<Ta>(which));
        if (ntry == null) {
            return -1;
        }
        return ntry.brBeg;
    }

    private void doBier(shrtPthFrstNode<Ta> ntry) {
        if (ntry.uplink == null) {
            return;
        }
        for (int o = 0; o < ntry.brLst.size(); o++) {
            ntry.uplink.nodeH.brLst.add(ntry.brLst.get(o));
        }
        doBier(ntry.uplink.nodeH);
    }

    /**
     * get bier info
     *
     * @return calculated bier info
     */
    public tabLabelBier getBierI() {
        tabLabelBier res = new tabLabelBier();
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            doBier(ntry);
        }
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (ntry.uplink == null) {
                continue;
            }
            if (ntry.uplink.iface == null) {
                continue;
            }
            if (ntry.brBeg <= 0) {
                continue;
            }
            BigInteger msk = BigInteger.ZERO;
            for (int o = 0; o < ntry.brLst.size(); o++) {
                msk = msk.setBit(ntry.brLst.get(o).get());
            }
            tabLabelBierN per = new tabLabelBierN(ntry.uplink.iface, ntry.uplink.nxtHop, ntry.brBeg);
            per.ned = msk.shiftRight(1);
            res.peers.add(per);
        }
        return res;
    }

    /**
     * list segment routing
     *
     * @return list of segment routing
     */
    public String listSegRou() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (ntry.srIdx <= 0) {
                continue;
            }
            s += " " + ntry + "=" + ntry.srIdx;
        }
        return s;
    }

    /**
     * list no segment routing
     *
     * @return list of no segment routing
     */
    public String listNoSegRou() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (ntry.srIdx > 0) {
                continue;
            }
            s += " " + ntry;
        }
        return s;
    }

    /**
     * list bier
     *
     * @return list of bier
     */
    public String listBier() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (ntry.brIdx <= 0) {
                continue;
            }
            s += " " + ntry + "=" + ntry.brIdx;
        }
        return s;
    }

    /**
     * list no bier
     *
     * @return list of no bier
     */
    public String listNoBier() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (ntry.brIdx > 0) {
                continue;
            }
            s += " " + ntry;
        }
        return s;
    }

    /**
     * list unreachables
     *
     * @return list of unreachable nodes
     */
    public String listUnreachables() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (ntry.visited) {
                continue;
            }
            s += " " + ntry;
        }
        return s;
    }

    /**
     * list reachables
     *
     * @return list of reachable nodes
     */
    public String listReachables() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (!ntry.visited) {
                continue;
            }
            s += " " + ntry;
        }
        return s;
    }

    /**
     * list stubs
     *
     * @return list of stub nodes
     */
    public String listStubs() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            if (ntry.conn.size() > 1) {
                continue;
            }
            s += " " + ntry;
        }
        return s;
    }

    /**
     * list topology
     *
     * @return list of topology
     */
    public String listTopoSum() {
        String s = "";
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            s += " " + ntry + "," + ntry.visited + "," + ntry.conn.size();
        }
        return s;
    }

    /**
     * list topology
     *
     * @param adr address of node
     * @return list of topology
     */
    public userFormat listTopology(Ta adr) {
        userFormat res = new userFormat("|", "category|value");
        shrtPthFrstNode<Ta> ntry = new shrtPthFrstNode<Ta>(adr);
        ntry = nodes.find(ntry);
        if (ntry == null) {
            return null;
        }
        res.add("node|" + ntry);
        res.add("reachable|" + ntry.visited);
        res.add("stub|" + (ntry.conn.size() <= 1));
        res.add("uplink|" + ntry.uplink);
        if (ntry.uplinks != null) {
            res.add("uplinks|" + ntry.uplinks.size());
            for (int i = 0; i < ntry.uplinks.size(); i++) {
                shrtPthFrstRes<Ta> upl = ntry.uplinks.get(i);
                res.add("uplinknod|" + upl.nodeH);
                res.add("uplinkhop|" + upl.hops);
            }
        }
        if (ntry.result != null) {
            res.add("reaches|" + ntry.result.size());
            for (int i = 0; i < ntry.result.size(); i++) {
                shrtPthFrstRes<Ta> upl = ntry.result.get(i);
                res.add("reachnod|" + upl.nodeH);
                res.add("reachhop|" + upl.hops);
                res.add("reachvia|" + upl.nxtHop);
                res.add("reachifc|" + upl.iface);
            }
        }
        res.add("reachmet|" + ntry.metric);
        res.add("connections|" + ntry.conn.size());
        res.add("segrou|" + ntry.srIdx);
        res.add("bier|" + ntry.brIdx);
        for (int i = 0; i < ntry.conn.size(); i++) {
            shrtPthFrstConn<Ta> con = ntry.conn.get(i);
            if (con == null) {
                continue;
            }
            res.add("neighbor|" + con.target + "=" + con.metric + "=" + con.ident);
        }
        return res;
    }

    /**
     * list topology
     *
     * @return list of topology
     */
    public userFormat listTopology() {
        userFormat res = new userFormat("|", "node|reach|met|uplink|ups|res|conn|sr|br|neighbors");
        for (int i = 0; i < nodes.size(); i++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(i);
            if (ntry == null) {
                continue;
            }
            String a = "";
            if (ntry.uplinks == null) {
                a = "-1";
            } else {
                a = "" + ntry.uplinks.size();
            }
            String s = "";
            if (ntry.result == null) {
                s = "-1";
            } else {
                s = "" + ntry.result.size();
            }
            a = ntry + "|" + ntry.visited + "|" + ntry.metric + "|" + ntry.uplink + "|" + a + "|" + s + "|" + ntry.conn.size() + "|" + ntry.srIdx + "|" + ntry.brIdx + "|";
            for (int o = 0; o < ntry.conn.size(); o++) {
                shrtPthFrstConn<Ta> con = ntry.conn.get(o);
                if (con == null) {
                    continue;
                }
                a += con.target + "=" + con.metric + "=" + con.ident + " ";
            }
            res.add(a);
        }
        return res;
    }

    /**
     * list statistics
     *
     * @return list
     */
    public userFormat listStatistics() {
        userFormat res = new userFormat("|", "category|value");
        res.add("reach|" + listReachables());
        res.add("unreach|" + listUnreachables());
        res.add("stub|" + listStubs());
        res.add("segrou|" + listSegRou());
        res.add("nosegrou|" + listNoSegRou());
        res.add("bier|" + listBier());
        res.add("nobier|" + listNoBier());
        String a = listTopoSum();
        res.add("topostr|" + a);
        res.add("topoid|" + bits.toHexD(a.hashCode()));
        res.add("last|" + bits.time2str(cfgAll.timeZoneName, tim1 + cfgAll.timeServerOffset, 3) + " (" + bits.timePast(tim1) + " ago)");
        res.add("fill|" + (tim2 - tim1) + " ms");
        res.add("calc|" + (tim3 - tim2) + " ms");
        res.add("run|" + count + " times");
        return res;
    }

    /**
     * list statistics
     *
     * @return list
     */
    public userFormat listUsages() {
        userFormat res = new userFormat("|", "when|ago|time|topoid|unreach");
        for (int i = log.size() - 1; i >= 0; i--) {
            res.add("" + log.get(i));
        }
        return res;
    }

    /**
     * list tree
     *
     * @return list
     */
    public List<String> listTree() {
        List<String> res = new ArrayList<String>();
        if (spfRoot == null) {
            return res;
        }
        listTree(res, spfRoot, "");
        return res;
    }

    private void listTree(List<String> res, shrtPthFrstNode<Ta> ntry, String pref) {
        List<shrtPthFrstConn<Ta>> down = new ArrayList<shrtPthFrstConn<Ta>>();
        for (int i = 0; i < ntry.conn.size(); i++) {
            shrtPthFrstConn<Ta> cur = ntry.conn.get(i);
            if (cur.target.uplink == null) {
                continue;
            }
            if (ntry.compare(ntry, cur.target.uplink.nodeH) != 0) {
                continue;
            }
            down.add(cur);
        }
        res.add(pref + "`--" + ntry);
        for (int i = 0; i < down.size(); i++) {
            shrtPthFrstConn<Ta> cur = down.get(i);
            String a = (i + 1) == down.size() ? "   " : "  |";
            listTree(res, cur.target, pref + a);
        }
    }

    /**
     * list graphviz
     *
     * @return list
     */
    public List<String> listGraphviz() {
        List<String> res = new ArrayList<String>();
        res.add(graphBeg);
        for (int o = 0; o < nodes.size(); o++) {
            shrtPthFrstNode<Ta> ntry = nodes.get(o);
            res.add("//" + ntry);
            for (int i = 0; i < ntry.conn.size(); i++) {
                shrtPthFrstConn<Ta> cur = ntry.conn.get(i);
                res.add("  \\\"" + ntry + "\\\" -- \\\"" + cur.target + "\\\" [weight=" + cur.metric + "]");
            }
        }
        res.add(graphEnd);
        return res;
    }

    /**
     * populate route entry
     *
     * @param <Ta> type of nodes
     * @param rou route
     * @param hop hop list
     */
    public static <Ta extends Comparator<? super Ta>> void populateRoute(tabRouteEntry<addrIP> rou, List<shrtPthFrstRes<Ta>> hop) {
        rou.alts.clear();
        for (int i = 0; i < hop.size(); i++) {
            shrtPthFrstRes<Ta> upl = hop.get(i);
            tabRouteAttr<addrIP> res = new tabRouteAttr<addrIP>();
            rou.best.copyBytes(res, false);
            res.nextHop = upl.nxtHop.copyBytes();
            res.iface = upl.iface;
            res.hops = upl.hops;
            res.segrouBeg = upl.srBeg;
            res.bierBeg = upl.brBeg;
            res.labelRem = null;
            rou.addAlt(res);
        }
        rou.hashBest();
    }

    /**
     * populate route entry
     *
     * @param <Ta> type of nodes
     * @param rou route
     * @param src source
     * @param hop hop list
     * @param srPop sr pop requested
     */
    public static <Ta extends Comparator<? super Ta>> void populateSegrout(tabRouteEntry<addrIP> rou, tabRouteAttr<addrIP> src, List<shrtPthFrstRes<Ta>> hop, boolean srPop) {
        for (int i = 0; i < rou.alts.size(); i++) {
            tabRouteAttr<addrIP> res = rou.alts.get(i);
            res.segrouIdx = src.segrouIdx;
            res.segrouOld = src.segrouOld;
            res.bierIdx = src.bierIdx;
            res.bierHdr = src.bierHdr;
            res.bierOld = src.bierOld;
            if ((res.segrouIdx < 1) || (res.segrouBeg < 1)) {
                continue;
            }
            if (i >= hop.size()) {
                continue;
            }
            shrtPthFrstRes<Ta> upl = hop.get(i);
            if (upl.iface != res.iface) {
                continue;
            }
            if (upl.nxtHop.compare(upl.nxtHop, res.nextHop) != 0) {
                continue;
            }
            res.labelRem = tabLabel.int2labels(res.segrouBeg + res.segrouIdx);
            if (srPop && (res.hops <= 1)) {
                res.labelRem = tabLabel.int2labels(ipMpls.labelImp);
            }
        }
    }

}
