package com.odong.relay.serial;

import com.odong.relay.MyException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午9:38
 */

public abstract class SerialUtil {
    public abstract void send(String portName, Command command) throws MyException;

    public abstract void open(String portName, int dataBand, SerialPort.Callback callback) throws MyException;

    public abstract void close(String portName);

    public abstract Set<String> getPortNameList();

    public boolean isOpen(String portName) {
        return map.containsKey(portName);
    }

    public void init() {
        map = new LinkedHashMap<>();
    }

    public void destroy() {
        for (SerialPort sp : map.values()) {
            if (sp != null && sp.isOpen()) {
                sp.close();
            }
        }
        map.clear();
    }

    public boolean hasOpen() {
        return !map.isEmpty();
    }

    protected void put(String portName, SerialPort port) {
        map.put(portName, port);
    }

    protected void pop(String portName) {
        map.remove(portName);
    }

    protected SerialPort get(String portName) {
        return map.get(portName);
    }

    private Map<String, SerialPort> map;
}
