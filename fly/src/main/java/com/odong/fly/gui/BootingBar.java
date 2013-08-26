package com.odong.fly.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午12:20
 */
@Component("gui.bootingBar")
public class BootingBar {
    public synchronized void next(){
        int value = processBar.getValue()+10;
        if(value>MAX){
            dispose();
        }
        else {
            processBar.setValue(value);
        }
    }

    public void dispose(){
        frame.dispose();

    }

    @PostConstruct
    void init(){
        processBar = new JProgressBar(0, MAX);
        processBar.setStringPainted(true);
        processBar.setIndeterminate(true);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(message.getMessage("booting"));
        frame.setIconImage(message.getIcon());
        Container container = frame.getContentPane();
        container.add(processBar, BorderLayout.CENTER);


        frame.setSize(400, 40);
        frame.setUndecorated(true);
        //frame.setLocationByPlatform(true);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    private JProgressBar processBar;
    private JFrame frame;
    public static final int MAX=100;
    @Resource
    private Message message;
    private final static Logger logger = LoggerFactory.getLogger(BootingBar.class);

    public void setMessage(Message message) {
        this.message = message;
    }
}
