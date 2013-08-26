package com.odong.fly.gui.card;

import com.odong.fly.gui.Message;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 下午11:32
 */
@Component
public class HelpPanel {
    public String name(){
        return "doc";
    }
    @PostConstruct
    void init() {
        panel = new JPanel(new GridLayout(1, 1));
        content = new JLabel();
        content.setVerticalAlignment(SwingConstants.TOP);
        panel.add(content);
    }

    public void setText() {
        content.setText("<html>" + message.getMessage("help.doc") + "</html>");
    }

    public JPanel get() {
        return panel;
    }

    private JLabel content;
    private JPanel panel;
    @Resource
    private Message message;

    public void setMessage(Message message) {
        this.message = message;
    }
}
