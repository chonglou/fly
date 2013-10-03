package com.odong.fly;

import com.odong.fly.serial.impl.SerialPortRxtxImpl;
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
        try {
            new SerialPortRxtxImpl().listPortName();
        } catch (Exception | UnsatisfiedLinkError e) {
            logger.error("串口打开出错", e);
            exitDlg("serialIo");
        }
        /*
        try{
            new CameraUtilOpenCVImpl().listDevice();
        }
        catch (Exception | UnsatisfiedLinkError e){
            logger.error("摄像头打开出错",e);
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
