package com.odong.fly.serial.impl;

import com.odong.fly.serial.SerialUtil;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午9:39
 */
public class SerialUtilDemoImpl extends SerialUtil {
    @Override
    public String send(String portName, String request) {
        return request;
    }

    @Override
    public void open(String portName, int dataBand, boolean feedback) {
        put(portName, null);
    }

    @Override
    public void close(String portName) {
        pop(portName);
    }

    @Override
    public Set<String> getPortNameList() {
        Set<String> set = new LinkedHashSet<>();
        for (int i = 1; i < 5; i++) {
            set.add("COM" + i);
        }
        return set;  //
    }
}
