package com.odong.relay.serial;

import com.odong.relay.MyException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午9:16
 */

public interface SerialHelper {
    boolean isOpen();

    void close();

    void send(byte... request) throws MyException;


    void open(String portName, int dataBand, Callback callback) throws MyException;

    List<String> listPortNames();

    String getPortTypeName(int portType);


    public interface Callback {
        void process(byte[] buffer);
    }
}
