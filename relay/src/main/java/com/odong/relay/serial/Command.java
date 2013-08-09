package com.odong.relay.serial;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午8:46
 */
public interface Command extends Serializable {
    byte[] toBytes();

    Type getType();

    public enum Type {
        ON_OFF
    }
}
