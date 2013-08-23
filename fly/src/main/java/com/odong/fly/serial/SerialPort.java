package com.odong.fly.serial;

import com.odong.fly.MyException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午8:51
 */
public interface SerialPort {
    boolean isOpen();

    void close();

    String send(Command command) throws MyException;


    void open(String portName, int dataBand) throws MyException;

    List<String> listPortNames();

    String getPortTypeName(int portType);

    /**
     * 串口类型
     */
    enum Type {
        ON_OFF
    }
}
