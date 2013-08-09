package com.odong.relay.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午10:39
 */
public class OnOffCommand implements Serializable {
    private static final long serialVersionUID = -4430458793102362593L;

    public OnOffCommand(int port, boolean on) {
        this.port = port;
        this.flag = on;
    }

    public final int port;
    public final boolean flag;

    @Override
    public String toString() {
        return String.format("FF %02x %s", port, flag ? "01" : "00");
    }
}
