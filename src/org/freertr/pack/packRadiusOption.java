package org.freertr.pack;

import java.util.ArrayList;
import java.util.List;
import org.freertr.util.bits;
import org.freertr.util.cmds;

/**
 * radius option
 *
 * @author matecsaba
 */
public class packRadiusOption implements Comparable<packRadiusOption> {

    /**
     * create instance
     */
    public packRadiusOption() {
    }

    /**
     * vendor id
     */
    public int vendId;

    /**
     * vendor type
     */
    public int vendTyp;

    /**
     * data
     */
    public byte[] buffer;

    public int compareTo(packRadiusOption o) {
        if (vendId < o.vendId) {
            return -1;
        }
        if (vendId > o.vendId) {
            return +1;
        }
        if (vendTyp < o.vendTyp) {
            return -1;
        }
        if (vendTyp > o.vendTyp) {
            return +1;
        }
        return 0;
    }

    /**
     * parse from string
     *
     * @param cmd string to read
     */
    public void fromString(cmds cmd) {
        vendId = bits.str2num(cmd.word());
        vendTyp = bits.str2num(cmd.word());
        List<Integer> lst = new ArrayList<Integer>();
        for (;;) {
            String a = cmd.word();
            if (a.length() < 1) {
                break;
            }
            lst.add(bits.fromHex(a));
        }
        buffer = new byte[lst.size()];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (lst.get(i) & 0xff);
        }
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < buffer.length; i++) {
            s += " " + bits.toHexB(buffer[i]);
        }
        return vendId + " " + vendTyp + s;
    }

    /**
     * encode to bytes
     *
     * @return encoded
     */
    public byte[] doEncode() {
        byte[] res = new byte[buffer.length + 6];
        bits.msbPutD(res, 0, vendId);
        res[4] = (byte) vendTyp;
        res[5] = (byte) (buffer.length + 2);
        bits.byteCopy(buffer, 0, res, 6, buffer.length);
        return res;
    }

}
