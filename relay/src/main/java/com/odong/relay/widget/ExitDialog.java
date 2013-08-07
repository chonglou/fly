package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午1:14
 */
@Component
public class ExitDialog {

    public void show() {
        JOptionPane pane = new JOptionPane(message,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[1]);
        JDialog dialog = pane.createDialog(window.get(), title);
        dialog.setVisible(true);
        Object sel = pane.getValue();
        if (sel == options[0]) {
            if (serialDialog.isOpen()) {
                messageDialog.error("stillOpen");
            } else {
                logger.info("停止");
                System.exit(0);
            }

        }
    }

    public void setLocale(Locale locale) {
        options = new Object[]{
                labelHelper.getMessage("button.ok", locale),
                labelHelper.getMessage("button.cancel", locale)
        };
        message = labelHelper.getMessage("dialog.exit.message", locale);

        title = labelHelper.getMessage("dialog.exit.title", locale);


    }

    private String title;
    private Object[] options;
    private String message;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private Window window;
    @Resource
    private SerialDialog serialDialog;
    @Resource
    private MessageDialog messageDialog;
    private final static Logger logger = LoggerFactory.getLogger(ExitDialog.class);

    public void setMessageDialog(MessageDialog messageDialog) {
        this.messageDialog = messageDialog;
    }

    public void setSerialDialog(SerialDialog serialDialog) {
        this.serialDialog = serialDialog;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
