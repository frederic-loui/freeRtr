package org.freertr.util;

import java.util.ArrayList;
import java.util.List;

/**
 * interface history
 *
 * @author matecsaba
 */
public class history {

    private counter ocnt; // last value

    private long otim; // last updated

    private int secc = 0; // current seconds

    private int minc = 0; // current seconds

    private final static int limit = 60;

    private List<counter> secs = new ArrayList<counter>(); // last seconds

    private List<counter> mina = new ArrayList<counter>(); // avg of minutes

    private List<counter> minm = new ArrayList<counter>(); // max of minutes

    private List<counter> hora = new ArrayList<counter>(); // avg of hours

    private List<counter> horm = new ArrayList<counter>(); // max of hours

    /**
     * create new history
     */
    public history() {
        ocnt = new counter();
        otim = bits.getTime();
    }

    /**
     * create new history
     *
     * @param cur current counter
     */
    public history(counter cur) {
        ocnt = cur.copyBytes();
        otim = bits.getTime();
    }

    /**
     * threshold message
     *
     * @param prc percent
     * @return text, null if below
     */
    public String threshold(int prc) {
        if (prc < 1) {
            return null;
        }
        int siz = mina.size();
        if (siz < 2) {
            return null;
        }
        int r = mina.get(siz - 1).percent(mina.get(siz - 2), false, false) - 100;
        if (r < prc) {
            return null;
        }
        return "changed by " + r + " percent";
    }

    /**
     * update counter
     *
     * @param cur current counter
     * @param inc value increasing
     */
    public void update(counter cur, boolean inc) {
        long tim = bits.getTime();
        int pst = (int) ((tim - otim) / 1000);
        if (pst < 1) {
            pst = 1;
        }
        if (pst > limit) {
            pst = limit;
        }
        if (inc) {
            counter res = cur.minus(ocnt);
            if (res.compareTo(new counter()) < 0) {
                res = new counter();
            }
            res = res.div(pst);
            update(secs, res, pst);
        } else {
            update(secs, cur.copyBytes(), pst);
        }
        ocnt = cur.copyBytes();
        otim = tim;
        secc += pst;
        if (secc <= limit) {
            return;
        }
        secc = 0;
        minc++;
        update(mina, counter.average(secs), 1);
        update(minm, counter.maximum(secs), 1);
        if (minc <= limit) {
            return;
        }
        minc = 0;
        update(hora, counter.average(mina), 1);
        update(horm, counter.maximum(minm), 1);
    }

    private static void update(List<counter> lst, counter cur, int cnt) {
        if ((lst.size() + cnt) <= limit) {
            for (int i = 0; i < cnt; i++) {
                lst.add(cur);
            }
            return;
        }
        if (lst.size() < limit) {
            for (int i = lst.size(); i < limit; i++) {
                lst.add(cur);
                cnt--;
            }
        }
        for (int i = cnt; i < limit; i++) {
            lst.set(i - cnt, lst.get(i));
        }
        for (int i = limit - cnt; i < limit; i++) {
            lst.set(i, cur);
        }
    }

    /**
     * generate show output
     *
     * @param mod mode, 1=bothbyte, 2=rxbyte, 3=txbyte, 4=drbyte 5=bothpack,
     * 6=rxpack, 7=txpack 8=drpack, 9=realtime, 10=bothbyte-sec, 11=bothbyte-min
     * 12=bothbyte-hour, 13=bothpack-sec, 14=bothpack-min, 15=bothpack-hour
     * @return show output
     */
    public List<String> show(int mod) {
        List<String> res = new ArrayList<String>();
        switch (mod) {
            case 9:
                show(res, secs, secs, 0x10001);
                show(res, secs, secs, 0x10005);
                return res;
            case 10:
                show(res, secs, secs, 0x10001);
                return res;
            case 11:
                show(res, mina, minm, 0x20001);
                return res;
            case 12:
                show(res, hora, horm, 0x30001);
                return res;
            case 13:
                show(res, secs, secs, 0x10005);
                return res;
            case 14:
                show(res, mina, minm, 0x20005);
                return res;
            case 15:
                show(res, hora, horm, 0x30005);
                return res;
        }
        show(res, secs, secs, mod | 0x10000);
        show(res, mina, minm, mod | 0x20000);
        show(res, hora, horm, mod | 0x30000);
        return res;
    }

    private static void show(List<String> res, List<counter> la, List<counter> lm, int mod) {
        long[] aa = new long[limit];
        long[] am = new long[limit];
        long[] lima = show(aa, la, mod);
        long[] limm = show(am, lm, mod);
        if (lima[1] > limm[1]) {
            limm[1] = lima[1];
        }
        if (lima[0] < limm[0]) {
            limm[0] = lima[0];
        }
        long step = (limm[1] - limm[0]) / 10;
        for (int o = 10; o >= 0; o--) {
            long p = (o * step) + limm[0];
            String s = bits.padBeg("" + bits.toUser(p), 12, " ") + "|";
            for (int i = 0; i < limit; i++) {
                String a = " ";
                if (am[i] >= p) {
                    a = "*";
                }
                if (aa[i] >= p) {
                    a = "#";
                }
                s += a;
            }
            res.add(s);
        }
        String s = "        ";
        switch (mod & 0xfff) {
            case 1:
            case 2:
            case 3:
            case 4:
                s += "bps";
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                s += "pps";
                break;
            default:
                s += "???";
                break;
        }
        s += "|";
        for (int i = 0; i < 6; i++) {
            s += bits.padEnd("" + (i * 10), 10, "-");
        }
        s += " ";
        switch (mod >>> 16) {
            case 1:
                s += "seconds";
                break;
            case 2:
                s += "minutes";
                break;
            case 3:
                s += "hours";
                break;
            default:
                s += "?";
                break;
        }
        res.add(" " + s);
        res.add("");
    }

    private static long[] show(long[] trg, List<counter> src, int mod) {
        int siz = src.size();
        if (siz < 1) {
            long[] res = new long[2];
            res[0] = 0;
            res[1] = 10;
            return res;
        }
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < limit; i++) {
            long o = siz - i - 1;
            if (o < 0) {
                trg[i] = 0;
                continue;
            }
            counter g = src.get((int) o);
            switch (mod & 0xfff) {
                case 1:
                    o = 8 * (g.byteRx + g.byteTx + g.byteDr);
                    break;
                case 2:
                    o = 8 * g.byteRx;
                    break;
                case 3:
                    o = 8 * g.byteTx;
                    break;
                case 4:
                    o = 8 * g.byteDr;
                    break;
                case 5:
                    o = g.packRx + g.packTx + g.packDr;
                    break;
                case 6:
                    o = g.packRx;
                    break;
                case 7:
                    o = g.packTx;
                    break;
                case 8:
                    o = g.packDr;
                    break;
                default:
                    o = 0;
                    break;
            }
            trg[i] = o;
            if (o > max) {
                max = o;
            }
            if (o < min) {
                min = o;
            }
        }
        if (max <= min) {
            max = min + 10;
        }
        long[] res = new long[2];
        res[0] = min;
        res[1] = max;
        return res;
    }

    /**
     * generate dump output
     *
     * @param mod mode 1=byte, 4=pack
     * @param itv interval 1=sec, 2=min, 3=hour, 4=all
     * @return dump output
     */
    public List<String> dump(int mod, int itv) {
        List<String> res = new ArrayList<String>();
        switch (itv) {
            case 1:
                dump(res, secs, secs, mod, "s");
                break;
            case 2:
                dump(res, mina, minm, mod, "m");
                break;
            case 3:
                dump(res, hora, horm, mod, "h");
                break;
            case 4:
                res.addAll(dump(mod, 1));
                res.addAll(dump(mod, 2));
                res.addAll(dump(mod, 3));
                break;
        }
        return res;
    }

    private void dump(List<String> res, List<counter> la, List<counter> lm, int mod, String quant) {
        long[] rxa = new long[limit];
        long[] rxm = new long[limit];
        long[] txa = new long[limit];
        long[] txm = new long[limit];
        long[] dra = new long[limit];
        long[] drm = new long[limit];
        show(rxa, la, mod + 1);
        show(rxm, lm, mod + 1);
        show(txa, la, mod + 2);
        show(txm, lm, mod + 2);
        show(dra, la, mod + 3);
        show(drm, lm, mod + 3);
        for (int i = 0; i < limit; i++) {
            res.add(i + quant + "|" + rxa[i] + "|" + rxm[i] + "|" + txa[i] + "|" + txm[i] + "|" + dra[i] + "|" + drm[i]);
        }
    }

    /**
     * get summary
     *
     * @return summary for table
     */
    public String getShSum() {
        return getShSum(secs);
    }

    /**
     * get packet summary
     *
     * @return summary for table
     */
    public String getShPSum() {
        return getShPSum(secs);
    }

    /**
     * get summary
     *
     * @param hw hw history
     * @return summary for table
     */
    public String getShHwSum(history hw) {
        if (hw == null) {
            return getShSum(secs);
        }
        return getLast(secs).getShHwBsum(getLast(hw.secs));
    }

    /**
     * get packet summary
     *
     * @param hw hw counter
     * @return summary for table
     */
    public String getShHwPSum(history hw) {
        if (hw == null) {
            return getShPSum(secs);
        }
        return getLast(secs).getShHwPsum(getLast(hw.secs));
    }

    private static counter getLast(List<counter> lst) {
        int i = lst.size() - 1;
        if (i < 0) {
            return new counter();
        } else {
            return lst.get(i);
        }
    }

    private static String getShSum(List<counter> lst) {
        return getLast(lst).getShBsum();
    }

    private static String getShPSum(List<counter> lst) {
        return getLast(lst).getShPsum();
    }

    /**
     * get rate
     *
     * @return rate for table
     */
    public List<String> getShRate() {
        List<String> lst = new ArrayList<String>();
        lst.add("1sec|" + getShPSum(secs) + "|" + getShSum(secs));
        lst.add("1min|" + getShPSum(mina) + "|" + getShSum(mina));
        lst.add("1hour|" + getShPSum(hora) + "|" + getShSum(hora));
        return lst;
    }

    /**
     * get autobandwidth value
     *
     * @param mod mode: 0x01=rx, 0x02=tx, 0x03=both, 0x10=sec, 0x20=mina,
     * 0x30=minm, 0x40=hora, 0x50=horm
     * @return calculated bandwidth
     */
    public long getAutoBw(int mod) {
        counter cnt;
        switch (mod & 0xf0) {
            case 0x10:
                cnt = getLast(secs);
                break;
            case 0x20:
                cnt = getLast(mina);
                break;
            case 0x30:
                cnt = getLast(minm);
                break;
            case 0x40:
                cnt = getLast(hora);
                break;
            case 0x50:
                cnt = getLast(horm);
                break;
            default:
                return 0;
        }
        switch (mod & 0xf) {
            case 1:
                return cnt.byteRx;
            case 2:
                return cnt.byteTx;
            case 3:
                return cnt.byteRx + cnt.byteTx;
            default:
                return 0;
        }
    }

    /**
     * add values
     *
     * @param old add this
     * @return resulting values
     */
    public history plus(history old) {
        history res = new history();
        res.secc = secc;
        res.ocnt = old.ocnt.plus(ocnt);
        res.otim = otim;
        res.secc = secc;
        res.minc = minc;
        for (int i = 0; i < secs.size(); i++) {
            res.secs.add(old.secs.get(i).plus(secs.get(i)));
        }
        for (int i = 0; i < mina.size(); i++) {
            res.mina.add(old.mina.get(i).plus(mina.get(i)));
        }
        for (int i = 0; i < minm.size(); i++) {
            res.minm.add(old.minm.get(i).plus(minm.get(i)));
        }
        for (int i = 0; i < hora.size(); i++) {
            res.hora.add(old.hora.get(i).plus(hora.get(i)));
        }
        for (int i = 0; i < horm.size(); i++) {
            res.horm.add(old.horm.get(i).plus(horm.get(i)));
        }
        return res;
    }

}
