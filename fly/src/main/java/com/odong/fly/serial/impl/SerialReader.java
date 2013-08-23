package com.odong.fly.serial.impl;

import com.odong.fly.serial.SerialPort;
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
public final class SerialReader implements SerialPortEventListener {
    public SerialReader(InputStream in) {
        this.in = in;
    }

    @Override
    public  void serialEvent(SerialPortEvent serialPortEvent) {
        finish = false;
        try {
            int data;
            int len = 0;
            while ((data = in.read()) > -1) {
                if (data == '\n') {
                    break;
                }
                buffer[len++] = (byte) data;
            }
            /*
            byte[] buf = new byte[len];
            System.arraycopy(buffer, 0, buf, 0, len);
            */
            response = new String(buffer,0,len);

        } catch (IOException e) {
            logger.error("读串口出错", e);
        }
        finish = true;
    }

    private InputStream in;

    private byte[] buffer = new byte[1024];
    private String response;
    private boolean finish;
    private final static Logger logger = LoggerFactory.getLogger(SerialReader.class);

    public String getResponse() {
        String ret= response;
        if(finish){
            response = null;
        }
        return ret;
    }

    public boolean isFinish() {
        return finish;
    }
}
