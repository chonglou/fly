package com.odong.fly.widget;

import com.odong.core.util.JsonHelper;
import com.odong.fly.model.Task;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import com.odong.fly.util.GuiHelper;
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
 * Date: 13-8-7
 * Time: 上午11:14
 */
@Component
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
            btn.setName("port://" + portName);
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
        exit.setText(guiHelper.getMessage("button.exit"));
        help.setText(guiHelper.getMessage("button.help"));
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


        help = new JButton();
        help.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardPanel.showHelp();
            }
        });
        toolBar.add(help);
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

    private MouseListener btnMouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            String name = ((JButton) e.getSource()).getName();
            if (name.startsWith("task://")) {
                cardPanel.showTask(name.substring(7));
            } else if (name.startsWith("port://")) {
                cardPanel.showSerialOnOff(name.substring(7));
            } else {
                logger.error("未知的工具栏按钮", name);
            }
        }
    };
    private JToolBar toolBar;
    @Resource
    private GuiHelper guiHelper;
    @Resource
    private CardPanel cardPanel;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private StoreHelper storeHelper;
    private List<JButton> ports;
    private List<JButton> tasks;
    private JButton exit;
    private JButton help;
    private final static Logger logger = LoggerFactory.getLogger(ToolBar.class);

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

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

}
