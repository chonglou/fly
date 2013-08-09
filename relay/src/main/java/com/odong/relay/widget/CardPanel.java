package com.odong.relay.widget;

import com.odong.relay.job.Task;
import com.odong.relay.job.TaskJob;
import com.odong.relay.util.GuiHelper;
import com.odong.relay.util.StoreHelper;
import com.odong.relay.widget.task.OnOffTaskPanel;
import com.odong.relay.widget.task.TaskPanel;
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

    public synchronized void hide() {
        panel.setVisible(false);
    }

    public synchronized void show(String taskId) {
        Task task = storeHelper.getTask(taskId);
        String name = task.getType().name();
        layout.show(panel, name);
        panel.setVisible(true);

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

        onOffPanel = new OnOffTaskPanel();
        panel.add(onOffPanel.get(), onOffPanel.name());
        panel.setVisible(false);
    }

    public void setText() {
        onOffPanel.setText();
    }


    public JPanel get() {
        return panel;
    }

    private JPanel panel;
    private CardLayout layout;
    private OnOffTaskPanel onOffPanel;
    @Resource
    private GuiHelper guiHelper;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private TaskJob taskJob;
    private final static Logger logger = LoggerFactory.getLogger(CardPanel.class);


    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setTaskJob(TaskJob taskJob) {
        this.taskJob = taskJob;
    }
}
