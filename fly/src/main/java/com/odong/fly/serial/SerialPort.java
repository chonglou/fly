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

    byte[] send(byte[] request) throws MyException;


    void open(String portName, int dataBand, boolean feedback) throws MyException;

    List<String> listPortNames();

    /**
     * 串口类型
     */
    enum Type {
        ON_OFF
    }

    public interface Callback {
        void call(byte[] buf);
    }
}
