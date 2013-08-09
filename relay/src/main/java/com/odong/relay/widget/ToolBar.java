package com.odong.relay.widget;

import com.odong.relay.job.Task;
import com.odong.relay.job.TaskJob;
import com.odong.relay.util.GuiHelper;
import com.odong.relay.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:14
 */
@Component
public class ToolBar {
    public void show() {
        toolBar.removeAll();
        toolBar.addSeparator();


        MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                cardPanel.show(btn.getName());
            }
        };
        for (String tid : taskJob.getTaskList()) {
            Task task = storeHelper.getTask(tid);
            JButton btn = new JButton(task.toString());
            btn.setName(task.getId());
            btn.addMouseListener(listener);
            toolBar.add(btn);
        }

        toolBar.addSeparator();
    }

    public void setText() {

    }

    public JToolBar get() {
        return toolBar;
    }

    @PostConstruct
    void init() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        show();
    }

    private JToolBar toolBar;
    @Resource
    private GuiHelper guiHelper;
    @Resource
    private TaskJob taskJob;
    @Resource
    private CardPanel cardPanel;
    @Resource
    private StoreHelper storeHelper;
    private final static Logger logger = LoggerFactory.getLogger(ToolBar.class);

    public void setCardPanel(CardPanel cardPanel) {
        this.cardPanel = cardPanel;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    public void setTaskJob(TaskJob taskJob) {
        this.taskJob = taskJob;
    }
}
