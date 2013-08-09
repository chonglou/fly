package com.odong.relay.serial.impl;

import com.odong.relay.serial.Command;
import com.odong.relay.serial.SerialPort;
import com.odong.relay.serial.SerialUtil;

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
    public void send(String portName, Command command) {
        //
    }

    @Override
    public void open(String portName, int dataBand, SerialPort.Callback callback) {
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
