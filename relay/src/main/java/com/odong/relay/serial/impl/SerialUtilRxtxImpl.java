package com.odong.relay.serial.impl;

import com.odong.relay.MyException;
import com.odong.relay.serial.Command;
import com.odong.relay.serial.SerialPort;
import com.odong.relay.serial.SerialUtil;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午9:39
 */
public class SerialUtilRxtxImpl extends SerialUtil {

    @Override
    public void send(String portName, Command command) throws MyException {
        get(portName).send(command);
    }

    @Override
    public void open(String portName, int dataBand, SerialPort.Callback callback) throws MyException {
        SerialPort sp = new SerialPortRxtxImpl();
        sp.open(portName, dataBand, callback);
        if (sp.isOpen()) {
            put(portName, sp);
        }
    }

    @Override
    public void close(String portName) {
        SerialPort sp = get(portName);
        sp.close();
        pop(portName);
    }

    @Override
    public Set<String> getPortNameList() {
        return new SerialUtilRxtxImpl().getPortNameList();  //
    }
}
