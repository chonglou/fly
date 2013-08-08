package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        buttons = new HashMap<>();
        for (int i = 0; i < size; i++) {
            JButton btn = new JButton();
            String name = getButtonName(i + 1);
            btn.setName(name);
            toolBar.add(btn);
            toolBar.addSeparator();
            buttons.put(name, btn);
        }

        initEvents();

    }

    private String getButtonName(int port) {
        return "button_" + port;
    }

    private int getButtonPort(String name) {
        return Integer.parseInt(name.split("_")[1]);
    }

    public void setOn(int i, boolean on) {

        buttons.get(getButtonName(i)).setBackground(on ? Color.green : new JButton().getBackground());
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        for (String s : buttons.keySet()) {
            buttons.get(s).setEnabled(enable);
        }
    }

    public void setLocale(Locale locale) {
        for (String s : buttons.keySet()) {
            buttons.get(s).setText(labelHelper.getMessage("button.port", locale) + getButtonPort(s));
        }
    }

    private void initEvents() {
        MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton btn = (JButton) e.getSource();

                if (btn.isEnabled()) {
                    cardPanel.show(getButtonPort(btn.getName()));
                    setEnable(enable);
                    btn.setEnabled(false);

                }

            }
        };
        for (String s : buttons.keySet()) {
            buttons.get(s).addMouseListener(listener);
        }
    }

    private JToolBar toolBar;
    private Map<String, JButton> buttons;
    private boolean enable;
    @Value("${channel.size}")
    private int size;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private CardPanel cardPanel;
    private final static Logger logger = LoggerFactory.getLogger(ToolBar.class);

    public void setCardPanel(CardPanel cardPanel) {
        this.cardPanel = cardPanel;
    }

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
