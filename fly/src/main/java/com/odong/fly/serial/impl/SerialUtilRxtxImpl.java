package com.odong.fly.serial.impl;

import com.odong.fly.MyException;
import com.odong.fly.serial.Command;
import com.odong.fly.serial.SerialPort;
import com.odong.fly.serial.SerialUtil;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午9:39
 */
public class SerialUtilRxtxImpl extends SerialUtil {

    @Override
    public String send(String portName, Command command) throws MyException {
        return get(portName).send(command);
    }

    @Override
    public void open(String portName, int dataBand) throws MyException {
        SerialPort sp = new SerialPortRxtxImpl();
        sp.open(portName, dataBand);
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
