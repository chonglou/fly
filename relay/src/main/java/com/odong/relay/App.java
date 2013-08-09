package com.odong.relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class App {
    public static void main(String[] args) {
        logger.info("启动");
        System.setProperty("derby.stream.error.file", "var/derby.log");
        lock();
        new ClassPathXmlApplicationContext("spring/*.xml");
    }

    private static void lock() {
        try {
            FileLock lock = new RandomAccessFile(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "relay.lock", "rw").getChannel().tryLock();
            if (lock == null) {
                logger.error("不能重复启动");

                System.exit(-1);
            }

        } catch (IOException e) {
            logger.error("锁文件出错", e);
        }
    }


    private final static Logger logger = LoggerFactory.getLogger(App.class);
}
