package com.odong.relay.serial;

import com.odong.relay.MyException;

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

    void send(Command command) throws MyException;


    void open(String portName, int dataBand, Callback callback) throws MyException;

    List<String> listPortNames();

    String getPortTypeName(int portType);


    public interface Callback {
        void process(byte[] buffer);
    }

    /**
     * Created with IntelliJ IDEA.
     * User: flamen
     * Date: 13-8-9
     * Time: 下午10:43
     */
    enum Type {
        ON_OFF
    }
}
