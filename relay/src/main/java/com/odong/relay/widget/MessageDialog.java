package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午2:03
 */
@Component
public class MessageDialog {
    public void error(String key){
        JOptionPane.showMessageDialog(window.get(),
                labelHelper.getMessage("dialog."+key+".message", locale),
                labelHelper.getMessage("dialog."+key+".title", locale),
                JOptionPane.ERROR_MESSAGE);
    }
    public void info(String key){
        JOptionPane.showMessageDialog(window.get(),
                labelHelper.getMessage("dialog."+key+".message", locale),
                labelHelper.getMessage("dialog."+key+".title", locale),
                JOptionPane.INFORMATION_MESSAGE);
    }


    public void setLocale(Locale locale){
        this.locale = locale;
    }
    private Locale locale;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private Window window;

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
