package com.odong.relay.widget;

import com.odong.relay.job.Task;
import com.odong.relay.job.TaskJob;
import com.odong.relay.serial.SerialUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:14
 */
@Component
public class ToolBar {
    public void refresh(){
        toolBar.addSeparator();
        for(JButton btn : ports){
            toolBar.remove(btn);
        }
        ports.clear();
        for(JButton btn : tasks){
            toolBar.remove(btn);
        }
        tasks.clear();

        for(String portName : serialUtil.getStatus()){
            JButton btn = new JButton(portName);
            btn.setName("port://"+portName);
            ports.add(btn);
        }
        for(String tid : taskJob.getTaskList()){
            Task task = storeHelper.getTask(tid);
            JButton btn = new JButton(task.toString());
            btn.setName("task://"+task.getId());
            tasks.add(btn);
        }

        for(JButton btn : tasks){
            toolBar.add(btn,0);
        }

        for(JButton btn : ports){
            toolBar.add(btn,0);
        }

    }

    public void setText() {
        exit.setText(guiHelper.getMessage("button.exit"));
    }

    public JToolBar get() {
        return toolBar;
    }

    @PostConstruct
    void init() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        this.ports = new ArrayList<>();
        this.tasks = new ArrayList<>();
        toolBar.addSeparator();

        exit = new JButton();
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                guiHelper.showExitDialog();
            }
        });
        toolBar.add(exit);
        toolBar.addSeparator();
    }

    private JToolBar toolBar;
    @Resource
    private GuiHelper guiHelper;
    @Resource
    private TaskJob taskJob;
    @Resource
    private CardPanel cardPanel;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private StoreHelper storeHelper;
    private List<JButton> ports;
    private List<JButton> tasks;
    private JButton exit;
    private final static Logger logger = LoggerFactory.getLogger(ToolBar.class);

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

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
