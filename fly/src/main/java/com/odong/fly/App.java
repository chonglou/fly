package com.odong.fly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class App {
    public static void main(String[] args) {
        checkEnv();
        setStyle();
        init();
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("spring/*.xml");
        ctx.registerShutdownHook();
    }
    private static void init(){
        logger.info("正在检查参数设置");
        for (String path : new String[]{"var/camera"}) {
            File file = new File(path);
            if (!file.exists()) {
                logger.info("创建数据目录[{}]", path);
                if (!file.mkdirs()) {
                    logger.error("创建数据目录[{}]失败", path);
                }
            }
        }
        System.setProperty("derby.stream.error.file", "var/derby.log");
        System.setProperty("fly.store.dir", "var/");
    }
    private static void checkEnv(){
        logger.info("正在检查运行环境");
        try {
            FileLock lock = new RandomAccessFile(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + ".fly.lock", "rw").getChannel().tryLock();
            if (lock == null) {
                logger.error("不能重复启动");
                JOptionPane.showMessageDialog(null, "", "", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }

        } catch (IOException e) {
            logger.error("锁文件出错", e);
        }

    }
    private static void setStyle(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("加载系统风格失败", e);
        }
    }
    private final static Logger logger = LoggerFactory.getLogger(App.class);
}
