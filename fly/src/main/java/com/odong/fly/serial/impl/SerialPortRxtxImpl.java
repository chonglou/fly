package com.odong.fly.serial.impl;

import com.odong.fly.MyException;
import com.odong.fly.serial.SerialPort;
import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TooManyListenersException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午8:59
 */
public class SerialPortRxtxImpl implements SerialPort {
    @Override
    public boolean isOpen() {
        return connected;  //
    }

    @Override
    public void close() {
        try {
            serialPort.removeEventListener();
            serialPort.close();
        } finally {
            connected = false;
        }


    }

    @Override
    public synchronized byte[] send(byte[] bytes) throws MyException {
        try {
            return reader.getResponse(bytes);
        } catch (IOException e) {
            logger.error("串口操作出错", e);
            throw new MyException(MyException.Type.SERIAL_PORT_IO_ERROR);
        }
    }

    @Override
    public void open(String portName, int dataBand, boolean feedback) throws MyException {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                throw new MyException(MyException.Type.SERIAL_PORT_IN_USE);
            }
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (!(commPort instanceof gnu.io.SerialPort)) {
                throw new MyException(MyException.Type.SERIAL_PORT_NOT_VALID);
            }
            serialPort = (gnu.io.SerialPort) commPort;
            serialPort.setSerialPortParams(dataBand, gnu.io.SerialPort.DATABITS_8, gnu.io.SerialPort.STOPBITS_1, gnu.io.SerialPort.PARITY_NONE);


            reader = new SerialReader(serialPort.getInputStream(), serialPort.getOutputStream(), feedback);

            serialPort.addEventListener(reader);

            serialPort.notifyOnDataAvailable(true);
            connected = true;

        } catch (NoSuchPortException e) {
            logger.debug("端口{}不存在", portName, e);
            throw new MyException(MyException.Type.SERIAL_PORT_NOT_EXIST);
        } catch (PortInUseException e) {
            logger.error("端口被占用", e);
            throw new MyException(MyException.Type.SERIAL_PORT_IN_USE);
        } catch (UnsupportedCommOperationException e) {
            logger.error("不支持的操作", e);
            throw new MyException(MyException.Type.SERIAL_PORT_UNSUPPORTED);
        } catch (IOException e) {
            logger.error("串口操作出错", e);
            throw new MyException(MyException.Type.SERIAL_PORT_IO_ERROR);
        } catch (TooManyListenersException e) {
            logger.error("事件监听出错", e);
            throw new MyException(MyException.Type.SERIAL_PORT_IO_ERROR);
        } catch (UnsatisfiedLinkError e) {
            logger.error("RXTX未配置 lib路径[{}]", System.getProperty("java.library.path"), e);
            throw new MyException(MyException.Type.SERIAL_PORT_IO_ERROR);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public Set<String> listPortName() throws MyException {
        Set<String> list = new HashSet<>();
        try {
            Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
            while (portEnum.hasMoreElements()) {
                CommPortIdentifier pi = portEnum.nextElement();
                if (pi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    list.add(pi.getName());
                }
            }
        } catch (UnsatisfiedLinkError e) {
            logger.error("RXTX未配置 lib路径[{}]", System.getProperty("java.library.path"), e);
            throw new MyException(MyException.Type.SERIAL_PORT_IO_ERROR);
        }
        return list;
    }

    private gnu.io.SerialPort serialPort;
    private SerialReader reader;
    private boolean connected;
    private final static Logger logger = LoggerFactory.getLogger(SerialPortRxtxImpl.class);

}
