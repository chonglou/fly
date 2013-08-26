package com.odong.fly.gui;

import com.odong.core.util.JsonHelper;
import com.odong.fly.model.Task;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午12:16
 */
@Component("gui.toolBar")
public class ToolBar {

    public void refresh() {
        toolBar.addSeparator();
        for (JButton btn : ports) {
            toolBar.remove(btn);
        }
        ports.clear();
        for (JButton btn : tasks) {
            toolBar.remove(btn);
        }
        tasks.clear();

        for (String portName : serialUtil.getStatus()) {
            JButton btn = new JButton(portName);
            btn.setName("serial://" + portName);
            ports.add(btn);
        }
        for (Task task : storeHelper.listAvailableTask(Task.Type.ON_OFF)) {

            OnOffRequest r = (OnOffRequest) task.getRequest();
            if (!serialUtil.isOpen(r.getPortName())) {
                continue;
            }

            JButton btn = new JButton(task.toString());
            btn.setName("task://" + task.getId());
            tasks.add(btn);
        }

        //TODO 不同状态 不同颜色
        for (JButton btn : tasks) {
            btn.addMouseListener(btnMouseListener);
            toolBar.add(btn, 0);
        }

        for (JButton btn : ports) {
            btn.addMouseListener(btnMouseListener);
            toolBar.add(btn, 0);
        }
        toolBar.revalidate();

    }

    public void setText() {
        for(String s : toolBarItems){
            buttonMap.get(s).setText(message.getMessage("button."+s));
        }
    }

    public JToolBar get() {
        return toolBar;
    }

    @PostConstruct
    void init() {
        toolBar.setFloatable(false);
        buttonMap = new HashMap<>();
        this.ports = new ArrayList<>();
        this.tasks = new ArrayList<>();
        toolBar.addSeparator();

        initButtons();
        initEvents();
    }

    private void initButtons(){
        for(String s : toolBarItems){
            toolBar.addSeparator();
            JButton btn = new JButton();
            btn.setName(s);
            toolBar.add(btn);
            buttonMap.put(s, btn);
        }
        toolBar.addSeparator();
    }

    private void initEvents(){
        btnMouseListener =  new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String name = ((JButton) e.getSource()).getName();
                logger.debug("点击工具栏[{}]", name);
                if (name.startsWith("task://")) {
                    mainPanel.showTask(name.substring(7));
                } else if (name.startsWith("serial://")) {
                    mainPanel.showSerial(name.substring(9));
                } else if(name.equals("doc")){
                    mainPanel.showHelp();
                }
                else if(name.equals("exit")){
                    dialog.exit();
                }
                else{
                    logger.error("未知的工具栏按钮", name);
                }
            }
        };

        for(JButton btn : buttonMap.values()){
            btn.addMouseListener(btnMouseListener);
        }
    }

    private MouseListener btnMouseListener;
    private List<JButton> ports;
    private List<JButton> tasks;
    private Map<String,JButton> buttonMap;
    @Resource(name = "toolBar")
    private JToolBar toolBar;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private Message message;
    @Resource(name = "gui.mainPanel")
    private MainPanel mainPanel;
    @Resource(name = "gui.dialog")
    private Dialog dialog;
    @Resource(name="toolBar.items")
    private List<String> toolBarItems;
    private final static Logger logger = LoggerFactory.getLogger(ToolBar.class);

    public void setToolBarItems(List<String> toolBarItems) {
        this.toolBarItems = toolBarItems;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setToolBar(JToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }


    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}
