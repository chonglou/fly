package com.odong.fly.gui;

import com.odong.fly.gui.card.HelpPanel;
import com.odong.fly.gui.card.OnOffTaskPanel;
import com.odong.fly.gui.card.TaskPanel;
import com.odong.fly.model.Task;
import com.odong.fly.serial.SerialPort;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
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
 * Time: 下午5:02
 */
@Component("gui.mainPanel")
public class MainPanel {
    public synchronized void showHelp() {

        layout.show(rootP, helpPanel.name());

    }

    public void showTask(String taskId) {
        Task task = storeHelper.getTask(taskId);
        String name = task.getType().name();

        layout.show(rootP, name);

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

    public synchronized void showSerial(String portName) {
        SerialPort.Type type = serialUtil.getType(portName);
        layout.show(rootP, type.name());

        switch (type) {
            case ON_OFF:
                onOffPanel.show(portName);
                break;
        }

    }

    public void showCamera(int cameraId) {

    }

    public void setText() {
        onOffPanel.setText();
        helpPanel.setText();
    }

    @PostConstruct
    void init() {
        bootingBar.next();

        layout = new CardLayout();
        rootP.setLayout(layout);
        rootP.add(onOffPanel.get(), onOffPanel.name());
        rootP.add(helpPanel.get(), helpPanel.name());

        showHelp();
    }

    private CardLayout layout;
    @Resource(name = "mainPanel")
    private JPanel rootP;
    @Resource
    private BootingBar bootingBar;
    @Resource
    private OnOffTaskPanel onOffPanel;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private HelpPanel helpPanel;
    @Resource
    private SerialUtil serialUtil;
    private final static Logger logger = LoggerFactory.getLogger(MainPanel.class);

    public void setOnOffPanel(OnOffTaskPanel onOffPanel) {
        this.onOffPanel = onOffPanel;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setHelpPanel(HelpPanel helpPanel) {
        this.helpPanel = helpPanel;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

    public void setBootingBar(BootingBar bootingBar) {
        this.bootingBar = bootingBar;
    }

    public void setRootP(JPanel rootP) {
        this.rootP = rootP;
    }
}
