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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午12:16
 */
@Component("gui.toolbar")
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
        exit.setText(message.getMessage("button.exit"));
        help.setText(message.getMessage("button.help"));
    }

    public JToolBar get() {
        return toolBar;
    }

    @PostConstruct
    void init() {
        toolBar.setFloatable(false);
        this.ports = new ArrayList<>();
        this.tasks = new ArrayList<>();
        toolBar.addSeparator();


        help = new JButton();
        help.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPanel.showHelp();
            }
        });
        toolBar.add(help);
        toolBar.addSeparator();

        exit = new JButton();
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.exit();
            }
        });
        toolBar.add(exit);
        toolBar.addSeparator();
    }

    private MouseListener btnMouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            String name = ((JButton) e.getSource()).getName();
            if (name.startsWith("task://")) {
                mainPanel.showTask(name.substring(7));
            } else if (name.startsWith("serial://")) {
                mainPanel.showSerial(name.substring(10));
            } else {
                logger.error("未知的工具栏按钮", name);
            }
        }
    };

    private List<JButton> ports;
    private List<JButton> tasks;
    private JButton exit;
    private JButton help;
    @Resource
    private JToolBar toolBar;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private Message message;
    @Resource
    private MainPanel mainPanel;
    @Resource
    private Dialog dialog;
    private final static Logger logger = LoggerFactory.getLogger(ToolBar.class);

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
