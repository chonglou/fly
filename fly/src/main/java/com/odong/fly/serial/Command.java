package com.odong.fly.serial;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午8:46
 */
public final class Command implements Serializable {

    public static String onOff(int channel, boolean on) {
        return String.format("FF %02x %s", channel, on ? "01" : "00");
    }

    private static final long serialVersionUID = 6555345829872188205L;

    private Command() {
    }


}
