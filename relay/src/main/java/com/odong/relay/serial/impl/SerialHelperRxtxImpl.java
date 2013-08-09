package com.odong.relay.serial.impl;

import com.odong.relay.MyException;
import com.odong.relay.serial.SerialHelper;
import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午9:28
 */
@Component
public class SerialHelperRxtxImpl implements SerialHelper {
    @Override
    public boolean isOpen() {
        return serialPort != null;  //
    }

    @Override
    public void close() {
        serialPort.close();
        serialPort = null;
    }

    @Override
    public void send(byte... request) throws MyException {
        try (OutputStream out = serialPort.getOutputStream()) {
            //new Thread(new SerialWriter(out, request)).start();
            taskExecutor.execute(new SerialWriter(out, request));
        } catch (IOException e) {
            logger.error("串口操作出错", e);
            throw new MyException(MyException.Type.SERIAL_PORT_IO_ERROR);
        }
    }


    @Override
    public void open(String portName, int dataBand, Callback callback) throws MyException {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                throw new MyException(MyException.Type.SERIAL_PORT_IN_USE);
            }
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (!(commPort instanceof SerialPort)) {
                throw new MyException(MyException.Type.SERIAL_PORT_NOT_VALID);
            }
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(dataBand, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            InputStream in = serialPort.getInputStream();
            serialPort.addEventListener(new SerialReader(in, callback));
            serialPort.notifyOnDataAvailable(true);

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
    public List<String> listPortNames() {
        List<String> list = new ArrayList<>();
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier pi = portEnum.nextElement();
            list.add(pi.getName());
        }
        if (list.isEmpty()) {
            for (int i = 1; i <= 5; i++) {
                list.add("COM" + i);
            }
        }
        return list;
    }

    @Override
    public String getPortTypeName(int portType) {

        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }

    private SerialPort serialPort;
    @Resource
    private TaskExecutor taskExecutor;
    private final static Logger logger = LoggerFactory.getLogger(SerialHelperRxtxImpl.class);

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
