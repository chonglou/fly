package com.odong.relay.serial.impl;

import com.odong.relay.serial.SerialHelper;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午10:00
 */
public class SerialReader implements SerialPortEventListener {
    public SerialReader(InputStream in, SerialHelper.Callback callback) {
        this.in = in;
        this.callback = callback;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        try {
            int data;
            int len = 0;
            while ((data = in.read()) > -1) {
                if (data == '\n') {
                    break;
                }
                buffer[len++] = (byte) data;
            }
            byte[] buf = new byte[len];
            System.arraycopy(buffer, 0, buf, 0, len);
            callback.process(buf);
        } catch (IOException e) {
            logger.error("读串口出错", e);
        }

    }

    private InputStream in;
    private SerialHelper.Callback callback;
    private byte[] buffer = new byte[1024];
    private final static Logger logger = LoggerFactory.getLogger(SerialReader.class);

}
