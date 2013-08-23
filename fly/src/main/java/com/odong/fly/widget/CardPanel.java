package com.odong.fly.widget;

import com.odong.fly.model.Task;
import com.odong.fly.serial.SerialPort;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.util.StoreHelper;
import com.odong.fly.widget.card.HelpPanel;
import com.odong.fly.widget.card.OnOffTaskPanel;
import com.odong.fly.widget.card.TaskPanel;
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
 * Date: 13-8-7
 * Time: 上午11:13
 */
@Component
public class CardPanel {
    public synchronized void showPort(String portName){
        //FIXME
    }
    public synchronized void showHelp() {
        layout.show(panel, "doc");
    }

    public synchronized void showTask(String taskId) {
        Task task = storeHelper.getTask(taskId);
        String name = task.getType().name();

        layout.show(panel, name);

        TaskPanel tp;
        switch (task.getType()) {
            case ON_OFF:
                tp = onOffPanel;
                break;
            default:
                return;
        }
        tp.show(task);
    }

    @PostConstruct
    void init() {
        panel = new JPanel();
        layout = new CardLayout();
        panel.setLayout(layout);

        panel.add(onOffPanel.get(), onOffPanel.name());
        panel.add(helpPanel.get(), "doc");
        panel.setVisible(true);
    }

    public void setText() {
        onOffPanel.setText();
        helpPanel.setText();
    }


    public JPanel get() {
        return panel;
    }

    private JPanel panel;
    private CardLayout layout;
    @Resource
    private OnOffTaskPanel onOffPanel;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private HelpPanel helpPanel;
    private final static Logger logger = LoggerFactory.getLogger(CardPanel.class);

    public void setHelpPanel(HelpPanel helpPanel) {
        this.helpPanel = helpPanel;
    }

    public void setOnOffPanel(OnOffTaskPanel onOffPanel) {
        this.onOffPanel = onOffPanel;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

}
