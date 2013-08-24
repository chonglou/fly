package com.odong.fly.serial.impl;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午10:00
 */
public final class SerialReader implements SerialPortEventListener {
    public byte[] getResponse(byte[] request) throws IOException {
        out.write(request);
        if (feedback) {
            boolean interrupted = false;
            byte[] response;
            for (; ; ) {
                try {
                    response = answer.take();
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
            return response;
        }
        return null;
    }

    public SerialReader(InputStream in, OutputStream out, boolean feedback) {
        this.in = in;
        this.out = out;
        answer = new LinkedBlockingQueue<>();
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
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
                if (feedback) {
                    answer.add(buf);
                }

            } catch (IOException e) {
                logger.error("读串口出错", e);
            }
        }
    }

    private InputStream in;
    private OutputStream out;
    private boolean feedback;

    private byte[] buffer = new byte[1024];
    private final BlockingQueue<byte[]> answer;
    private final static Logger logger = LoggerFactory.getLogger(SerialReader.class);


}
