package rtr;

import addr.addrIP;
import addr.addrIPv4;
import cfg.cfgRtr;
import ip.ipFwd;
import ip.ipRtr;
import java.util.List;
import tab.tabRoute;
import user.userHelping;
import util.cmds;

/**
 * isis other router
 *
 * @author matecsaba
 */
public class rtrIsisOther extends ipRtr {

    /**
     * enabled
     */
    public boolean enabled;

    /**
     * external distance
     */
    public int distantExt;

    /**
     * intra-level distance
     */
    public int distantInt;

    /**
     * forwarding core
     */
    public final ipFwd fwd;

    private final rtrIsis parent;

    /**
     * unregister from ip
     */
    public void unregister2ip() {
        if (!enabled) {
            return;
        }
        enabled = false;
        fwd.routerDel(this);
    }

    /**
     * register to ip
     */
    public void register2ip() {
        if (enabled) {
            return;
        }
        enabled = true;
        fwd.routerAdd(this, parent.rouTyp, parent.rtrNum);
    }

    /**
     * create instance
     *
     * @param p parent
     * @param f forwarder
     */
    public rtrIsisOther(rtrIsis p, ipFwd f) {
        fwd = f;
        parent = p;
        distantExt = 115;
        distantInt = 115;
    }

    /**
     * convert to string
     *
     * @return string
     */
    public String toString() {
        return "isis on " + parent.fwdCore;
    }

    /**
     * create computed table
     */
    public synchronized void routerCreateComputed() {
    }

    /**
     * redistribution changed
     */
    public void routerRedistChanged() {
        parent.routerRedistChanged();
        fwd.routerChg(this);
    }

    /**
     * others changed
     */
    public void routerOthersChanged() {
    }

    /**
     * get help
     *
     * @param l list
     */
    public void routerGetHelp(userHelping l) {
    }

    /**
     * get config
     *
     * @param l list
     * @param beg beginning
     * @param filter filter
     */
    public void routerGetConfig(List<String> l, String beg, int filter) {
    }

    /**
     * configure
     *
     * @param cmd command
     * @return false if success, true if error
     */
    public boolean routerConfigure(cmds cmd) {
        return true;
    }

    /**
     * stop work
     */
    public void routerCloseNow() {
    }

    /**
     * get config
     *
     * @param l list to append
     * @param beg beginning
     */
    public void getConfig(List<String> l, String beg) {
        if (enabled) {
            l.add(beg + "enable");
        } else {
            l.add(cmds.tabulator + "no" + beg + "enable");
        }
        l.add(beg + "distance " + distantInt + " " + distantExt);
        cfgRtr.getShRedist(l, beg, this);
    }

    /**
     * get neighbor count
     *
     * @return count
     */
    public int routerNeighCount() {
        return 0;
    }

    /**
     * get neighbor list
     *
     * @param tab list
     */
    public void routerNeighList(tabRoute<addrIP> tab) {
    }

    /**
     * get interface count
     *
     * @return count
     */
    public int routerIfaceCount() {
        return 0;
    }

    /**
     * get list of link states
     *
     * @param tab table to update
     * @param par parameter
     * @param asn asn
     * @param adv advertiser
     */
    public void routerLinkStates(tabRoute<addrIP> tab, int par, int asn, addrIPv4 adv) {
    }

}
