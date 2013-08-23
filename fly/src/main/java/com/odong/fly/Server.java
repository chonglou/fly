package com.odong.fly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 下午4:00
 */
public class Server {
    public <T> T bean(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    public void init() {
        logger.info("正在设置运行环境");
        System.setProperty("derby.stream.error.file", "var/derby.log");
        try {
            FileLock lock = new RandomAccessFile(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "relay.lock", "rw").getChannel().tryLock();
            if (lock == null) {
                logger.error("不能重复启动");
                System.exit(-1);
            }

        } catch (IOException e) {
            logger.error("锁文件出错", e);
        }

        for (String path : new String[]{"var/camera"}) {
            File file = new File(path);
            if (!file.exists()) {
                logger.info("创建数据目录[{}]", path);
                if (!file.mkdirs()) {
                    logger.error("创建数据目录[{}]失败", path);
                }
            }
        }
    }

    public void destroy() {
        ((ClassPathXmlApplicationContext) ctx).stop();

    }

    public void start() {
        logger.info("正在启动...");
        ctx = new ClassPathXmlApplicationContext("spring/*.xml");
    }

    public void stop() {
        logger.info("正在停止...");
        System.exit(0);
    }

    private Server() {
    }

    private ApplicationContext ctx;


    public static synchronized Server get() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private static Server instance;
    private final static Logger logger = LoggerFactory.getLogger(Server.class);
}
