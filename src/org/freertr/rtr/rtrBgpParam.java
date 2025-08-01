package org.freertr.rtr;

import java.util.ArrayList;
import java.util.List;
import org.freertr.addr.addrIP;
import org.freertr.auth.authLocal;
import org.freertr.cfg.cfgAll;
import org.freertr.cfg.cfgIfc;
import org.freertr.cfg.cfgPrfxlst;
import org.freertr.cfg.cfgProxy;
import org.freertr.cfg.cfgRoump;
import org.freertr.cfg.cfgRouplc;
import org.freertr.cfg.cfgRtr;
import org.freertr.clnt.clntProxy;
import org.freertr.sec.secInfoCfg;
import org.freertr.sec.secInfoUtl;
import org.freertr.tab.tabGen;
import org.freertr.tab.tabIntMatcher;
import org.freertr.tab.tabListing;
import org.freertr.tab.tabPrfxlstN;
import org.freertr.tab.tabRoute;
import org.freertr.tab.tabRtrmapN;
import org.freertr.tab.tabRtrplcN;
import org.freertr.user.userFilter;
import org.freertr.user.userHelp;
import org.freertr.util.bits;
import org.freertr.util.cmds;

/**
 * bgp4 parameters
 *
 * @author matecsaba
 */
public abstract class rtrBgpParam {

    /**
     * lower
     */
    public final rtrBgp lower;

    /**
     * true if template, false if neighbor
     */
    public final boolean isTemplate;

    /**
     * as of peer
     */
    public int remoteAs;

    /**
     * any as allowed
     */
    public boolean remoteAny;

    /**
     * local as
     */
    public int localAs;

    /**
     * address families
     */
    public long addrFams;

    /**
     * source template
     */
    public rtrBgpTemp template;

    /**
     * key id
     */
    public int keyId;

    /**
     * password
     */
    public String passwd;

    /**
     * remote description
     */
    public String description;

    /**
     * attribute filter
     */
    public tabIntMatcher attribFilter;

    /**
     * check neighbor route
     */
    public boolean fallOver;

    /**
     * ha mode
     */
    public boolean haMode;

    /**
     * consider remote asn in group membership
     */
    public boolean ungrpRemAs;

    /**
     * confederation peer
     */
    public boolean remoteConfed;

    /**
     * send accumulated igp
     */
    public boolean accIgp;

    /**
     * send entropy label
     */
    public boolean entrLab;

    /**
     * send traffic engineering
     */
    public boolean traffEng;

    /**
     * send pmsi tunnel
     */
    public boolean pmsiTun;

    /**
     * send connector
     */
    public boolean connect;

    /**
     * send pe distinguisher
     */
    public boolean peDist;

    /**
     * send aspath limit
     */
    public boolean pathLim;

    /**
     * send nsh service chain
     */
    public boolean nshChain;

    /**
     * send domain path
     */
    public boolean domainPath;

    /**
     * send bfd discriminator
     */
    public boolean bfdDiscr;

    /**
     * send tunnel encapsulation
     */
    public boolean tunEnc;

    /**
     * send link state
     */
    public boolean lnkSta;

    /**
     * send attribute set
     */
    public boolean attribSet;

    /**
     * collect unknown attributes
     */
    public rtrBgpMrt unknownsColl;

    /**
     * receive unknown attributes
     */
    public tabIntMatcher unknownsIn;

    /**
     * send unknown attributes
     */
    public tabIntMatcher unknownsOut;

    /**
     * log unknown attributes
     */
    public boolean unknownsLog;

    /**
     * log end changes
     */
    public boolean endChanges;

    /**
     * log hop changes
     */
    public boolean hopChanges;

    /**
     * log length changes
     */
    public tabIntMatcher lengthChanges;

    /**
     * ipinfo config
     */
    public secInfoCfg ipInfoCfg;

    /**
     * send segment routing
     */
    public boolean segRout;

    /**
     * send bier
     */
    public boolean bier;

    /**
     * send wide aspath
     */
    public boolean wideAsPath;

    /**
     * send route refresh
     */
    public boolean routeRefreshOld;

    /**
     * send route refresh
     */
    public boolean routeRefreshNew;

    /**
     * egress engineering index
     */
    public int egressEng;

    /**
     * leak prevention local role
     */
    public int leakRole;

    /**
     * leak prevention attribute
     */
    public boolean leakAttr;

    /**
     * leak prevention enforced
     */
    public boolean leakForce;

    /**
     * rpki ingress mode
     */
    public int rpkiIn;

    /**
     * rpki egress mode
     */
    public int rpkiOut;

    /**
     * rpki ingress mode
     */
    public int vpkiIn;

    /**
     * rpki egress mode
     */
    public int vpkiOut;

    /**
     * advertise pop label
     */
    public boolean labelPop;

    /**
     * capability negotiation
     */
    public boolean capaNego;

    /**
     * track nexthops
     */
    public boolean trackNxthop;

    /**
     * route reflector client
     */
    public boolean reflectClnt;

    /**
     * dmz link bandwidth
     */
    public int dmzLinkBw;

    /**
     * spf metric
     */
    public int spfMetric;

    /**
     * spf stub
     */
    public boolean spfStub;

    /**
     * randomize session startup times
     */
    public int randomStartF;

    /**
     * randomize session startup times
     */
    public int randomStartL;

    /**
     * distance
     */
    public int distance;

    /**
     * preference
     */
    public int preference;

    /**
     * keep alive
     */
    public int keepAlive;

    /**
     * hold timer
     */
    public int holdTimer;

    /**
     * source interface
     */
    public cfgIfc srcIface;

    /**
     * peer disabled
     */
    public boolean shutdown;

    /**
     * bfd enabled, 0=off, 1=on, 2=strict
     */
    public int bfdTrigger;

    /**
     * backup of peer
     */
    public addrIP backupPeer;

    /**
     * proxy to use
     */
    public clntProxy proxy2use;

    /**
     * address to use
     */
    public addrIP proxy2adr;

    /**
     * port to use
     */
    public int proxy2prt;

    /**
     * soft reconfiguration
     */
    public boolean softReconfig;

    /**
     * graceful restart
     */
    public long graceRestart;

    /**
     * long lived graceful restart
     */
    public long llGraceRestart;

    /**
     * multiple labels
     */
    public long multiLabel;

    /**
     * extended nexthop current afi
     */
    public long extNextCur;

    /**
     * extended nexthop other afi
     */
    public long extNextOtr;

    /**
     * hostname
     */
    public int hostname;

    /**
     * software version
     */
    public boolean software;

    /**
     * extended open
     */
    public boolean extOpen;

    /**
     * extended update
     */
    public boolean extUpdate;

    /**
     * not transmit during receive
     */
    public boolean unidirection;

    /**
     * connection mode 1=active, 2=passive, 3=both, 4=dynamic, 5=listen, replay
     */
    public int socketMode;

    /**
     * compression mode 0=none, 1=receive, 2=send, 3=both
     */
    public int compressMode;

    /**
     * dynamic capability exchange
     */
    public boolean dynamicCapab;

    /**
     * buffer size
     */
    public int bufferSize;

    /**
     * ttl security
     */
    public int ttlSecurity;

    /**
     * tos value
     */
    public int tosValue;

    /**
     * additional path receive(1) mode
     */
    public long addpathRmode;

    /**
     * additional path transmit(2) mode
     */
    public long addpathTmode;

    /**
     * default information originate
     */
    public boolean sendDefRou;

    /**
     * other default information originate
     */
    public boolean sendOtrDefRou;

    /**
     * propagate next hop
     */
    public boolean nxtHopUnchgd;

    /**
     * rewrite next hop
     */
    public boolean nxtHopPeer;

    /**
     * rewrite next hop
     */
    public boolean nxtHopSelf;

    /**
     * use multiple labels
     */
    public boolean nxtHopMltlb;

    /**
     * use next hop capabilities
     */
    public boolean nxtHopCapa;

    /**
     * propagate community 0=none, 1=std, 2=ext, 4=lrg, 7=all
     */
    public int sendCommunity;

    /**
     * preserve attributes
     */
    public boolean intVpnClnt;

    /**
     * allow my as relearn
     */
    public boolean allowAsIn;

    /**
     * allow peer as advertisement
     */
    public boolean allowAsOut;

    /**
     * user collected rtfilters
     */
    public boolean rtfilterIn;

    /**
     * honor advertised rtfilters
     */
    public boolean rtfilterOut;

    /**
     * rtfilters in use
     */
    public tabRoute<addrIP> rtfilterUsed;

    /**
     * transmit advertisement interval
     */
    public int advertIntTx;

    /**
     * receive advertisement interval
     */
    public int advertIntRx;

    /**
     * dampening prefixes
     */
    public tabGen<rtrBgpDamp> dampenPfxs;

    /**
     * dampening withdarw penalty
     */
    public int dampenWthd;

    /**
     * dampening announce penalty
     */
    public int dampenAnno;

    /**
     * dampening minimum penalty
     */
    public int dampenMinp;

    /**
     * dampening maximum penalty
     */
    public int dampenMaxp;

    /**
     * dampening suppress threshold
     */
    public int dampenSupp;

    /**
     * dampening reuse threshold
     */
    public int dampenReus;

    /**
     * dampening half life time
     */
    public int dampenHalf;

    /**
     * max template clones
     */
    public int maxClones;

    /**
     * max prefix count
     */
    public int maxPrxInCnt;

    /**
     * max prefix percent
     */
    public int maxPrxInPrc;

    /**
     * max prefix count
     */
    public int maxPrxOutCnt;

    /**
     * max prefix percent
     */
    public int maxPrxOutPrc;

    /**
     * enforce first as
     */
    public boolean enforceFirst;

    /**
     * route server client
     */
    public boolean serverClnt;

    /**
     * remove private as
     */
    public boolean removePrivAsOut;

    /**
     * remove private as
     */
    public boolean removePrivAsIn;

    /**
     * override peer as
     */
    public boolean overridePeerOut;

    /**
     * override peer as
     */
    public boolean overridePeerIn;

    /**
     * monitor to use
     */
    public rtrBgpMon monitor;

    /**
     * dump to use
     */
    public rtrBgpMrt dump;

    /**
     * other address
     */
    public addrIP otherAdr;

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
     * other ingress prefix list
     */
    public tabListing<tabPrfxlstN, addrIP> oprflstIn;

    /**
     * other egress prefix list
     */
    public tabListing<tabPrfxlstN, addrIP> oprflstOut;

    /**
     * other ingress route map
     */
    public tabListing<tabRtrmapN, addrIP> oroumapIn;

    /**
     * other egress route map
     */
    public tabListing<tabRtrmapN, addrIP> oroumapOut;

    /**
     * other ingress route policy
     */
    public tabListing<tabRtrplcN, addrIP> oroupolIn;

    /**
     * other egress route policy
     */
    public tabListing<tabRtrplcN, addrIP> oroupolOut;

    /**
     * ingress vpn route map
     */
    public tabListing<tabRtrmapN, addrIP> vroumapIn;

    /**
     * egress vpn route map
     */
    public tabListing<tabRtrmapN, addrIP> vroumapOut;

    /**
     * ingress vpn route policy
     */
    public tabListing<tabRtrplcN, addrIP> vroupolIn;

    /**
     * egress vpn route policy
     */
    public tabListing<tabRtrplcN, addrIP> vroupolOut;

    /**
     * ingress other vpn route map
     */
    public tabListing<tabRtrmapN, addrIP> wroumapIn;

    /**
     * egress other vpn route map
     */
    public tabListing<tabRtrmapN, addrIP> wroumapOut;

    /**
     * ingress other vpn route policy
     */
    public tabListing<tabRtrplcN, addrIP> wroupolIn;

    /**
     * egress other vpn route policy
     */
    public tabListing<tabRtrplcN, addrIP> wroupolOut;

    /**
     * ingress ethernet vpn route map
     */
    public tabListing<tabRtrmapN, addrIP> eroumapIn;

    /**
     * egress ethernet vpn route map
     */
    public tabListing<tabRtrmapN, addrIP> eroumapOut;

    /**
     * ingress ethernet vpn route policy
     */
    public tabListing<tabRtrplcN, addrIP> eroupolIn;

    /**
     * egress ethernet vpn route policy
     */
    public tabListing<tabRtrplcN, addrIP> eroupolOut;

    /**
     * unicast
     */
    public final static long mskUni = 0x01;

    /**
     * labeled unicast
     */
    public final static long mskLab = 0x02;

    /**
     * multicast
     */
    public final static long mskMlt = 0x04;

    /**
     * vpn unicast
     */
    public final static long mskVpnU = 0x08;

    /**
     * vpn multicast
     */
    public final static long mskVpnM = 0x10;

    /**
     * vpls
     */
    public final static long mskVpls = 0x20;

    /**
     * evpn
     */
    public final static long mskEvpn = 0x40;

    /**
     * mdt
     */
    public final static long mskMdt = 0x80;

    /**
     * flowspec
     */
    public final static long mskFlw = 0x100;

    /**
     * vpn flowspec
     */
    public final static long mskVpnF = 0x200;

    /**
     * other vpn unicast
     */
    public final static long mskVpoU = 0x400;

    /**
     * other vpn multicast
     */
    public final static long mskVpoM = 0x800;

    /**
     * other vpn flowspec
     */
    public final static long mskVpoF = 0x1000;

    /**
     * mvpn
     */
    public final static long mskMvpn = 0x2000;

    /**
     * other mvpn
     */
    public final static long mskMvpo = 0x4000;

    /**
     * other labeled unicast
     */
    public final static long mskOlab = 0x8000;

    /**
     * mspw
     */
    public final static long mskMspw = 0x10000;

    /**
     * srte
     */
    public final static long mskSrte = 0x20000;

    /**
     * link state
     */
    public final static long mskLnks = 0x40000;

    /**
     * other unicast
     */
    public final static long mskOuni = 0x80000;

    /**
     * other multicast
     */
    public final static long mskOmlt = 0x100000;

    /**
     * other flowspec
     */
    public final static long mskOflw = 0x200000;

    /**
     * other srte
     */
    public final static long mskOsrt = 0x400000;

    /**
     * nsh
     */
    public final static long mskNsh = 0x800000;

    /**
     * rtfilter
     */
    public final static long mskRtf = 0x1000000;

    /**
     * classful transport plane
     */
    public final static long mskCtp = 0x2000000;

    /**
     * other classful transport plane
     */
    public final static long mskOctp = 0x4000000;

    /**
     * rpd
     */
    public final static long mskRpd = 0x8000000;

    /**
     * color aware routing
     */
    public final static long mskCar = 0x10000000;

    /**
     * other color aware routing
     */
    public final static long mskOcar = 0x20000000;

    /**
     * mvpn
     */
    public final static long mskMtre = 0x40000000;

    /**
     * other mvpn
     */
    public final static long mskMtro = 0x80000000L;

    /**
     * spf
     */
    public final static long mskSpf = 0x100000000L;

    /**
     * all
     */
    public final static long mskAll = mskUni | mskLab | mskCtp | mskCar | mskMlt | mskVpnU | mskVpnM | mskVpls | mskEvpn | mskMdt | mskSrte | mskLnks | mskFlw | mskVpnF | mskVpoU | mskVpoM | mskVpoF | mskMvpn | mskMvpo | mskOlab | mskOctp | mskOcar | mskOuni | mskOmlt | mskOflw | mskOsrt | mskMspw | mskNsh | mskRpd | mskSpf | mskRtf | mskMtre | mskMtro;

    /**
     * string to afi mask
     *
     * @param s string
     * @return afi mask
     */
    public final static long string2mask(String s) {
        cmds c = new cmds("afi", s);
        return string2mask(c);
    }

    /**
     * string to afi mask
     *
     * @param c string
     * @return afi mask
     */
    public final static long string2mask(cmds c) {
        long i = 0;
        for (;;) {
            String a = c.word();
            if (a.length() < 1) {
                break;
            }
            if (a.equals("all")) {
                i |= mskAll;
            }
            if (a.equals("none")) {
                i = 0;
            }
            if (a.equals("unicast")) {
                i |= mskUni;
            }
            if (a.equals("labeled")) {
                i |= mskLab;
            }
            if (a.equals("ctp")) {
                i |= mskCtp;
            }
            if (a.equals("car")) {
                i |= mskCar;
            }
            if (a.equals("multicast")) {
                i |= mskMlt;
            }
            if (a.equals("flowspec")) {
                i |= mskFlw;
            }
            if (a.equals("vpnuni")) {
                i |= mskVpnU;
            }
            if (a.equals("vpnmlt")) {
                i |= mskVpnM;
            }
            if (a.equals("vpnflw")) {
                i |= mskVpnF;
            }
            if (a.equals("vpls")) {
                i |= mskVpls;
            }
            if (a.equals("mspw")) {
                i |= mskMspw;
            }
            if (a.equals("evpn")) {
                i |= mskEvpn;
            }
            if (a.equals("mdt")) {
                i |= mskMdt;
            }
            if (a.equals("nsh")) {
                i |= mskNsh;
            }
            if (a.equals("rpd")) {
                i |= mskRpd;
            }
            if (a.equals("spf")) {
                i |= mskSpf;
            }
            if (a.equals("rtfilter")) {
                i |= mskRtf;
            }
            if (a.equals("srte")) {
                i |= mskSrte;
            }
            if (a.equals("linkstate")) {
                i |= mskLnks;
            }
            if (a.equals("mvpn")) {
                i |= mskMvpn;
            }
            if (a.equals("mtree")) {
                i |= mskMtre;
            }
            if (a.equals("omtree")) {
                i |= mskMtro;
            }
            if (a.equals("omvpn")) {
                i |= mskMvpo;
            }
            if (a.equals("ovpnuni")) {
                i |= mskVpoU;
            }
            if (a.equals("ovpnmlt")) {
                i |= mskVpoM;
            }
            if (a.equals("ovpnflw")) {
                i |= mskVpoF;
            }
            if (a.equals("olab")) {
                i |= mskOlab;
            }
            if (a.equals("octp")) {
                i |= mskOctp;
            }
            if (a.equals("ocar")) {
                i |= mskOcar;
            }
            if (a.equals("ouni")) {
                i |= mskOuni;
            }
            if (a.equals("omlt")) {
                i |= mskOmlt;
            }
            if (a.equals("oflw")) {
                i |= mskOflw;
            }
            if (a.equals("osrt")) {
                i |= mskOsrt;
            }
        }
        i = exclusiveMsk(i, mskUni, mskLab);
        i = exclusiveMsk(i, mskUni, mskCtp);
        i = exclusiveMsk(i, mskUni, mskCar);
        i = exclusiveMsk(i, mskLab, mskCtp);
        i = exclusiveMsk(i, mskLab, mskCar);
        i = exclusiveMsk(i, mskCtp, mskCar);
        i = exclusiveMsk(i, mskOuni, mskOlab);
        i = exclusiveMsk(i, mskOuni, mskOctp);
        i = exclusiveMsk(i, mskOuni, mskOcar);
        i = exclusiveMsk(i, mskOlab, mskOctp);
        i = exclusiveMsk(i, mskOlab, mskOcar);
        i = exclusiveMsk(i, mskOctp, mskOcar);
        return i;
    }

    private static long exclusiveMsk(long cur, long bck, long pri) {
        if ((cur & bck) == 0) {
            return cur;
        }
        if ((cur & pri) == 0) {
            return cur;
        }
        return cur - bck;
    }

    /**
     * afi mask to string
     *
     * @param i afi mask
     * @return string
     */
    public final static String mask2string(long i) {
        String a = "";
        if ((i & mskUni) != 0) {
            a += " unicast";
        }
        if ((i & mskLab) != 0) {
            a += " labeled";
        }
        if ((i & mskCtp) != 0) {
            a += " ctp";
        }
        if ((i & mskCar) != 0) {
            a += " car";
        }
        if ((i & mskMlt) != 0) {
            a += " multicast";
        }
        if ((i & mskOlab) != 0) {
            a += " olab";
        }
        if ((i & mskOctp) != 0) {
            a += " octp";
        }
        if ((i & mskOcar) != 0) {
            a += " ocar";
        }
        if ((i & mskOuni) != 0) {
            a += " ouni";
        }
        if ((i & mskOmlt) != 0) {
            a += " omlt";
        }
        if ((i & mskFlw) != 0) {
            a += " flowspec";
        }
        if ((i & mskOflw) != 0) {
            a += " oflw";
        }
        if ((i & mskVpnU) != 0) {
            a += " vpnuni";
        }
        if ((i & mskVpnM) != 0) {
            a += " vpnmlt";
        }
        if ((i & mskVpnF) != 0) {
            a += " vpnflw";
        }
        if ((i & mskVpoU) != 0) {
            a += " ovpnuni";
        }
        if ((i & mskVpoM) != 0) {
            a += " ovpnmlt";
        }
        if ((i & mskVpoF) != 0) {
            a += " ovpnflw";
        }
        if ((i & mskVpls) != 0) {
            a += " vpls";
        }
        if ((i & mskMspw) != 0) {
            a += " mspw";
        }
        if ((i & mskEvpn) != 0) {
            a += " evpn";
        }
        if ((i & mskMdt) != 0) {
            a += " mdt";
        }
        if ((i & mskNsh) != 0) {
            a += " nsh";
        }
        if ((i & mskRpd) != 0) {
            a += " rpd";
        }
        if ((i & mskSpf) != 0) {
            a += " spf";
        }
        if ((i & mskRtf) != 0) {
            a += " rtfilter";
        }
        if ((i & mskSrte) != 0) {
            a += " srte";
        }
        if ((i & mskOsrt) != 0) {
            a += " osrt";
        }
        if ((i & mskLnks) != 0) {
            a += " linkstate";
        }
        if ((i & mskMvpn) != 0) {
            a += " mvpn";
        }
        if ((i & mskMvpo) != 0) {
            a += " omvpn";
        }
        if ((i & mskMtre) != 0) {
            a += " mtree";
        }
        if ((i & mskMtro) != 0) {
            a += " omtree";
        }
        return a;
    }

    /**
     * get list of address families
     *
     * @param hl helping
     * @param lev level
     * @param nxt next
     * @param end ending
     * @param all all, none
     */
    public static void getAfiList(userHelp hl, int lev, int[] nxt, String end, boolean all) {
        if (all) {
            hl.add(null, false, lev, nxt, "all", "all address family to " + end);
            hl.add(null, false, lev, nxt, "none", "no address family to " + end);
        }
        hl.add(null, false, lev, nxt, "unicast", "address family to " + end);
        hl.add(null, false, lev, nxt, "labeled", "address family to " + end);
        hl.add(null, false, lev, nxt, "ctp", "address family to " + end);
        hl.add(null, false, lev, nxt, "car", "address family to " + end);
        hl.add(null, false, lev, nxt, "olab", "address family to " + end);
        hl.add(null, false, lev, nxt, "octp", "address family to " + end);
        hl.add(null, false, lev, nxt, "ocar", "address family to " + end);
        hl.add(null, false, lev, nxt, "ouni", "address family to " + end);
        hl.add(null, false, lev, nxt, "omlt", "address family to " + end);
        hl.add(null, false, lev, nxt, "oflw", "address family to " + end);
        hl.add(null, false, lev, nxt, "osrt", "address family to " + end);
        hl.add(null, false, lev, nxt, "multicast", "address family to " + end);
        hl.add(null, false, lev, nxt, "flowspec", "address family to " + end);
        hl.add(null, false, lev, nxt, "vpnuni", "address family to " + end);
        hl.add(null, false, lev, nxt, "vpnmlt", "address family to " + end);
        hl.add(null, false, lev, nxt, "vpnflw", "address family to " + end);
        hl.add(null, false, lev, nxt, "ovpnuni", "address family to " + end);
        hl.add(null, false, lev, nxt, "ovpnmlt", "address family to " + end);
        hl.add(null, false, lev, nxt, "ovpnflw", "address family to " + end);
        hl.add(null, false, lev, nxt, "vpls", "address family to " + end);
        hl.add(null, false, lev, nxt, "mspw", "address family to " + end);
        hl.add(null, false, lev, nxt, "evpn", "address family to " + end);
        hl.add(null, false, lev, nxt, "mdt", "address family to " + end);
        hl.add(null, false, lev, nxt, "nsh", "address family to " + end);
        hl.add(null, false, lev, nxt, "rpd", "address family to " + end);
        hl.add(null, false, lev, nxt, "spf", "address family to " + end);
        hl.add(null, false, lev, nxt, "rtfilter", "address family to " + end);
        hl.add(null, false, lev, nxt, "srte", "address family to " + end);
        hl.add(null, false, lev, nxt, "linkstate", "address family to " + end);
        hl.add(null, false, lev, nxt, "mvpn", "address family to " + end);
        hl.add(null, false, lev, nxt, "omvpn", "address family to " + end);
        hl.add(null, false, lev, nxt, "mtree", "address family to " + end);
        hl.add(null, false, lev, nxt, "omtree", "address family to " + end);
    }

    /**
     * create parameter container
     *
     * @param parent bgp process
     * @param temp template or not
     */
    public rtrBgpParam(rtrBgp parent, boolean temp) {
        lower = parent;
        isTemplate = temp;
        if (lower != null) {
            localAs = lower.localAs;
            addrFams = lower.addrFams;
        }
        wideAsPath = true;
        routeRefreshOld = true;
        routeRefreshNew = true;
        allowAsOut = true;
        dmzLinkBw = -1;
        preference = 100;
        spfMetric = 10;
        randomStartF = 2;
        randomStartL = 15;
        socketMode = 3;
        bufferSize = 65536;
        ttlSecurity = -1;
        tosValue = -1;
        leakRole = -1;
        rpkiIn = 0;
        rpkiOut = 0;
        vpkiIn = 0;
        vpkiOut = 0;
        keyId = -1;
        passwd = null;
        capaNego = true;
        trackNxthop = true;
        keepAlive = 60 * 1000;
        holdTimer = keepAlive * 3;
    }

    /**
     * copy parameters
     *
     * @param src source where from copy
     */
    public void copyFrom(rtrBgpParam src) {
        remoteAs = src.remoteAs;
        remoteAny = src.remoteAny;
        localAs = src.localAs;
        addrFams = src.addrFams;
        template = src.template;
        description = src.description;
        haMode = src.haMode;
        fallOver = src.fallOver;
        attribFilter = src.attribFilter;
        remoteConfed = src.remoteConfed;
        reflectClnt = src.reflectClnt;
        dmzLinkBw = src.dmzLinkBw;
        spfMetric = src.spfMetric;
        spfStub = src.spfStub;
        randomStartF = src.randomStartF;
        randomStartL = src.randomStartL;
        distance = src.distance;
        preference = src.preference;
        keepAlive = src.keepAlive;
        holdTimer = src.holdTimer;
        srcIface = src.srcIface;
        shutdown = src.shutdown;
        monitor = src.monitor;
        dump = src.dump;
        otherAdr = src.otherAdr;
        keyId = src.keyId;
        passwd = src.passwd;
        accIgp = src.accIgp;
        entrLab = src.entrLab;
        traffEng = src.traffEng;
        pmsiTun = src.pmsiTun;
        connect = src.connect;
        peDist = src.peDist;
        pathLim = src.pathLim;
        nshChain = src.nshChain;
        domainPath = src.domainPath;
        bfdDiscr = src.bfdDiscr;
        tunEnc = src.tunEnc;
        lnkSta = src.lnkSta;
        attribSet = src.attribSet;
        endChanges = src.endChanges;
        hopChanges = src.hopChanges;
        unknownsColl = src.unknownsColl;
        unknownsLog = src.unknownsLog;
        unknownsOut = src.unknownsOut;
        unknownsIn = src.unknownsIn;
        ipInfoCfg = src.ipInfoCfg;
        segRout = src.segRout;
        bier = src.bier;
        wideAsPath = src.wideAsPath;
        routeRefreshOld = src.routeRefreshOld;
        routeRefreshNew = src.routeRefreshNew;
        egressEng = src.egressEng;
        leakRole = src.leakRole;
        leakAttr = src.leakAttr;
        rpkiIn = src.rpkiIn;
        rpkiOut = src.rpkiOut;
        vpkiIn = src.vpkiIn;
        vpkiOut = src.vpkiOut;
        leakForce = src.leakForce;
        labelPop = src.labelPop;
        capaNego = src.capaNego;
        trackNxthop = src.trackNxthop;
        bfdTrigger = src.bfdTrigger;
        backupPeer = src.backupPeer;
        softReconfig = src.softReconfig;
        graceRestart = src.graceRestart;
        llGraceRestart = src.llGraceRestart;
        multiLabel = src.multiLabel;
        extNextCur = src.extNextCur;
        extNextOtr = src.extNextOtr;
        hostname = src.hostname;
        software = src.software;
        extOpen = src.extOpen;
        extUpdate = src.extUpdate;
        unidirection = src.unidirection;
        compressMode = src.compressMode;
        dynamicCapab = src.dynamicCapab;
        socketMode = src.socketMode;
        bufferSize = src.bufferSize;
        ttlSecurity = src.ttlSecurity;
        tosValue = src.tosValue;
        addpathRmode = src.addpathRmode;
        addpathTmode = src.addpathTmode;
        sendDefRou = src.sendDefRou;
        sendOtrDefRou = src.sendOtrDefRou;
        nxtHopUnchgd = src.nxtHopUnchgd;
        nxtHopPeer = src.nxtHopPeer;
        nxtHopSelf = src.nxtHopSelf;
        nxtHopMltlb = src.nxtHopMltlb;
        nxtHopCapa = src.nxtHopCapa;
        sendCommunity = src.sendCommunity;
        intVpnClnt = src.intVpnClnt;
        allowAsIn = src.allowAsIn;
        allowAsOut = src.allowAsOut;
        rtfilterIn = src.rtfilterIn;
        rtfilterOut = src.rtfilterOut;
        rtfilterUsed = src.rtfilterUsed;
        advertIntTx = src.advertIntTx;
        advertIntRx = src.advertIntRx;
        serverClnt = src.serverClnt;
        dampenWthd = src.dampenWthd;
        dampenAnno = src.dampenAnno;
        dampenMinp = src.dampenMinp;
        dampenMaxp = src.dampenMaxp;
        dampenSupp = src.dampenSupp;
        dampenReus = src.dampenReus;
        dampenHalf = src.dampenHalf;
        if (src.lengthChanges == null) {
            lengthChanges = null;
        } else {
            lengthChanges = src.lengthChanges.copyBytes();
        }
        if (src.dampenPfxs == null) {
            dampenPfxs = null;
        } else {
            if (dampenPfxs == null) {
                dampenPfxs = new tabGen<rtrBgpDamp>();
            }
        }
        maxClones = src.maxClones;
        maxPrxInCnt = src.maxPrxInCnt;
        maxPrxInPrc = src.maxPrxInPrc;
        maxPrxOutCnt = src.maxPrxOutCnt;
        maxPrxOutPrc = src.maxPrxOutPrc;
        enforceFirst = src.enforceFirst;
        removePrivAsOut = src.removePrivAsOut;
        removePrivAsIn = src.removePrivAsIn;
        ungrpRemAs = src.ungrpRemAs;
        overridePeerOut = src.overridePeerOut;
        overridePeerIn = src.overridePeerIn;
        prflstIn = src.prflstIn;
        prflstOut = src.prflstOut;
        roumapIn = src.roumapIn;
        roumapOut = src.roumapOut;
        roupolIn = src.roupolIn;
        roupolOut = src.roupolOut;
        oprflstIn = src.oprflstIn;
        oprflstOut = src.oprflstOut;
        oroumapIn = src.oroumapIn;
        oroumapOut = src.oroumapOut;
        oroupolIn = src.oroupolIn;
        oroupolOut = src.oroupolOut;
        vroumapIn = src.vroumapIn;
        vroumapOut = src.vroumapOut;
        vroupolIn = src.vroupolIn;
        vroupolOut = src.vroupolOut;
        wroumapIn = src.wroumapIn;
        wroumapOut = src.wroumapOut;
        wroupolIn = src.wroupolIn;
        wroupolOut = src.wroupolOut;
        eroumapIn = src.eroumapIn;
        eroumapOut = src.eroumapOut;
        eroupolIn = src.eroupolIn;
        eroupolOut = src.eroupolOut;
    }

    /**
     * check if rtfilter needed
     *
     * @param afi afi in question
     * @return true if yes, false if not
     */
    public boolean shouldRtfilter(int afi) {
        return (afi == lower.afiVpnU) || (afi == lower.afiVpoU)
                || (afi == lower.afiVpnM) || (afi == lower.afiVpoM)
                || (afi == lower.afiVpnF) || (afi == lower.afiVpoF)
                || (afi == lower.afiVpls) || (afi == lower.afiEvpn);
    }

    /**
     * check if same output will produced
     *
     * @param src where to compare
     * @return false if same, true if differs
     */
    public boolean sameOutput(rtrBgpParam src) {
        if (!allowAsOut) {
            if (remoteAs != src.remoteAs) {
                return true;
            }
        }
        if (overridePeerOut) {
            if (remoteAs != src.remoteAs) {
                return true;
            }
        }
        if (ungrpRemAs) {
            if (remoteAs != src.remoteAs) {
                return true;
            }
        }
        if (localAs != src.localAs) {
            return true;
        }
        if (addrFams != src.addrFams) {
            return true;
        }
        if (preference != src.preference) {
            return true;
        }
        if (leakAttr != src.leakAttr) {
            return true;
        }
        if (leakRole != src.leakRole) {
            return true;
        }
        if (rpkiOut != src.rpkiOut) {
            return true;
        }
        if (remoteConfed != src.remoteConfed) {
            return true;
        }
        if (reflectClnt != src.reflectClnt) {
            return true;
        }
        if (serverClnt != src.serverClnt) {
            return true;
        }
        if (intVpnClnt != src.intVpnClnt) {
            return true;
        }
        if (accIgp != src.accIgp) {
            return true;
        }
        if (entrLab != src.entrLab) {
            return true;
        }
        if (traffEng != src.traffEng) {
            return true;
        }
        if (pmsiTun != src.pmsiTun) {
            return true;
        }
        if (connect != src.connect) {
            return true;
        }
        if (peDist != src.peDist) {
            return true;
        }
        if (pathLim != src.pathLim) {
            return true;
        }
        if (nshChain != src.nshChain) {
            return true;
        }
        if (domainPath != src.domainPath) {
            return true;
        }
        if (bfdDiscr != src.bfdDiscr) {
            return true;
        }
        if (tunEnc != src.tunEnc) {
            return true;
        }
        if (lnkSta != src.lnkSta) {
            return true;
        }
        if (attribSet != src.attribSet) {
            return true;
        }
        if (unknownsOut != src.unknownsOut) {
            return true;
        }
        if (labelPop != src.labelPop) {
            return true;
        }
        if (segRout != src.segRout) {
            return true;
        }
        if (bier != src.bier) {
            return true;
        }
        if (addpathTmode != src.addpathTmode) {
            return true;
        }
        if (sendOtrDefRou != src.sendOtrDefRou) {
            return true;
        }
        if (sendDefRou != src.sendDefRou) {
            return true;
        }
        if (nxtHopUnchgd != src.nxtHopUnchgd) {
            return true;
        }
        if (nxtHopSelf != src.nxtHopSelf) {
            return true;
        }
        if (nxtHopMltlb != src.nxtHopMltlb) {
            return true;
        }
        if (nxtHopCapa != src.nxtHopCapa) {
            return true;
        }
        if (sendCommunity != src.sendCommunity) {
            return true;
        }
        if (allowAsOut != src.allowAsOut) {
            return true;
        }
        if (rtfilterOut != src.rtfilterOut) {
            return true;
        }
        if (removePrivAsOut != src.removePrivAsOut) {
            return true;
        }
        if (overridePeerOut != src.overridePeerOut) {
            return true;
        }
        if (prflstOut == null) {
            if (src.prflstOut != null) {
                return true;
            }
        } else {
            if (src.prflstOut == null) {
                return true;
            }
            if (!prflstOut.listName.equals(src.prflstOut.listName)) {
                return true;
            }
        }
        if (roumapOut == null) {
            if (src.roumapOut != null) {
                return true;
            }
        } else {
            if (src.roumapOut == null) {
                return true;
            }
            if (!roumapOut.listName.equals(src.roumapOut.listName)) {
                return true;
            }
        }
        if (roupolOut == null) {
            if (src.roupolOut != null) {
                return true;
            }
        } else {
            if (src.roupolOut == null) {
                return true;
            }
            if (!roupolOut.listName.equals(src.roupolOut.listName)) {
                return true;
            }
        }
        if (oprflstOut == null) {
            if (src.oprflstOut != null) {
                return true;
            }
        } else {
            if (src.oprflstOut == null) {
                return true;
            }
            if (!oprflstOut.listName.equals(src.oprflstOut.listName)) {
                return true;
            }
        }
        if (oroumapOut == null) {
            if (src.oroumapOut != null) {
                return true;
            }
        } else {
            if (src.oroumapOut == null) {
                return true;
            }
            if (!oroumapOut.listName.equals(src.oroumapOut.listName)) {
                return true;
            }
        }
        if (oroupolOut == null) {
            if (src.oroupolOut != null) {
                return true;
            }
        } else {
            if (src.oroupolOut == null) {
                return true;
            }
            if (!oroupolOut.listName.equals(src.oroupolOut.listName)) {
                return true;
            }
        }
        if (vroumapOut == null) {
            if (src.vroumapOut != null) {
                return true;
            }
        } else {
            if (src.vroumapOut == null) {
                return true;
            }
            if (!vroumapOut.listName.equals(src.vroumapOut.listName)) {
                return true;
            }
        }
        if (vroupolOut == null) {
            if (src.vroupolOut != null) {
                return true;
            }
        } else {
            if (src.vroupolOut == null) {
                return true;
            }
            if (!vroupolOut.listName.equals(src.vroupolOut.listName)) {
                return true;
            }
        }
        if (wroumapOut == null) {
            if (src.wroumapOut != null) {
                return true;
            }
        } else {
            if (src.wroumapOut == null) {
                return true;
            }
            if (!wroumapOut.listName.equals(src.wroumapOut.listName)) {
                return true;
            }
        }
        if (wroupolOut == null) {
            if (src.wroupolOut != null) {
                return true;
            }
        } else {
            if (src.wroupolOut == null) {
                return true;
            }
            if (!wroupolOut.listName.equals(src.wroupolOut.listName)) {
                return true;
            }
        }
        if (eroumapOut == null) {
            if (src.eroumapOut != null) {
                return true;
            }
        } else {
            if (src.eroumapOut == null) {
                return true;
            }
            if (!eroumapOut.listName.equals(src.eroumapOut.listName)) {
                return true;
            }
        }
        if (eroupolOut == null) {
            if (src.eroupolOut != null) {
                return true;
            }
        } else {
            if (src.eroupolOut == null) {
                return true;
            }
            if (!eroupolOut.listName.equals(src.eroupolOut.listName)) {
                return true;
            }
        }
        if (rtfilterUsed == null) {
            if (src.rtfilterUsed != null) {
                return true;
            }
        } else {
            if (src.rtfilterUsed == null) {
                return true;
            }
            if (rtfilterUsed.differs(tabRoute.addType.never, src.rtfilterUsed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get parameter help
     *
     * @param l list to append
     */
    public static void getParamHelp(userHelp l) {
        l.add(null, false, 3, new int[]{4}, "remote-as", "remote as number");
        l.add(null, false, 4, new int[]{5, -1}, "any", "any autonomous system number");
        l.add(null, false, 4, new int[]{5, -1}, "<num>", "autonomous system number");
        l.add(null, false, 5, new int[]{-1}, "shutdown", "connection disabled for this peer");
        l.add(null, false, 3, new int[]{4}, "password", "set session password");
        l.add(null, false, 4, new int[]{4, -1}, "<text>", "password to use");
        l.add(null, false, 3, new int[]{4}, "authen-type", "set authentication type");
        l.add(null, false, 4, new int[]{-1}, "md5", "legacy password option");
        l.add(null, false, 4, new int[]{5}, "sha1", "ao password with sha1");
        l.add(null, false, 5, new int[]{-1}, "<num>", "key id");
        l.add(null, false, 3, new int[]{-1}, "shutdown", "connection disabled for this peer");
        l.add(null, false, 3, new int[]{4}, "description", "describe this neighbor");
        l.add(null, false, 4, new int[]{4, -1}, "<str>", "description of neighbor");
        l.add(null, false, 3, new int[]{4}, "update-source", "connection source for this peer");
        l.add(null, false, 4, new int[]{-1}, "<name:ifc>", "name of interface");
        l.add(null, false, 3, new int[]{4}, "address-family", "specify address families");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{4}, "monitor", "bgp monitor protocol for this peer");
        l.add(null, false, 4, new int[]{-1}, "<str>", "name of bmp");
        l.add(null, false, 3, new int[]{4}, "other-address", "address of peer in the other afi");
        l.add(null, false, 4, new int[]{-1}, "<addr>", "other address");
        l.add(null, false, 3, new int[]{4}, "dump", "bgp dump for this peer");
        l.add(null, false, 4, new int[]{-1}, "<str>", "name of mrt");
        l.add(null, false, 3, new int[]{4}, "buffer-size", "size of buffer");
        l.add(null, false, 4, new int[]{-1}, "<num>", "bytes in buffer");
        l.add(null, false, 3, new int[]{4}, "ttl-security", "sending ttl value");
        l.add(null, false, 4, new int[]{-1}, "<num>", "ttl value");
        l.add(null, false, 3, new int[]{4}, "tos-value", "sending tos value");
        l.add(null, false, 4, new int[]{-1}, "<num>", "tos value");
        l.add(null, false, 3, new int[]{4}, "egress-engineering", "set egress engineering");
        l.add(null, false, 4, new int[]{-1}, "<num>", "index value");
        l.add(null, false, 3, new int[]{4}, "rpki-in", "rpki ingress extcomm mode");
        l.add(null, false, 4, new int[]{-1}, "transparent", "pass everything unmodified");
        l.add(null, false, 4, new int[]{-1}, "accept", "accept remote markings");
        l.add(null, false, 4, new int[]{-1}, "fix-unset", "remove remote markings");
        l.add(null, false, 4, new int[]{-1}, "onlymiss", "rewrite if miss markings");
        l.add(null, false, 4, new int[]{-1}, "rewrite", "always rewrite markings");
        l.add(null, false, 4, new int[]{-1}, "fix-valid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-invalid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-unknown", "fixed rewrite");
        l.add(null, false, 3, new int[]{4}, "rpki-out", "rpki egress extcomm mode");
        l.add(null, false, 4, new int[]{-1}, "transparent", "pass everything unmodified");
        l.add(null, false, 4, new int[]{-1}, "accept", "accept remote markings");
        l.add(null, false, 4, new int[]{-1}, "fix-unset", "remove remote markings");
        l.add(null, false, 4, new int[]{-1}, "rewrite", "always rewrite markings");
        l.add(null, false, 4, new int[]{-1}, "onlymiss", "rewrite if miss markings");
        l.add(null, false, 4, new int[]{-1}, "fix-valid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-invalid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-unknown", "fixed rewrite");
        l.add(null, false, 3, new int[]{4}, "rpki-vpn-in", "rpki ingress extcomm mode");
        l.add(null, false, 4, new int[]{-1}, "transparent", "pass everything unmodified");
        l.add(null, false, 4, new int[]{-1}, "accept", "accept remote markings");
        l.add(null, false, 4, new int[]{-1}, "fix-unset", "remove remote markings");
        l.add(null, false, 4, new int[]{-1}, "onlymiss", "rewrite if miss markings");
        l.add(null, false, 4, new int[]{-1}, "rewrite", "always rewrite markings");
        l.add(null, false, 4, new int[]{-1}, "fix-valid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-invalid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-unknown", "fixed rewrite");
        l.add(null, false, 3, new int[]{4}, "rpki-vpn-out", "rpki egress extcomm mode");
        l.add(null, false, 4, new int[]{-1}, "transparent", "pass everything unmodified");
        l.add(null, false, 4, new int[]{-1}, "accept", "accept remote markings");
        l.add(null, false, 4, new int[]{-1}, "fix-unset", "remove remote markings");
        l.add(null, false, 4, new int[]{-1}, "rewrite", "always rewrite markings");
        l.add(null, false, 4, new int[]{-1}, "onlymiss", "rewrite if miss markings");
        l.add(null, false, 4, new int[]{-1}, "fix-valid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-invalid", "fixed rewrite");
        l.add(null, false, 4, new int[]{-1}, "fix-unknown", "fixed rewrite");
        l.add(null, false, 3, new int[]{4}, "leak-role", "remote leak prevention role");
        l.add(null, false, 4, new int[]{5, -1}, "disabled", "disable processing");
        l.add(null, false, 5, new int[]{-1}, "enforce", "enforce negotiation");
        l.add(null, false, 4, new int[]{5, -1}, "attrib", "only send otc attribute");
        l.add(null, false, 5, new int[]{-1}, "enforce", "enforce negotiation");
        l.add(null, false, 4, new int[]{5, -1}, "provider", "provider");
        l.add(null, false, 5, new int[]{-1}, "enforce", "enforce negotiation");
        l.add(null, false, 4, new int[]{5, -1}, "ix-server", "route server");
        l.add(null, false, 5, new int[]{-1}, "enforce", "enforce negotiation");
        l.add(null, false, 4, new int[]{5, -1}, "ix-client", "route server client");
        l.add(null, false, 5, new int[]{-1}, "enforce", "enforce negotiation");
        l.add(null, false, 4, new int[]{5, -1}, "customer", "customer");
        l.add(null, false, 5, new int[]{-1}, "enforce", "enforce negotiation");
        l.add(null, false, 4, new int[]{5, -1}, "peer", "peer");
        l.add(null, false, 5, new int[]{-1}, "enforce", "enforce negotiation");
        l.add(null, false, 3, new int[]{-1}, "capability-negotiation", "perform capability negosiation");
        l.add(null, false, 3, new int[]{-1}, "track-next-hop", "perform next hop tracking");
        l.add(null, false, 3, new int[]{4}, "connection-mode", "connection mode allowed");
        l.add(null, false, 4, new int[]{-1}, "active", "this router will initiate session");
        l.add(null, false, 4, new int[]{-1}, "passive", "remote router will initiate session");
        l.add(null, false, 4, new int[]{-1}, "both", "both modes allowed");
        l.add(null, false, 3, new int[]{-1}, "dynamic-capability", "allow dynamic capability exchange");
        l.add(null, false, 3, new int[]{4}, "compression", "compression mode allowed");
        l.add(null, false, 4, new int[]{-1}, "none", "not allowed");
        l.add(null, false, 4, new int[]{-1}, "receive", "receive direction");
        l.add(null, false, 4, new int[]{-1}, "transmit", "transmit direction");
        l.add(null, false, 4, new int[]{-1}, "both", "both directions");
        l.add(null, false, 3, new int[]{4}, "additional-path-rx", "additional path receive mode");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{4}, "additional-path-tx", "additional path transmit mode");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{-1}, "route-reflector-client", "reflect routes to this client");
        l.add(null, false, 3, new int[]{-1}, "confederation-peer", "confederation peer");
        l.add(null, false, 3, new int[]{-1}, "default-originate", "send default route to peer");
        l.add(null, false, 3, new int[]{-1}, "other-default-originate", "send other default route to peer");
        l.add(null, false, 3, new int[]{-1}, "aigp", "send accumulated igp attribute");
        l.add(null, false, 3, new int[]{-1}, "entropy", "send entropy label attribute");
        l.add(null, false, 3, new int[]{-1}, "traffeng", "send traffic engineering attribute");
        l.add(null, false, 3, new int[]{-1}, "pmsitun", "send provider multicast service interface tunnel attribute");
        l.add(null, false, 3, new int[]{-1}, "connector", "send connector attribute");
        l.add(null, false, 3, new int[]{-1}, "pe-distinguisher", "send pe distinguisher attribute");
        l.add(null, false, 3, new int[]{-1}, "aspath-limit", "send as path limit attribute");
        l.add(null, false, 3, new int[]{-1}, "nsh-chain", "send nsh service chain attribute");
        l.add(null, false, 3, new int[]{-1}, "domain-path", "send domain path attribute");
        l.add(null, false, 3, new int[]{-1}, "bfd-discriminator", "send bfd discriminator attribute");
        l.add(null, false, 3, new int[]{-1}, "tunenc", "send tunnel encapsulation attribute");
        l.add(null, false, 3, new int[]{-1}, "linkstate", "send link state attribute");
        l.add(null, false, 3, new int[]{-1}, "attribset", "send attribute set attribute");
        l.add(null, false, 3, new int[]{4}, "unknowns-collect", "bgp dump to use");
        l.add(null, false, 4, new int[]{-1}, "<str>", "name of mrt");
        l.add(null, false, 3, new int[]{4}, "unknowns-out", "send unknown attributes");
        l.add(null, false, 4, new int[]{-1}, "<num>", "allowed attributes");
        l.add(null, false, 3, new int[]{4}, "unknowns-in", "receive unknown attributes");
        l.add(null, false, 4, new int[]{-1}, "<num>", "allowed attributes");
        l.add(null, false, 3, new int[]{-1}, "unknowns-log", "log received unknown attributes");
        l.add(null, false, 3, new int[]{-1}, "log-end-changes", "log received origin asn changes");
        l.add(null, false, 3, new int[]{-1}, "log-nexthop-changes", "log received origin hop changes");
        l.add(null, false, 3, new int[]{4}, "log-length-changes", "log received aspath length changes");
        l.add(null, false, 4, new int[]{-1}, "<num>", "path length");
        l.add(null, false, 4, new int[]{-1}, "all", "any value");
        l.add(null, false, 3, new int[]{-1}, "label-pop", "advertise pop label");
        l.add(null, false, 3, new int[]{-1}, "lookup-database", "lookup rib before accepting");
        l.add(null, false, 3, new int[]{-1}, "lookup-reverse", "lookup dns before accepting");
        secInfoUtl.getHelp(l, 3, "ipinfo", "check peers");
        l.add(null, false, 3, new int[]{-1}, "segrout", "send segment routing attribute");
        l.add(null, false, 3, new int[]{-1}, "bier", "send bier attribute");
        l.add(null, false, 3, new int[]{-1}, "wide-aspath", "send wide aspath attribute");
        l.add(null, false, 3, new int[]{-1}, "route-refresh-original", "send original route refresh capability");
        l.add(null, false, 3, new int[]{-1}, "route-refresh-enhanced", "send enhanced route refresh capability");
        l.add(null, false, 3, new int[]{-1}, "internal-vpn-client", "preserve attributes from peer");
        l.add(null, false, 3, new int[]{-1}, "allow-as-in", "allow my as to relearn from peer");
        l.add(null, false, 3, new int[]{-1}, "allow-as-out", "allow peer as to advertised out");
        l.add(null, false, 3, new int[]{-1}, "route-target-filter-in", "use collected route target filters");
        l.add(null, false, 3, new int[]{-1}, "route-target-filter-out", "honor advertised route target filters");
        l.add(null, false, 3, new int[]{-1}, "enforce-first-as", "discard unprepended aspath from peer");
        l.add(null, false, 3, new int[]{-1}, "route-server-client", "unmodified attributes to this client");
        l.add(null, false, 3, new int[]{-1}, "remove-private-as-out", "remove private as to peer");
        l.add(null, false, 3, new int[]{-1}, "ungroup-remoteas", "consider remote asn while grouping peers");
        l.add(null, false, 3, new int[]{-1}, "remove-private-as-in", "remove private as from peer");
        l.add(null, false, 3, new int[]{-1}, "override-peer-as-out", "replace peer as to peer");
        l.add(null, false, 3, new int[]{-1}, "override-peer-as-in", "replace peer as from peer");
        l.add(null, false, 3, new int[]{-1}, "next-hop-unchanged", "send next hop unchanged to peer");
        l.add(null, false, 3, new int[]{-1}, "next-hop-multilabel", "send multiple labels to peer");
        l.add(null, false, 3, new int[]{-1}, "next-hop-capability", "send next hop capabilities to peer");
        l.add(null, false, 3, new int[]{-1}, "next-hop-self", "send next hop myself to peer");
        l.add(null, false, 3, new int[]{-1}, "next-hop-peer", "set next hop to peer address");
        l.add(null, false, 3, new int[]{4}, "proxy-profile", "proxy profile to use");
        l.add(null, false, 4, new int[]{5}, "<name:prx>", "proxy profile name");
        l.add(null, false, 5, new int[]{6}, "<addr>", "remote address");
        l.add(null, false, 6, new int[]{-1}, "<num>", "remote port");
        l.add(null, false, 3, new int[]{4}, "backup-peer", "keep down if an other peer is up");
        l.add(null, false, 4, new int[]{-1}, "<addr>", "other address");
        l.add(null, false, 3, new int[]{4, -1}, "bfd-trigger", "enable bfd triggered down");
        l.add(null, false, 4, new int[]{-1}, "strict", "enable strict bfd triggered down");
        l.add(null, false, 3, new int[]{4}, "multiple-labels", "advertise multiple labels capability");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{4}, "graceful-restart", "advertise graceful restart capability");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{4}, "longlived-graceful", "advertise long lived graceful restart capability");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{4}, "extended-nexthop-current", "advertise extended nexthop capability");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{4}, "extended-nexthop-other", "advertise extended nexthop capability");
        getAfiList(l, 4, new int[]{4, -1}, "use", true);
        l.add(null, false, 3, new int[]{4, -1}, "hostname", "advertise hostname capability");
        l.add(null, false, 4, new int[]{-1}, "domain", "advertise domain too");
        l.add(null, false, 3, new int[]{-1}, "software", "advertise software version capability");
        l.add(null, false, 3, new int[]{-1}, "extended-open", "send open in extended format");
        l.add(null, false, 3, new int[]{-1}, "extended-update", "advertise extended update capability");
        l.add(null, false, 3, new int[]{-1}, "unidirection", "not advertise when receiving");
        l.add(null, false, 3, new int[]{-1}, "fall-over", "track outgoing interface");
        l.add(null, false, 3, new int[]{-1}, "ha-mode", "save state");
        l.add(null, false, 3, new int[]{4}, "attribute-filter", "filter received attributes");
        l.add(null, false, 4, new int[]{-1}, "<num>", "filtered attributes");
        l.add(null, false, 3, new int[]{-1}, "soft-reconfiguration", "enable soft reconfiguration");
        l.add(null, false, 3, new int[]{4}, "maximum-clones", "maximum number of accepted neighbors");
        l.add(null, false, 4, new int[]{-1}, "<num>", "prefix count");
        l.add(null, false, 3, new int[]{4}, "maximum-prefix-in", "maximum number of accepted prefixes");
        l.add(null, false, 4, new int[]{5}, "<num>", "prefix count");
        l.add(null, false, 5, new int[]{-1}, "<num>", "warning percent");
        l.add(null, false, 3, new int[]{4}, "maximum-prefix-out", "maximum number of advertised prefixes");
        l.add(null, false, 4, new int[]{5}, "<num>", "prefix count");
        l.add(null, false, 5, new int[]{-1}, "<num>", "warning percent");
        l.add(null, false, 3, new int[]{4}, "dampening", "route flap dampening of prefixes");
        l.add(null, false, 4, new int[]{5}, "<num>", "withdraw penalty");
        l.add(null, false, 5, new int[]{6}, "<num>", "announce penalty");
        l.add(null, false, 6, new int[]{7}, "<num>", "minimum penalty");
        l.add(null, false, 7, new int[]{8}, "<num>", "maximum penalty");
        l.add(null, false, 8, new int[]{9}, "<num>", "suppress threshold");
        l.add(null, false, 9, new int[]{10}, "<num>", "reuse threshold");
        l.add(null, false, 10, new int[]{-1}, "<num>", "half life time in ms");
        l.add(null, false, 3, new int[]{4}, "send-community", "send community to peer");
        l.add(null, false, 4, new int[]{4, -1}, "standard", "send standard community");
        l.add(null, false, 4, new int[]{4, -1}, "extended", "send extended community");
        l.add(null, false, 4, new int[]{4, -1}, "large", "send large community");
        l.add(null, false, 4, new int[]{4, -1}, "both", "send std+ext communities");
        l.add(null, false, 4, new int[]{4, -1}, "all", "send std+ext+lrg communities");
        l.add(null, false, 4, new int[]{4, -1}, "none", "send no community");
        l.add(null, false, 3, new int[]{4}, "local-as", "local as number");
        l.add(null, false, 4, new int[]{-1}, "<num>", "autonomous system number");
        l.add(null, false, 3, new int[]{4}, "advertisement-interval-tx", "time between sending updates");
        l.add(null, false, 3, new int[]{4}, "advertisement-interval-rx", "time between receiving updates");
        l.add(null, false, 4, new int[]{-1}, "<num>", "interval in ms");
        l.add(null, false, 3, new int[]{4}, "dmz-link-bw", "set dmz link bandwidth");
        l.add(null, false, 4, new int[]{-1}, "<num>", "link bandwidth in kb");
        l.add(null, false, 3, new int[]{-1}, "spf-stub", "stub peer");
        l.add(null, false, 3, new int[]{4}, "spf-metric", "set spf metric");
        l.add(null, false, 4, new int[]{-1}, "<num>", "link metric");
        l.add(null, false, 3, new int[]{4}, "randomize-startup", "set session startup timers");
        l.add(null, false, 4, new int[]{5}, "<num>", "minimum in ms");
        l.add(null, false, 5, new int[]{-1}, "<num>", "maximum in ms");
        l.add(null, false, 3, new int[]{4}, "timer", "neighbor keepalive times");
        l.add(null, false, 4, new int[]{5}, "<num>", "keepalive in ms");
        l.add(null, false, 5, new int[]{-1}, "<num>", "hold time in ms");
        l.add(null, false, 3, new int[]{4}, "distance", "administrative distance of routes");
        l.add(null, false, 4, new int[]{-1}, "<num>", "set administrative distance");
        l.add(null, false, 3, new int[]{4}, "preference", "local preference of routes");
        l.add(null, false, 4, new int[]{-1}, "<num>", "set local preference");
        l.add(null, false, 3, new int[]{4}, "route-map-in", "process prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "route-map-out", "process prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "route-policy-in", "process prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "route-policy-out", "process prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "prefix-list-in", "filter prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:pl>", "name of prefix list");
        l.add(null, false, 3, new int[]{4}, "prefix-list-out", "filter prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:pl>", "name of prefix list");
        l.add(null, false, 3, new int[]{4}, "other-route-map-in", "process other prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "other-route-map-out", "process other prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "other-route-policy-in", "process other prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "other-route-policy-out", "process other prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "other-prefix-list-in", "filter other prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:pl>", "name of prefix list");
        l.add(null, false, 3, new int[]{4}, "other-prefix-list-out", "filter other prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:pl>", "name of prefix list");
        l.add(null, false, 3, new int[]{4}, "vpn-route-map-in", "process vpn prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "vpn-route-map-out", "process vpn prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "vpn-route-policy-in", "process vpn prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "vpn-route-policy-out", "process vpn prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "ovpn-route-map-in", "process other vpn prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "ovpn-route-map-out", "process other vpn prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "ovpn-route-policy-in", "process other vpn prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "ovpn-route-policy-out", "process other vpn prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "evpn-route-map-in", "process evpn prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "evpn-route-map-out", "process evpn prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rm>", "name of route map");
        l.add(null, false, 3, new int[]{4}, "evpn-route-policy-in", "process evpn prefixes in ingress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
        l.add(null, false, 3, new int[]{4}, "evpn-route-policy-out", "process evpn prefixes in egress updates");
        l.add(null, false, 4, new int[]{-1}, "<name:rpl>", "name of route policy");
    }

    /**
     * get configuration
     *
     * @param beg beginning
     * @param nei prepending
     * @param filter filter defaults
     * @return list of text
     */
    public List<String> getParamCfg(String beg, String nei, int filter) {
        List<String> l = new ArrayList<String>();
        if (template == null) {
            l.add(beg + cmds.negated + cmds.tabulator + nei + "template");
        } else {
            l.add(beg + nei + "template " + template.tempName);
        }
        String s = "" + bits.num2str(remoteAs);
        if (remoteAny) {
            s = "any";
        }
        l.add(beg + nei + "remote-as " + s);
        cmds.cfgLine(l, description == null, beg, nei + "description", description);
        if (keyId < 0) {
            l.add(beg + nei + "authen-type md5");
        } else {
            l.add(beg + nei + "authen-type sha1 " + keyId);
        }
        cmds.cfgLine(l, passwd == null, beg, nei + "password", authLocal.passwdEncode(passwd, (filter & 2) != 0));
        l.add(beg + nei + "local-as " + bits.num2str(localAs));
        secInfoUtl.getConfig(l, ipInfoCfg, beg + nei + "ipinfo ");
        l.add(beg + nei + "advertisement-interval-tx " + advertIntTx);
        l.add(beg + nei + "advertisement-interval-rx " + advertIntRx);
        l.add(beg + nei + "address-family" + mask2string(addrFams));
        l.add(beg + nei + "distance " + distance);
        l.add(beg + nei + "preference " + preference);
        l.add(beg + nei + "timer " + keepAlive + " " + holdTimer);
        l.add(beg + nei + "dmz-link-bw " + dmzLinkBw);
        l.add(beg + nei + "spf-metric " + spfMetric);
        cmds.cfgLine(l, !spfStub, beg, nei + "spf-stub", "");
        l.add(beg + nei + "randomize-startup " + randomStartF + " " + randomStartL);
        switch (socketMode) {
            case 1:
                s = "active";
                break;
            case 2:
                s = "passive";
                break;
            default:
                s = "both";
                break;
        }
        l.add(beg + nei + "connection-mode " + s);
        cmds.cfgLine(l, !dynamicCapab, beg, nei + "dynamic-capability", "");
        switch (compressMode) {
            case 1:
                s = "receive";
                break;
            case 2:
                s = "transmit";
                break;
            case 3:
                s = "both";
                break;
            default:
                s = "none";
                break;
        }
        cmds.cfgLine(l, compressMode == 0, beg, nei + "compression", s);
        l.add(beg + nei + "buffer-size " + bufferSize);
        l.add(beg + nei + "ttl-security " + ttlSecurity);
        l.add(beg + nei + "tos-value " + tosValue);
        l.add(beg + nei + "additional-path-rx" + mask2string(addpathRmode));
        l.add(beg + nei + "additional-path-tx" + mask2string(addpathTmode));
        cmds.cfgLine(l, !shutdown, beg, nei + "shutdown", "");
        if (srcIface == null) {
            l.add(beg + cmds.negated + cmds.tabulator + nei + "update-source");
        } else {
            l.add(beg + nei + "update-source " + srcIface.name);
        }
        if (monitor == null) {
            l.add(beg + cmds.negated + cmds.tabulator + nei + "monitor");
        } else {
            l.add(beg + nei + "monitor " + monitor.monName);
        }
        if (dump == null) {
            l.add(beg + cmds.negated + cmds.tabulator + nei + "dump");
        } else {
            l.add(beg + nei + "dump " + dump.dumpName);
        }
        cmds.cfgLine(l, otherAdr == null, beg, nei + "other-address", "" + otherAdr);
        cmds.cfgLine(l, backupPeer == null, beg, nei + "backup-peer", "" + backupPeer);
        cmds.cfgLine(l, proxy2use == null, beg, nei + "proxy-profile", proxy2use + " " + proxy2adr + " " + proxy2prt);
        if (bfdTrigger == 2) {
            s = "strict";
        } else {
            s = "";
        }
        cmds.cfgLine(l, bfdTrigger == 0, beg, nei + "bfd-trigger", s);
        cmds.cfgLine(l, !ungrpRemAs, beg, nei + "ungroup-remoteas", "");
        cmds.cfgLine(l, !softReconfig, beg, nei + "soft-reconfiguration", "");
        l.add(beg + nei + "multiple-labels" + mask2string(multiLabel));
        l.add(beg + nei + "graceful-restart" + mask2string(graceRestart));
        l.add(beg + nei + "longlived-graceful" + mask2string(llGraceRestart));
        l.add(beg + nei + "extended-nexthop-current" + mask2string(extNextCur));
        l.add(beg + nei + "extended-nexthop-other" + mask2string(extNextOtr));
        s = "";
        if (hostname > 1) {
            s = "domain";
        }
        cmds.cfgLine(l, hostname < 1, beg, nei + "hostname", s);
        cmds.cfgLine(l, !software, beg, nei + "software", "");
        cmds.cfgLine(l, !extOpen, beg, nei + "extended-open", "");
        cmds.cfgLine(l, !extUpdate, beg, nei + "extended-update", "");
        cmds.cfgLine(l, !unidirection, beg, nei + "unidirection", "");
        cmds.cfgLine(l, !fallOver, beg, nei + "fall-over", "");
        cmds.cfgLine(l, !haMode, beg, nei + "ha-mode", "");
        cmds.cfgLine(l, attribFilter == null, beg, nei + "attribute-filter", "" + attribFilter);
        cmds.cfgLine(l, !sendDefRou, beg, nei + "default-originate", "");
        cmds.cfgLine(l, !sendOtrDefRou, beg, nei + "other-default-originate", "");
        cmds.cfgLine(l, !intVpnClnt, beg, nei + "internal-vpn-client", "");
        cmds.cfgLine(l, !allowAsIn, beg, nei + "allow-as-in", "");
        cmds.cfgLine(l, !allowAsOut, beg, nei + "allow-as-out", "");
        cmds.cfgLine(l, !rtfilterIn, beg, nei + "route-target-filter-in", "");
        cmds.cfgLine(l, !rtfilterOut, beg, nei + "route-target-filter-out", "");
        cmds.cfgLine(l, !enforceFirst, beg, nei + "enforce-first-as", "");
        cmds.cfgLine(l, maxClones < 1, beg, nei + "maximum-clones", "" + maxClones);
        cmds.cfgLine(l, maxPrxInCnt < 1, beg, nei + "maximum-prefix-in", maxPrxInCnt + " " + maxPrxInPrc);
        cmds.cfgLine(l, maxPrxOutCnt < 1, beg, nei + "maximum-prefix-out", maxPrxOutCnt + " " + maxPrxOutPrc);
        cmds.cfgLine(l, (dampenWthd + dampenAnno) < 1, beg, nei + "dampening", dampenWthd + " " + dampenAnno + " " + dampenMinp + " " + dampenMaxp + " " + dampenSupp + " " + dampenReus + " " + dampenHalf);
        cmds.cfgLine(l, !serverClnt, beg, nei + "route-server-client", "");
        cmds.cfgLine(l, !removePrivAsOut, beg, nei + "remove-private-as-out", "");
        cmds.cfgLine(l, !removePrivAsIn, beg, nei + "remove-private-as-in", "");
        cmds.cfgLine(l, !overridePeerOut, beg, nei + "override-peer-as-out", "");
        cmds.cfgLine(l, !overridePeerIn, beg, nei + "override-peer-as-in", "");
        cmds.cfgLine(l, !accIgp, beg, nei + "aigp", "");
        cmds.cfgLine(l, !entrLab, beg, nei + "entropy", "");
        cmds.cfgLine(l, !traffEng, beg, nei + "traffeng", "");
        cmds.cfgLine(l, !pmsiTun, beg, nei + "pmsitun", "");
        cmds.cfgLine(l, !connect, beg, nei + "connector", "");
        cmds.cfgLine(l, !peDist, beg, nei + "pe-distinguisher", "");
        cmds.cfgLine(l, !pathLim, beg, nei + "aspath-limit", "");
        cmds.cfgLine(l, !nshChain, beg, nei + "nsh-chain", "");
        cmds.cfgLine(l, !domainPath, beg, nei + "domain-path", "");
        cmds.cfgLine(l, !bfdDiscr, beg, nei + "bfd-discriminator", "");
        cmds.cfgLine(l, !tunEnc, beg, nei + "tunenc", "");
        cmds.cfgLine(l, !lnkSta, beg, nei + "linkstate", "");
        cmds.cfgLine(l, !attribSet, beg, nei + "attribset", "");
        if (unknownsColl == null) {
            l.add(beg + cmds.negated + cmds.tabulator + nei + "unknowns-collect");
        } else {
            l.add(beg + nei + "unknowns-collect " + unknownsColl.dumpName);
        }
        cmds.cfgLine(l, unknownsOut == null, beg, nei + "unknowns-out", "" + unknownsOut);
        cmds.cfgLine(l, unknownsIn == null, beg, nei + "unknowns-in", "" + unknownsIn);
        cmds.cfgLine(l, !unknownsLog, beg, nei + "unknowns-log", "");
        cmds.cfgLine(l, !endChanges, beg, nei + "log-end-changes", "");
        cmds.cfgLine(l, !hopChanges, beg, nei + "log-nexthop-changes", "");
        cmds.cfgLine(l, lengthChanges == null, beg, nei + "log-length-changes", "" + lengthChanges);
        cmds.cfgLine(l, !segRout, beg, nei + "segrout", "");
        cmds.cfgLine(l, !bier, beg, nei + "bier", "");
        cmds.cfgLine(l, !wideAsPath, beg, nei + "wide-aspath", "");
        cmds.cfgLine(l, !routeRefreshOld, beg, nei + "route-refresh-original", "");
        cmds.cfgLine(l, !routeRefreshNew, beg, nei + "route-refresh-enhanced", "");
        cmds.cfgLine(l, egressEng == 0, beg, nei + "egress-engineering", "" + egressEng);
        s = rtrBgpUtil.leakRole2string(rtrBgpUtil.leakInverter(leakRole), leakAttr);
        if (leakForce) {
            s += " enforce";
        }
        l.add(beg + nei + "leak-role " + s);
        l.add(beg + nei + "rpki-in " + rtrBgpUtil.rpkiMode2string(rpkiIn));
        l.add(beg + nei + "rpki-out " + rtrBgpUtil.rpkiMode2string(rpkiOut));
        l.add(beg + nei + "rpki-vpn-in " + rtrBgpUtil.rpkiMode2string(vpkiIn));
        l.add(beg + nei + "rpki-vpn-out " + rtrBgpUtil.rpkiMode2string(vpkiOut));
        cmds.cfgLine(l, !labelPop, beg, nei + "label-pop", "");
        cmds.cfgLine(l, !capaNego, beg, nei + "capability-negotiation", "");
        cmds.cfgLine(l, !trackNxthop, beg, nei + "track-next-hop", "");
        cmds.cfgLine(l, !reflectClnt, beg, nei + "route-reflector-client", "");
        cmds.cfgLine(l, !remoteConfed, beg, nei + "confederation-peer", "");
        cmds.cfgLine(l, !nxtHopUnchgd, beg, nei + "next-hop-unchanged", "");
        cmds.cfgLine(l, !nxtHopMltlb, beg, nei + "next-hop-multilabel", "");
        cmds.cfgLine(l, !nxtHopCapa, beg, nei + "next-hop-capability", "");
        cmds.cfgLine(l, !nxtHopSelf, beg, nei + "next-hop-self", "");
        cmds.cfgLine(l, !nxtHopPeer, beg, nei + "next-hop-peer", "");
        switch (sendCommunity) {
            case 1:
                s = "standard";
                break;
            case 2:
                s = "extended";
                break;
            case 3:
                s = "standard extended";
                break;
            case 4:
                s = "large";
                break;
            case 5:
                s = "standard large";
                break;
            case 6:
                s = "extended large";
                break;
            case 7:
                s = "all";
                break;
            default:
                s = "none";
                break;
        }
        cmds.cfgLine(l, sendCommunity == 0, beg, nei + "send-community", s);
        cmds.cfgLine(l, prflstIn == null, beg, nei + "prefix-list-in", "" + prflstIn);
        cmds.cfgLine(l, prflstOut == null, beg, nei + "prefix-list-out", "" + prflstOut);
        cmds.cfgLine(l, roumapIn == null, beg, nei + "route-map-in", "" + roumapIn);
        cmds.cfgLine(l, roumapOut == null, beg, nei + "route-map-out", "" + roumapOut);
        cmds.cfgLine(l, roupolIn == null, beg, nei + "route-policy-in", "" + roupolIn);
        cmds.cfgLine(l, roupolOut == null, beg, nei + "route-policy-out", "" + roupolOut);
        cmds.cfgLine(l, oprflstIn == null, beg, nei + "other-prefix-list-in", "" + oprflstIn);
        cmds.cfgLine(l, oprflstOut == null, beg, nei + "other-prefix-list-out", "" + oprflstOut);
        cmds.cfgLine(l, oroumapIn == null, beg, nei + "other-route-map-in", "" + oroumapIn);
        cmds.cfgLine(l, oroumapOut == null, beg, nei + "other-route-map-out", "" + oroumapOut);
        cmds.cfgLine(l, oroupolIn == null, beg, nei + "other-route-policy-in", "" + oroupolIn);
        cmds.cfgLine(l, oroupolOut == null, beg, nei + "other-route-policy-out", "" + oroupolOut);
        cmds.cfgLine(l, vroumapIn == null, beg, nei + "vpn-route-map-in", "" + vroumapIn);
        cmds.cfgLine(l, vroumapOut == null, beg, nei + "vpn-route-map-out", "" + vroumapOut);
        cmds.cfgLine(l, vroupolIn == null, beg, nei + "vpn-route-policy-in", "" + vroupolIn);
        cmds.cfgLine(l, vroupolOut == null, beg, nei + "vpn-route-policy-out", "" + vroupolOut);
        cmds.cfgLine(l, wroumapIn == null, beg, nei + "ovpn-route-map-in", "" + wroumapIn);
        cmds.cfgLine(l, wroumapOut == null, beg, nei + "ovpn-route-map-out", "" + wroumapOut);
        cmds.cfgLine(l, wroupolIn == null, beg, nei + "ovpn-route-policy-in", "" + wroupolIn);
        cmds.cfgLine(l, wroupolOut == null, beg, nei + "ovpn-route-policy-out", "" + wroupolOut);
        cmds.cfgLine(l, eroumapIn == null, beg, nei + "evpn-route-map-in", "" + eroumapIn);
        cmds.cfgLine(l, eroumapOut == null, beg, nei + "evpn-route-map-out", "" + eroumapOut);
        cmds.cfgLine(l, eroupolIn == null, beg, nei + "evpn-route-policy-in", "" + eroupolIn);
        cmds.cfgLine(l, eroupolOut == null, beg, nei + "evpn-route-policy-out", "" + eroupolOut);
        if ((filter & 1) == 0) {
            return l;
        }
        l = userFilter.filterText(l, cfgRtr.defaultF);
        if (template == null) {
            return l;
        }
        List<String> t = template.getParamCfg(beg, nei, filter);
        t = userFilter.filterText(t, cfgRtr.defaultF);
        for (int i = 1; i < t.size(); i++) {
            String a = t.get(i);
            int o = l.indexOf(a);
            if (o < 0) {
                continue;
            }
            l.remove(o);
        }
        return l;
    }

    /**
     * template configuration
     *
     * @param cmd command to do
     * @param negated negated
     */
    public abstract void doTempCfg(String cmd, boolean negated);

    /**
     * get configuration
     *
     * @param l list to append
     * @param beg beginning
     * @param filter filter defaults
     */
    public abstract void getConfig(List<String> l, String beg, int filter);

    /**
     * check shutdown policy
     *
     * @return true if shutdown, false if not
     */
    public boolean checkShutdown() {
        if (shutdown) {
            return true;
        }
        if (backupPeer != null) {
            rtrBgpNeigh per = lower.findPeer(backupPeer);
            if (per != null) {
                if (per.conn.ready2adv) {
                    return true;
                }
            }
        }
        if (!lower.safeEbgp) {
            return false;
        }
        if (remoteAs == localAs) {
            return false;
        }
        if ((addrFams & mskUni) != 0) {
            if ((roumapIn == null) && (roupolIn == null) && (prflstIn == null)) {
                return true;
            }
            if ((roumapOut == null) && (roupolOut == null) && (prflstOut == null)) {
                return true;
            }
        }
        if ((addrFams & mskOuni) != 0) {
            if ((oroumapIn == null) && (oroupolIn == null) && (oprflstIn == null)) {
                return true;
            }
            if ((oroumapOut == null) && (oroupolOut == null) && (oprflstOut == null)) {
                return true;
            }
        }
        return false;
    }

    /**
     * parse configuration command
     *
     * @param cmd command to parse
     * @param negated negated
     * @return false on success, true on error
     */
    public boolean setParamCfg(cmds cmd, boolean negated) {
        String s = cmd.word();
        if (s.equals("template")) {
            if (negated) {
                template = null;
                return false;
            }
            if (isTemplate) {
                return true;
            }
            rtrBgpTemp t = lower.findTemp(cmd.word());
            if (t == null) {
                cmd.error("no such template");
                return false;
            }
            copyFrom(t);
            template = t;
            shutdown |= cmd.word().equals("shutdown");
            return false;
        }
        if (isTemplate) {
            doTempCfg(s + " " + cmd.getRemaining(), negated);
        }
        if (s.equals("remote-as")) {
            s = cmd.word();
            remoteAs = bits.str2num(s);
            if (s.equals("any")) {
                remoteAs = -1;
                remoteAny = true;
            } else {
                remoteAny = false;
            }
            if (negated) {
                remoteAs = 0;
                return false;
            }
            if (remoteAs == localAs) {
                distance = lower.distantInt;
            } else {
                distance = lower.distantExt;
            }
            shutdown |= cmd.word().equals("shutdown");
            return false;
        }
        if (s.equals("local-as")) {
            localAs = bits.str2num(cmd.word());
            if (negated) {
                localAs = lower.localAs;
            }
            return false;
        }
        if (s.equals("address-family")) {
            addrFams = string2mask(cmd);
            if (negated) {
                addrFams = lower.addrFams;
            }
            return false;
        }
        if (s.equals("update-source")) {
            if (negated) {
                srcIface = null;
                return false;
            }
            cfgIfc res = cfgAll.ifcFind(cmd.word(), 0);
            if (res == null) {
                cmd.error("no such interface");
                return false;
            }
            if (res.vrfFor != lower.vrfCore) {
                cmd.error("in other vrf");
                return false;
            }
            srcIface = res;
            return false;
        }
        if (s.equals("other-address")) {
            if (negated) {
                otherAdr = null;
                return false;
            }
            otherAdr = new addrIP();
            otherAdr.fromString(cmd.word());
            return false;
        }
        if (s.equals("dump")) {
            if (negated) {
                dump = null;
                return false;
            }
            rtrBgpMrt mon = new rtrBgpMrt(cmd.word());
            dump = lower.dmps.find(mon);
            if (dump == null) {
                cmd.error("no such dump");
                return false;
            }
            return false;
        }
        if (s.equals("monitor")) {
            if (negated) {
                monitor = null;
                return false;
            }
            rtrBgpMon mon = new rtrBgpMon(null, cmd.word());
            monitor = lower.mons.find(mon);
            if (monitor == null) {
                cmd.error("no such monitor");
                return false;
            }
            return false;
        }
        if (s.equals("authen-type")) {
            keyId = -1;
            if (negated) {
                return false;
            }
            if (!cmd.word().equals("sha1")) {
                return false;
            }
            keyId = bits.str2num(cmd.word());
            return false;
        }
        if (s.equals("password")) {
            if (negated) {
                passwd = null;
                return false;
            }
            passwd = authLocal.passwdDecode(cmd.getRemaining());
            return false;
        }
        if (s.equals("description")) {
            if (negated) {
                description = null;
                return false;
            }
            description = cmd.getRemaining();
            return false;
        }
        if (s.equals("distance")) {
            distance = bits.str2num(cmd.word());
            return false;
        }
        if (s.equals("preference")) {
            preference = bits.str2num(cmd.word());
            return false;
        }
        if (s.equals("dmz-link-bw")) {
            dmzLinkBw = bits.str2num(cmd.word());
            if (negated) {
                dmzLinkBw = -1;
            }
            return false;
        }
        if (s.equals("spf-metric")) {
            spfMetric = bits.str2num(cmd.word());
            if (negated) {
                spfMetric = 10;
            }
            return false;
        }
        if (s.equals("spf-stub")) {
            spfStub = !negated;
            return false;
        }
        if (s.equals("randomize-startup")) {
            randomStartF = bits.str2num(cmd.word());
            randomStartL = bits.str2num(cmd.word());
            if (negated) {
                randomStartF = 2;
                randomStartL = 15;
            }
            return false;
        }
        if (s.equals("timer")) {
            keepAlive = bits.str2num(cmd.word());
            holdTimer = bits.str2num(cmd.word());
            return false;
        }
        if (s.equals("proxy-profile")) {
            if (negated) {
                proxy2use = null;
                proxy2adr = null;
                proxy2prt = 0;
                return false;
            }
            cfgProxy prx = cfgAll.proxyFind(cmd.word(), false);
            if (prx == null) {
                cmd.error("no such proxy");
                return false;
            }
            proxy2adr = new addrIP();
            proxy2adr.fromString(cmd.word());
            proxy2prt = bits.str2num(cmd.word());
            proxy2use = prx.proxy;
            return false;
        }
        if (s.equals("backup-peer")) {
            if (negated) {
                backupPeer = null;
                return false;
            }
            backupPeer = new addrIP();
            backupPeer.fromString(cmd.word());
            return false;
        }
        if (s.equals("bfd-trigger")) {
            if (negated) {
                bfdTrigger = 0;
                return false;
            }
            bfdTrigger = 1;
            if (cmd.word().equals("strict")) {
                bfdTrigger = 2;
            }
            return false;
        }
        if (s.equals("soft-reconfiguration")) {
            softReconfig = !negated;
            return false;
        }
        if (s.equals("extended-nexthop-current")) {
            extNextCur = string2mask(cmd);
            if (negated) {
                extNextCur = 0;
            }
            return false;
        }
        if (s.equals("extended-nexthop-other")) {
            extNextOtr = string2mask(cmd);
            if (negated) {
                extNextOtr = 0;
            }
            return false;
        }
        if (s.equals("multiple-labels")) {
            multiLabel = string2mask(cmd);
            if (negated) {
                multiLabel = 0;
            }
            return false;
        }
        if (s.equals("graceful-restart")) {
            graceRestart = string2mask(cmd);
            if (negated) {
                graceRestart = 0;
            }
            return false;
        }
        if (s.equals("longlived-graceful")) {
            llGraceRestart = string2mask(cmd);
            if (negated) {
                llGraceRestart = 0;
            }
            return false;
        }
        if (s.equals("hostname")) {
            if (negated) {
                hostname = 0;
                return false;
            }
            hostname = 1;
            s = cmd.word();
            if (s.equals("domain")) {
                hostname = 2;
            }
            return false;
        }
        if (s.equals("software")) {
            software = !negated;
            return false;
        }
        if (s.equals("extended-open")) {
            extOpen = !negated;
            return false;
        }
        if (s.equals("extended-update")) {
            extUpdate = !negated;
            return false;
        }
        if (s.equals("unidirection")) {
            unidirection = !negated;
            return false;
        }
        if (s.equals("shutdown")) {
            shutdown = !negated;
            return false;
        }
        if (s.equals("connection-mode")) {
            socketMode = 3;
            if (negated) {
                return false;
            }
            s = cmd.word();
            if (s.equals("active")) {
                socketMode = 1;
            }
            if (s.equals("passive")) {
                socketMode = 2;
            }
            return false;
        }
        if (s.equals("dynamic-capability")) {
            dynamicCapab = !negated;
            return false;
        }
        if (s.equals("compression")) {
            compressMode = 0;
            if (negated) {
                return false;
            }
            s = cmd.word();
            if (s.equals("receive")) {
                compressMode = 1;
            }
            if (s.equals("transmit")) {
                compressMode = 2;
            }
            if (s.equals("both")) {
                compressMode = 3;
            }
            return false;
        }
        if (s.equals("advertisement-interval-tx")) {
            advertIntTx = bits.str2num(cmd.word());
            if (negated) {
                advertIntTx = 0;
            }
            return false;
        }
        if (s.equals("advertisement-interval-rx")) {
            advertIntRx = bits.str2num(cmd.word());
            if (negated) {
                advertIntRx = 0;
            }
            return false;
        }
        if (s.equals("ttl-security")) {
            ttlSecurity = bits.str2num(cmd.word());
            return false;
        }
        if (s.equals("tos-value")) {
            tosValue = bits.str2num(cmd.word());
            return false;
        }
        if (s.equals("buffer-size")) {
            bufferSize = bits.str2num(cmd.word());
            return false;
        }
        if (s.equals("additional-path-rx")) {
            addpathRmode = string2mask(cmd);
            if (negated) {
                addpathRmode = 0;
                return false;
            }
            return false;
        }
        if (s.equals("additional-path-tx")) {
            addpathTmode = string2mask(cmd);
            if (negated) {
                addpathTmode = 0;
                return false;
            }
            return false;
        }
        if (s.equals("internal-vpn-client")) {
            intVpnClnt = !negated;
            return false;
        }
        if (s.equals("allow-as-in")) {
            allowAsIn = !negated;
            return false;
        }
        if (s.equals("allow-as-out")) {
            allowAsOut = !negated;
            return false;
        }
        if (s.equals("route-target-filter-in")) {
            rtfilterIn = !negated;
            return false;
        }
        if (s.equals("route-target-filter-out")) {
            rtfilterOut = !negated;
            return false;
        }
        if (s.equals("enforce-first-as")) {
            enforceFirst = !negated;
            return false;
        }
        if (s.equals("dampening")) {
            dampenWthd = bits.str2num(cmd.word());
            dampenAnno = bits.str2num(cmd.word());
            dampenMinp = bits.str2num(cmd.word());
            dampenMaxp = bits.str2num(cmd.word());
            dampenSupp = bits.str2num(cmd.word());
            dampenReus = bits.str2num(cmd.word());
            dampenHalf = bits.str2num(cmd.word());
            dampenPfxs = new tabGen<rtrBgpDamp>();
            if (!negated) {
                return false;
            }
            dampenWthd = 0;
            dampenAnno = 0;
            dampenMinp = 0;
            dampenMaxp = 0;
            dampenSupp = 0;
            dampenReus = 0;
            dampenHalf = 0;
            dampenPfxs = null;
            return false;
        }
        if (s.equals("maximum-clones")) {
            maxClones = bits.str2num(cmd.word());
            if (!negated) {
                return false;
            }
            maxClones = 0;
            return false;
        }
        if (s.equals("maximum-prefix-in")) {
            maxPrxInCnt = bits.str2num(cmd.word());
            maxPrxInPrc = bits.str2num(cmd.word());
            if (!negated) {
                return false;
            }
            maxPrxInCnt = 0;
            maxPrxInPrc = 0;
            return false;
        }
        if (s.equals("maximum-prefix-out")) {
            maxPrxOutCnt = bits.str2num(cmd.word());
            maxPrxOutPrc = bits.str2num(cmd.word());
            if (!negated) {
                return false;
            }
            maxPrxOutCnt = 0;
            maxPrxOutPrc = 0;
            return false;
        }
        if (s.equals("route-server-client")) {
            serverClnt = !negated;
            return false;
        }
        if (s.equals("aigp")) {
            accIgp = !negated;
            return false;
        }
        if (s.equals("entropy")) {
            entrLab = !negated;
            return false;
        }
        if (s.equals("traffeng")) {
            traffEng = !negated;
            return false;
        }
        if (s.equals("pmsitun")) {
            pmsiTun = !negated;
            return false;
        }
        if (s.equals("connector")) {
            connect = !negated;
            return false;
        }
        if (s.equals("pe-distinguisher")) {
            peDist = !negated;
            return false;
        }
        if (s.equals("aspath-limit")) {
            pathLim = !negated;
            return false;
        }
        if (s.equals("nsh-chain")) {
            nshChain = !negated;
            return false;
        }
        if (s.equals("domain-path")) {
            domainPath = !negated;
            return false;
        }
        if (s.equals("bfd-discriminator")) {
            bfdDiscr = !negated;
            return false;
        }
        if (s.equals("tunenc")) {
            tunEnc = !negated;
            return false;
        }
        if (s.equals("linkstate")) {
            lnkSta = !negated;
            return false;
        }
        if (s.equals("attribset")) {
            attribSet = !negated;
            return false;
        }
        if (s.equals("unknowns-collect")) {
            if (negated) {
                unknownsColl = null;
                return false;
            }
            rtrBgpMrt mon = new rtrBgpMrt(cmd.word());
            unknownsColl = lower.dmps.find(mon);
            if (unknownsColl == null) {
                cmd.error("no such dump");
                return false;
            }
            return false;
        }
        if (s.equals("unknowns-log")) {
            unknownsLog = !negated;
            return false;
        }
        if (s.equals("log-length-changes")) {
            if (negated) {
                lengthChanges = null;
                return false;
            }
            lengthChanges = new tabIntMatcher();
            if (lengthChanges.fromString(cmd.word())) {
                lengthChanges = null;
                return false;
            }
            return false;
        }
        if (s.equals("log-end-changes")) {
            endChanges = !negated;
            return false;
        }
        if (s.equals("log-nexthop-changes")) {
            hopChanges = !negated;
            return false;
        }
        if (s.equals("unknowns-out")) {
            if (negated) {
                unknownsOut = null;
                return false;
            }
            unknownsOut = new tabIntMatcher();
            unknownsOut.fromString(cmd.word());
            return false;
        }
        if (s.equals("unknowns-in")) {
            if (negated) {
                unknownsIn = null;
                return false;
            }
            unknownsIn = new tabIntMatcher();
            unknownsIn.fromString(cmd.word());
            return false;
        }
        if (s.equals("ipinfo")) {
            ipInfoCfg = secInfoUtl.doCfgStr(ipInfoCfg, cmd, negated);
            return false;
        }
        if (s.equals("segrout")) {
            segRout = !negated;
            return false;
        }
        if (s.equals("bier")) {
            bier = !negated;
            return false;
        }
        if (s.equals("wide-aspath")) {
            wideAsPath = !negated;
            return false;
        }
        if (s.equals("route-refresh-original")) {
            routeRefreshOld = !negated;
            return false;
        }
        if (s.equals("route-refresh-enhanced")) {
            routeRefreshNew = !negated;
            return false;
        }
        if (s.equals("rpki-in")) {
            if (negated) {
                rpkiIn = 0;
                return false;
            }
            rpkiIn = rtrBgpUtil.string2rpkiMode(cmd.word());
            return false;
        }
        if (s.equals("rpki-out")) {
            if (negated) {
                rpkiOut = 0;
                return false;
            }
            rpkiOut = rtrBgpUtil.string2rpkiMode(cmd.word());
            return false;
        }
        if (s.equals("rpki-vpn-in")) {
            if (negated) {
                vpkiIn = 0;
                return false;
            }
            vpkiIn = rtrBgpUtil.string2rpkiMode(cmd.word());
            return false;
        }
        if (s.equals("rpki-vpn-out")) {
            if (negated) {
                vpkiOut = 0;
                return false;
            }
            vpkiOut = rtrBgpUtil.string2rpkiMode(cmd.word());
            return false;
        }
        if (s.equals("leak-role")) {
            s = cmd.word();
            leakRole = -1;
            leakAttr = false;
            if (s.equals("disabled")) {
                leakRole = -1;
            }
            if (s.equals("provider")) {
                leakRole = rtrBgpUtil.roleProv;
            }
            if (s.equals("ix-server")) {
                leakRole = rtrBgpUtil.roleRs;
            }
            if (s.equals("ix-client")) {
                leakRole = rtrBgpUtil.roleRsc;
            }
            if (s.equals("customer")) {
                leakRole = rtrBgpUtil.roleCust;
            }
            if (s.equals("peer")) {
                leakRole = rtrBgpUtil.rolePeer;
            }
            if (s.equals("attrib")) {
                leakAttr = true;
            }
            leakRole = rtrBgpUtil.leakInverter(leakRole);
            leakAttr |= leakRole >= 0;
            s = cmd.word();
            leakForce = s.equals("enforce");
            if (!negated) {
                return false;
            }
            leakAttr = false;
            leakForce = false;
            leakRole = -1;
            return false;
        }
        if (s.equals("egress-engineering")) {
            egressEng = bits.str2num(cmd.word());
            if (!negated) {
                return false;
            }
            egressEng = 0;
            return false;
        }
        if (s.equals("label-pop")) {
            labelPop = !negated;
            return false;
        }
        if (s.equals("capability-negotiation")) {
            capaNego = !negated;
            return false;
        }
        if (s.equals("track-next-hop")) {
            trackNxthop = !negated;
            return false;
        }
        if (s.equals("ungroup-remoteas")) {
            ungrpRemAs = !negated;
            return false;
        }
        if (s.equals("remove-private-as-out")) {
            removePrivAsOut = !negated;
            return false;
        }
        if (s.equals("remove-private-as-in")) {
            removePrivAsIn = !negated;
            return false;
        }
        if (s.equals("override-peer-as-out")) {
            overridePeerOut = !negated;
            return false;
        }
        if (s.equals("override-peer-as-in")) {
            overridePeerIn = !negated;
            return false;
        }
        if (s.equals("fall-over")) {
            fallOver = !negated;
            return false;
        }
        if (s.equals("ha-mode")) {
            haMode = !negated;
            return false;
        }
        if (s.equals("attribute-filter")) {
            if (negated) {
                attribFilter = null;
                return false;
            }
            attribFilter = new tabIntMatcher();
            attribFilter.fromString(cmd.word());
            return false;
        }
        if (s.equals("confederation-peer")) {
            remoteConfed = !negated;
            return false;
        }
        if (s.equals("next-hop-unchanged")) {
            nxtHopUnchgd = !negated;
            return false;
        }
        if (s.equals("next-hop-multilabel")) {
            nxtHopMltlb = !negated;
            return false;
        }
        if (s.equals("next-hop-capability")) {
            nxtHopCapa = !negated;
            return false;
        }
        if (s.equals("next-hop-self")) {
            nxtHopSelf = !negated;
            return false;
        }
        if (s.equals("next-hop-peer")) {
            nxtHopPeer = !negated;
            return false;
        }
        if (s.equals("send-community")) {
            sendCommunity = 0;
            if (negated) {
                return false;
            }
            for (;;) {
                s = cmd.word();
                if (s.length() < 1) {
                    break;
                }
                if (s.equals("standard")) {
                    sendCommunity |= 1;
                    continue;
                }
                if (s.equals("extended")) {
                    sendCommunity |= 2;
                    continue;
                }
                if (s.equals("large")) {
                    sendCommunity |= 4;
                    continue;
                }
                if (s.equals("none")) {
                    sendCommunity = 0;
                    continue;
                }
                if (s.equals("both")) {
                    sendCommunity |= 3;
                    continue;
                }
                if (s.equals("all")) {
                    sendCommunity |= 7;
                    continue;
                }
            }
            return false;
        }
        if (s.equals("other-default-originate")) {
            sendOtrDefRou = !negated;
            return false;
        }
        if (s.equals("default-originate")) {
            sendDefRou = !negated;
            return false;
        }
        if (s.equals("route-reflector-client")) {
            reflectClnt = !negated;
            return false;
        }
        if (s.equals("prefix-list-in")) {
            if (negated) {
                prflstIn = null;
                return false;
            }
            cfgPrfxlst ntry = cfgAll.prfxFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such prefix list");
                return false;
            }
            prflstIn = ntry.prflst;
            return false;
        }
        if (s.equals("prefix-list-out")) {
            if (negated) {
                prflstOut = null;
                return false;
            }
            cfgPrfxlst ntry = cfgAll.prfxFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such prefix list");
                return false;
            }
            prflstOut = ntry.prflst;
            return false;
        }
        if (s.equals("route-map-in")) {
            if (negated) {
                roumapIn = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            roumapIn = ntry.roumap;
            return false;
        }
        if (s.equals("route-map-out")) {
            if (negated) {
                roumapOut = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            roumapOut = ntry.roumap;
            return false;
        }
        if (s.equals("route-policy-in")) {
            if (negated) {
                roupolIn = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            roupolIn = ntry.rouplc;
            return false;
        }
        if (s.equals("route-policy-out")) {
            if (negated) {
                roupolOut = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            roupolOut = ntry.rouplc;
            return false;
        }
        if (s.equals("other-prefix-list-in")) {
            if (negated) {
                oprflstIn = null;
                return false;
            }
            cfgPrfxlst ntry = cfgAll.prfxFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such prefix list");
                return false;
            }
            oprflstIn = ntry.prflst;
            return false;
        }
        if (s.equals("other-prefix-list-out")) {
            if (negated) {
                oprflstOut = null;
                return false;
            }
            cfgPrfxlst ntry = cfgAll.prfxFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such prefix list");
                return false;
            }
            oprflstOut = ntry.prflst;
            return false;
        }
        if (s.equals("other-route-map-in")) {
            if (negated) {
                oroumapIn = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            oroumapIn = ntry.roumap;
            return false;
        }
        if (s.equals("other-route-map-out")) {
            if (negated) {
                oroumapOut = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            oroumapOut = ntry.roumap;
            return false;
        }
        if (s.equals("other-route-policy-in")) {
            if (negated) {
                oroupolIn = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            oroupolIn = ntry.rouplc;
            return false;
        }
        if (s.equals("other-route-policy-out")) {
            if (negated) {
                oroupolOut = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            oroupolOut = ntry.rouplc;
            return false;
        }
        if (s.equals("vpn-route-map-in")) {
            if (negated) {
                vroumapIn = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            vroumapIn = ntry.roumap;
            return false;
        }
        if (s.equals("vpn-route-map-out")) {
            if (negated) {
                vroumapOut = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            vroumapOut = ntry.roumap;
            return false;
        }
        if (s.equals("vpn-route-policy-in")) {
            if (negated) {
                vroupolIn = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            vroupolIn = ntry.rouplc;
            return false;
        }
        if (s.equals("vpn-route-policy-out")) {
            if (negated) {
                vroupolOut = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            vroupolOut = ntry.rouplc;
            return false;
        }
        if (s.equals("ovpn-route-map-in")) {
            if (negated) {
                wroumapIn = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            wroumapIn = ntry.roumap;
            return false;
        }
        if (s.equals("ovpn-route-map-out")) {
            if (negated) {
                wroumapOut = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            wroumapOut = ntry.roumap;
            return false;
        }
        if (s.equals("ovpn-route-policy-in")) {
            if (negated) {
                wroupolIn = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            wroupolIn = ntry.rouplc;
            return false;
        }
        if (s.equals("ovpn-route-policy-out")) {
            if (negated) {
                wroupolOut = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            wroupolOut = ntry.rouplc;
            return false;
        }
        if (s.equals("evpn-route-map-in")) {
            if (negated) {
                eroumapIn = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            eroumapIn = ntry.roumap;
            return false;
        }
        if (s.equals("evpn-route-map-out")) {
            if (negated) {
                eroumapOut = null;
                return false;
            }
            cfgRoump ntry = cfgAll.rtmpFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route map");
                return false;
            }
            eroumapOut = ntry.roumap;
            return false;
        }
        if (s.equals("evpn-route-policy-in")) {
            if (negated) {
                eroupolIn = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            eroupolIn = ntry.rouplc;
            return false;
        }
        if (s.equals("evpn-route-policy-out")) {
            if (negated) {
                eroupolOut = null;
                return false;
            }
            cfgRouplc ntry = cfgAll.rtplFind(cmd.word(), false);
            if (ntry == null) {
                cmd.error("no such route policy");
                return false;
            }
            eroupolOut = ntry.rouplc;
            return false;
        }
        return true;
    }

}
