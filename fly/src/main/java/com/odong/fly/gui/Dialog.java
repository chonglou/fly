package com.odong.fly.gui;

import com.odong.core.util.JsonHelper;
import com.odong.fly.MyException;
import com.odong.fly.camera.CameraUtil;
import com.odong.fly.serial.SerialUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.incrementer.DB2MainframeSequenceMaxValueIncrementer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午12:27
 */
@Component("gui.dialog")
public class Dialog {


    public void exit(){
        if (serialUtil.hasOpen() || cameraUtil.hasOpen()) {
            logger.error("当前串口状态[{}]", jsonHelper.object2json(serialUtil.getStatus()));
            logger.error("当前摄像头状态[{}]", jsonHelper.object2json(cameraUtil.getStatus()));
            error("stillOpen");
        } else {
            System.exit(0);
        }
    }

    public void confirm(String key, Runnable yes, Runnable no) {
        switch (JOptionPane.showConfirmDialog(
                mainFrame,
                message.getMessage("dialog." + key + ".message"),
                message.getMessage("dialog." + key + ".title"),
                JOptionPane.YES_NO_OPTION)) {
            case JOptionPane.YES_OPTION:
                if(yes!=null){
                    yes.run();
                }
                break;
            case JOptionPane.NO_OPTION:
                if(no != null){
                    no.run();
                }
                break;
        }
    }


    public void error( MyException.Type type) {
        JOptionPane.showMessageDialog(mainFrame,
                message.getMessage("exception." + type.name()),
                type.name(),
                JOptionPane.ERROR_MESSAGE);
    }

    public void error( String key) {
        JOptionPane.showMessageDialog(mainFrame,
                message.getMessage("dialog." + key + ".message"),
                message.getMessage("dialog." + key + ".title"),
                JOptionPane.ERROR_MESSAGE);
    }

    public void info(String key) {
        JOptionPane.showMessageDialog(mainFrame,
                message.getMessage("dialog." + key + ".message"),
                message.getMessage("dialog." + key + ".title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Resource
    private Message message;
    @Resource
    private JFrame mainFrame;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private CameraUtil cameraUtil;
    private final static Logger logger = LoggerFactory.getLogger(Dialog.class);

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

    public void setCameraUtil(CameraUtil cameraUtil) {
        this.cameraUtil = cameraUtil;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
