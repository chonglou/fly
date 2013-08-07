package com.odong.relay.widget;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:13
 */
@Component
public class Channel {

    @PostConstruct
    void init() {
        panel = new JPanel();
    }

    public void setLocale(Locale locale) {

    }

    private JPanel panel;

    public JPanel getPanel() {
        return panel;
    }
}
