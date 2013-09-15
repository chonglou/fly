package com.odong.fly.gui;

import com.odong.fly.camera.CameraUtil;
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
    private void addSeparator(boolean sep) {
        if (sep) {
            toolBar.addSeparator();
        }
    }

    public synchronized void refresh() {
        toolBar.removeAll();

        boolean sep = false;

        for (String portName : serialUtil.listPortName()) {
            if (serialUtil.isOpen(portName)) {
                addButton(PREFIX_SERIAL + portName, portName);
                sep = true;
            }
        }
        addSeparator(sep);

        Map<Integer, String> cameraIds = cameraUtil.getStatus();
        sep = false;
        for (int cameraId : cameraIds.keySet()) {
            if (cameraUtil.isOpen(cameraId)) {
                sep = true;
                addButton(PREFIX_CAMERA + cameraId, cameraIds.get(cameraId));
            }
        }
        addSeparator(sep);


        sep = false;

        for (Task task : storeHelper.listAvailableTask(Task.Type.ON_OFF)) {
            OnOffRequest oor = (OnOffRequest) task.getRequest();
            //TODO 不同状态 不同颜色
            if (serialUtil.isOpen(oor.getPortName())) {
                sep = true;
                addButton(PREFIX_TASK + task.getId(), oor.name());
            }
        }
        addSeparator(sep);


        for (String k : toolBarItems) {
            addButton(k, message.getMessage("button." + k));
        }

        toolBar.repaint();
    }

    public void setText() {
        refresh();
    }

    public JToolBar get() {
        return toolBar;
    }

    @PostConstruct
    void init() {
        toolBar.setFloatable(false);
        toolBar.addSeparator();

        initButtons();
        initEvents();
    }

    private void addButton(String name, String text) {
        JButton btn = new JButton(text);
        btn.setName(name);
        btn.addMouseListener(btnMouseListener);
        toolBar.add(btn);
    }

    private void initButtons() {
        for (String s : toolBarItems) {
            toolBar.addSeparator();
            JButton btn = new JButton();
            btn.setName(s);
            toolBar.add(btn);
        }
        toolBar.addSeparator();
    }

    private void initEvents() {
        btnMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String name = ((JButton) e.getSource()).getName();
                logger.debug("点击工具栏[{}]", name);
                if (name.startsWith(PREFIX_TASK)) {
                    mainPanel.showTask(name.substring(PREFIX_TASK.length()));
                } else if (name.startsWith(PREFIX_SERIAL)) {
                    mainPanel.showSerial(name.substring(PREFIX_SERIAL.length()));
                } else if (name.startsWith(PREFIX_CAMERA)) {
                    mainPanel.showCamera(Integer.parseInt(name.substring(PREFIX_CAMERA.length())));
                } else if (name.equals("help")) {
                    mainPanel.showHelp();
                } else if (name.equals("exit")) {
                    dialog.exit();
                } else {
                    logger.error("未知的工具栏按钮", name);
                }
            }
        };
    }

    private MouseListener btnMouseListener;
    @Resource(name = "toolBar")
    private JToolBar toolBar;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private CameraUtil cameraUtil;
    @Resource
    private Message message;
    @Resource(name = "gui.mainPanel")
    private MainPanel mainPanel;
    @Resource(name = "gui.dialog")
    private Dialog dialog;
    @Resource(name = "toolBar.items")
    private List<String> toolBarItems;
    private final static String PREFIX_SERIAL = "serial://";
    private final static String PREFIX_TASK = "task://";
    private final static String PREFIX_CAMERA = "camera://";
    private final static Logger logger = LoggerFactory.getLogger(ToolBar.class);

    public void setCameraUtil(CameraUtil cameraUtil) {
        this.cameraUtil = cameraUtil;
    }

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
