package com.odong.fly.gui;

import javax.swing.*;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午6:20
 */

public abstract class DateTimePanel {
    public abstract void setEnable(boolean enable);

    public abstract void setText(Map<String, String> map);

    public abstract void setDate(Date date);

    public abstract Date getDate();

    public DateTimePanel() {
        this.panel = new JPanel();
    }

    public JPanel get() {
        return panel;
    }

    protected JPanel panel;
}
