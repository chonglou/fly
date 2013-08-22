package com.odong.fly.serial.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午10:08
 */
public class SerialWriter implements Runnable {
    public SerialWriter(OutputStream out, byte[] buf) {
        this.out = out;
        this.buf = buf;
    }

    @Override
    public void run() {
        try {

            out.write(buf);
        } catch (IOException e) {
            logger.error("写串口出错", e);

        }
    }

    private OutputStream out;
    private byte[] buf;
    private final static Logger logger = LoggerFactory.getLogger(SerialWriter.class);
}
