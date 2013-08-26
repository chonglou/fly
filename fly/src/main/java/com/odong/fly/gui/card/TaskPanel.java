package com.odong.fly.gui.card;


import com.odong.fly.model.Task;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 下午12:46
 */
public abstract class TaskPanel {

    public abstract void show(Task task);

    public abstract String name();

    public abstract void setText();

    protected abstract void initEvents();

    protected abstract void initPanel();

    public TaskPanel() {
        panel = new JPanel(new GridBagLayout());
        panel.setName(name());
        initPanel();
        initEvents();
    }

    public JPanel get() {
        return panel;
    }

    protected JPanel panel;

}
