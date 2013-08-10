package com.odong.relay.serial;

import com.odong.relay.MyException;

import java.util.HashMap;
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
        return portMap.containsKey(portName);
    }

    public void setType(String portName, SerialPort.Type type) {
        typeMap.put(portName, type);
    }

    public SerialPort.Type getType(String portName) {
        return typeMap.get(portName);
    }

    public void init() {
        portMap = new LinkedHashMap<>();
        typeMap = new HashMap<>();
    }

    public void destroy() {
        for (SerialPort sp : portMap.values()) {
            if (sp != null && sp.isOpen()) {
                sp.close();
            }
        }
        portMap.clear();
        typeMap.clear();
    }

    public boolean hasOpen() {
        return !portMap.isEmpty();
    }

    public Set<String> getStatus() {
        return portMap.keySet();
    }

    protected void put(String portName, SerialPort port) {
        portMap.put(portName, port);
    }

    protected void pop(String portName) {
        portMap.remove(portName);
    }

    protected SerialPort get(String portName) {
        return portMap.get(portName);
    }

    private Map<String, SerialPort> portMap;
    private Map<String, SerialPort.Type> typeMap;
}
