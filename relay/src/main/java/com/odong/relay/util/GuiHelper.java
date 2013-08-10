package com.odong.relay.util;

import com.odong.core.util.JsonHelper;
import com.odong.relay.MyException;
import com.odong.relay.Server;
import com.odong.relay.serial.SerialUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:17
 */
@Component
public class GuiHelper {

    public void showExitDialog() {
        switch (JOptionPane.showConfirmDialog(
                window,
                getMessage("dialog.exit.message"),
                getMessage("dialog.exit.title"),
                JOptionPane.YES_NO_OPTION)) {
            case JOptionPane.YES_OPTION:
                if (serialUtil.hasOpen()) {
                    logger.error("当前端口状态[{}]", jsonHelper.object2json(serialUtil.getStatus()));
                    showErrorDialog("stillOpen");
                } else {
                    Server.get().destroy();
                    Server.get().stop();
                }

                break;
        }
    }

    public void showErrorDialog(MyException.Type type) {
        JOptionPane.showMessageDialog(window,
                getMessage("exception." + type.name()),
                type.name(),
                JOptionPane.ERROR_MESSAGE);
    }

    public void showErrorDialog(String key) {
        JOptionPane.showMessageDialog(window,
                getMessage("dialog." + key + ".message"),
                getMessage("dialog." + key + ".title"),
                JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoDialog(String key) {
        JOptionPane.showMessageDialog(window,
                getMessage("dialog." + key + ".message"),
                getMessage("dialog." + key + ".title"),
                JOptionPane.INFORMATION_MESSAGE);
    }


    public Image getIconImage() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tray.png"));
    }

    public String getMessage(String key) {
        return messageSource.getMessage("lbl." + key, null, locale);
    }


    public void show(boolean visible) {
        if (visible) {
            //frame.setLocationRelativeTo( null);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            //frame.setSize(d.width / 2, d.height / 2);
            window.setSize(800, 600);
            window.setLocation(d.width / 4, d.height / 4);

            //frame.pack();

        }
        window.setVisible(visible);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.window.setTitle(getMessage("title"));
    }

    @PostConstruct
    void init() {
        locale = Locale.SIMPLIFIED_CHINESE;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("加载系统风格失败", e);
        }
        window = new JFrame();
    }

    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private MessageSource messageSource;
    @Resource
    private SerialUtil serialUtil;
    private Locale locale;
    private JFrame window;
    private final static Logger logger = LoggerFactory.getLogger(GuiHelper.class);

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public Locale getLocale() {
        return locale;
    }

    public JFrame getWindow() {
        return window;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
