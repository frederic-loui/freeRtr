package rtr;

import addr.addrIP;
import pack.packHolder;
import tab.tabLabelBier;
import tab.tabRouteEntry;
import util.bits;
import util.typLenVal;

/**
 * isis bier
 *
 * @author matecsaba
 */
public class rtrIsisBr {

    private rtrIsisBr() {
    }

    /**
     * generate br prefix
     *
     * @param lower lower layer to use
     * @param idx index
     * @return bytes generated
     */
    protected static byte[] putPref(rtrIsis lower, int idx) {
        if (lower.bierLab == null) {
            return new byte[0];
        }
        packHolder pck = new packHolder(true, true);
        typLenVal tlv = rtrIsis.getTlv();
        tlv.valDat[0] = 0; // algorithm
        tlv.valDat[1] = 0; // subdomain
        bits.msbPutW(tlv.valDat, 2, idx); // bfr id
        tlv.valDat[4] = 1; // type: mpls encap
        tlv.valDat[5] = 4; // length
        bits.msbPutD(tlv.valDat, 6, lower.bierLab[0].getValue() | (tabLabelBier.num2bsl(lower.bierLen) << 20)); // bsl, label
        tlv.valDat[6] = (byte) lower.bierLab.length; // label range
        tlv.valSiz = 10;
        tlv.valTyp = 32;
        tlv.putThis(pck);
        pck.merge2beg();
        return pck.getCopy();
    }

    /**
     * read br prefix
     *
     * @param tlv data
     * @param prf prefix
     */
    protected static void getPref(typLenVal tlv, tabRouteEntry<addrIP> prf) {
        if (tlv.valTyp != 32) {
            return;
        }
        prf.bierI = bits.msbGetW(tlv.valDat, 2); // bfr id
        if (tlv.valDat[4] != 1) { // type
            return;
        }
        if (tlv.valDat[5] != 4) { // length
            return;
        }
        int i = bits.msbGetD(tlv.valDat, 6);
        prf.bierB = i & 0xfffff; // label
        prf.bierS = (i >>> 20) & 0xf; // bsl
    }

}
