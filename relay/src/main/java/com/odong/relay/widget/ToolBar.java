package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:14
 */
@Component
public class ToolBar {

    @PostConstruct
    void init() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.addSeparator();

        buttons = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JButton btn = new JButton();
            toolBar.add(btn);
            toolBar.addSeparator();

            buttons.add(btn);
        }
    }

    public void setEnable(boolean enable) {
        for (JButton btn : buttons) {
            btn.setEnabled(enable);
        }
    }

    public void setLocale(Locale locale) {
        for (int i = 0; i < size; i++) {
            buttons.get(i).setText(labelHelper.getMessage("button.port", locale) + (i + 1));
        }
    }

    private JToolBar toolBar;
    private java.util.List<JButton> buttons;
    @Value("${channel.size}")
    private int size;
    @Resource
    private LabelHelper labelHelper;

    public JToolBar get() {
        return toolBar;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
