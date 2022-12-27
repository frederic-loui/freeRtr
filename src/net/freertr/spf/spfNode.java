package net.freertr.spf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.freertr.addr.addrIP;
import net.freertr.addr.addrType;
import net.freertr.tab.tabGen;
import net.freertr.tab.tabRoute;

/**
 * spf node
 *
 * @param <Ta> type of nodes
 * @author matecsaba
 */
public class spfNode<Ta extends addrType> implements Comparator<spfNode<Ta>> {

    /**
     * node id
     */
    protected Ta name;

    /**
     * identifier data
     */
    protected String ident;

    /**
     * reachable
     */
    protected boolean visited;

    /**
     * next hop metric
     */
    protected int nxtMet;

    /**
     * connections
     */
    protected List<spfConn<Ta>> conn = new ArrayList<spfConn<Ta>>();

    /**
     * algorithms
     */
    protected List<Integer> algo = new ArrayList<Integer>();

    /**
     * best uplink
     */
    protected spfResult<Ta> uplink;

    /**
     * uplinks
     */
    protected List<spfResult<Ta>> uplinks;

    /**
     * result
     */
    protected List<spfResult<Ta>> result;

    /**
     * fixed metric prefixes
     */
    protected tabRoute<addrIP> prfFix = new tabRoute<addrIP>("prf");

    /**
     * cumulative metric prefixes
     */
    protected tabRoute<addrIP> prfAdd = new tabRoute<addrIP>("prf");

    /**
     * fixed metric other prefixes
     */
    protected tabRoute<addrIP> othFix = new tabRoute<addrIP>("prf");

    /**
     * cumulative metric other prefixes
     */
    protected tabRoute<addrIP> othAdd = new tabRoute<addrIP>("prf");

    /**
     * metric
     */
    protected int metric;

    /**
     * segrou base
     */
    protected int srBeg;

    /**
     * segrou index
     */
    protected int srIdx;

    /**
     * bier base
     */
    protected int brBeg;

    /**
     * bier index
     */
    protected int brIdx;

    /**
     * bier nodes behind
     */
    protected tabGen<spfIndex> brLst = new tabGen<spfIndex>();

    /**
     * create new instance
     *
     * @param nam node id
     */
    public spfNode(Ta nam) {
        name = nam;
    }

    public int compare(spfNode<Ta> o1, spfNode<Ta> o2) {
        return o1.name.compare(o1.name, o2.name);
    }

    /**
     * find connection
     *
     * @param peer node id
     * @param met required metric
     * @return connection, null if not found
     */
    protected spfConn<Ta> findConn(spfNode<Ta> peer, int met) {
        spfConn<Ta> best = null;
        int diff = Integer.MAX_VALUE;
        for (int i = 0; i < conn.size(); i++) {
            spfConn<Ta> ntry = conn.get(i);
            if (peer.compare(peer, ntry.target) != 0) {
                continue;
            }
            if (met < 0) {
                return ntry;
            }
            if (met == ntry.metric) {
                return ntry;
            }
            int o = ntry.metric - met;
            if (o < 0) {
                o = -o;
            }
            if (o > diff) {
                continue;
            }
            best = ntry;
            diff = o;
        }
        return best;
    }

    public String toString() {
        if (ident != null) {
            return "" + ident;
        }
        return "" + name;
    }

}
