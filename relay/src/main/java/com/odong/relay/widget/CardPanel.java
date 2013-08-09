package com.odong.relay.widget;

import com.odong.relay.job.TaskJob;
import com.odong.relay.util.LabelHelper;
import com.odong.relay.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:13
 */
@Component
public class CardPanel {
    public synchronized void close() {
        for (String s : channelPanels.keySet()) {
            ChannelPanel cp = channelPanels.get(s);
            if (cp.isOn()) {
                cp.setOn(false);
            }
        }
    }

    public synchronized void hide() {
        panel.setVisible(false);
    }

    public synchronized void show(int port) {
        String name = port2name(port);
        layout.show(panel, name);
        panel.setVisible(true);
        channelPanels.get(name).refreshLogList();

    }

    @PostConstruct
    void init() {
        channelPanels = new HashMap<>();
        panel = new JPanel();
        layout = new CardLayout();
        panel.setLayout(layout);
        for (int i = 1; i <= size; i++) {
            String name = port2name(i);
            ChannelPanel cp = new ChannelPanel(name, i, locale, toolBar, labelHelper, logService, messageDialog, taskJob);
            channelPanels.put(name, cp);
            panel.add(cp.get(), name);
        }
        panel.setVisible(false);
    }

    public void setLocale(Locale locale) {
        for (String s : channelPanels.keySet()) {
            channelPanels.get(s).setLocale(locale);
        }
        this.locale = locale;

    }

    private String port2name(int port) {
        return "panel-" + port;
    }

    private JPanel panel;
    private CardLayout layout;
    private Map<String, ChannelPanel> channelPanels;
    private Locale locale;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private StoreHelper logService;
    @Resource
    private ToolBar toolBar;
    @Resource
    private TaskJob taskJob;
    @Resource
    private MessageDialog messageDialog;
    @Value("${channel.size}")
    private int size;
    private final static Logger logger = LoggerFactory.getLogger(CardPanel.class);

    public void setMessageDialog(MessageDialog messageDialog) {
        this.messageDialog = messageDialog;
    }

    public void setTaskJob(TaskJob taskJob) {
        this.taskJob = taskJob;
    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setLogService(StoreHelper logService) {
        this.logService = logService;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }

    public JPanel get() {
        return panel;
    }
}
