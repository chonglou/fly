package com.odong.fly.serial.impl;

import com.odong.fly.MyException;
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
    public String send(String portName, String request) throws MyException {
        //FIXME 可能有转换错误
        byte[] buf = get(portName).send(request.getBytes());
        return new String(buf);
    }

    @Override
    public void open(String portName, int dataBand, boolean feedback) throws MyException {
        SerialPort sp = new SerialPortRxtxImpl();
        sp.open(portName, dataBand, feedback);
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
    public Set<String> listPortName() {
        return new SerialUtilRxtxImpl().listPortName();  //
    }
}
