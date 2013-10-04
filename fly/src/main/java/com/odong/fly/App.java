package com.odong.fly;

import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.channels.FileLock;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class App {
    public static void main(String[] args) {
        setStyle();
        checkEnv();
        init();
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("spring/*.xml");
        ctx.registerShutdownHook();
    }

    private static void init() {
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

    private static void load(String dir) {

        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);

            String[] paths = (String[]) field.get(null);
            for (String s : paths) {
                if (dir.equals(s)) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = dir;
            field.set(null, tmp);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            logger.error("加载动态链接库出错", e);
            exitDlg("3rd");
        }
    }

    private static void checkEnv() {
        logger.info("正在检查运行环境");
        try (FileLock lock = new RandomAccessFile(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + ".fly.lock", "rw").getChannel().tryLock()) {
            if (lock == null) {
                logger.error("不能重复启动");
                exitDlg("duplicateBoot");
            }

        } catch (IOException e) {
            logger.error("锁文件出错", e);
            exitDlg("unknown");
        }


        if (System.getProperty("os.name").contains("Windows")) {
            //Windows环境 只支持32位
            //System.setProperty("java.library.path", "./3rd/win/x86");

            load("./3rd/win/x86");
        }

        for (String lib : new String[]{"rxtxSerial", Core.NATIVE_LIBRARY_NAME}) {
            System.loadLibrary(lib);
        }

        /*
        try {
            logger.debug("串口列表{}", new SerialPortRxtxImpl().listPortName());
        } catch (Exception | UnsatisfiedLinkError e) {
            logger.error("串口打开出错", e);
            exitDlg("serialIo");
        }


        try {
            logger.debug("摄像头列表{}", new CameraUtilOpenCVImpl().listDevice());
        } catch (Exception | UnsatisfiedLinkError e) {
            logger.error("摄像头打开出错", e);
            exitDlg("cameraIo");
        }
        */
    }

    private static void exitDlg(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        JFrame frame = new JFrame();
        frame.setVisible(false);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(App.class.getResource("/tray.png")));
        JOptionPane.showMessageDialog(
                frame,
                bundle.getString("lbl.dialog." + key + ".message"),
                bundle.getString("lbl.dialog." + key + ".title"),
                JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }

    private static void setStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("加载系统风格失败", e);
        }

        FontUIResource font = new FontUIResource(new Font("宋体", Font.PLAIN, 16));

        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(App.class);
}
