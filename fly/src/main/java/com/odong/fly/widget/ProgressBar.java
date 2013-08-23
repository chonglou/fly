package com.odong.fly.widget;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-23
 * Time: 上午8:40
 */
public final class ProgressBar {
    public ProgressBar() {
        processBar = new JProgressBar(0, 100);
        processBar.setStringPainted(true);
        processBar.setIndeterminate(true);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container container = frame.getContentPane();
        container.add(processBar, BorderLayout.CENTER);

    }

    public void show(boolean visible) {
        if (visible) {
            frame.setSize(400, 40);
            frame.setUndecorated(true);
            //frame.setLocationByPlatform(true);
            frame.setLocationRelativeTo(null);
        }
        frame.setVisible(visible);
    }

    public void message(String key){
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
        JOptionPane.showMessageDialog(frame,
                bundle.getString("lbl.dialog." + key + ".message"),
                bundle.getString("lbl.dialog." + key + ".title"),
                JOptionPane.ERROR_MESSAGE);
    }

    public void set(int value) {
        processBar.setValue(value);
    }

    private JProgressBar processBar;
    private JFrame frame;
    private final static ProgressBar instance = new ProgressBar();

    public static ProgressBar get() {
        return instance;
    }
}
