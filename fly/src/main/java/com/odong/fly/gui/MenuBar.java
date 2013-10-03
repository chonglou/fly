package com.odong.fly.gui;

import com.odong.fly.MyException;
import com.odong.fly.camera.CameraUtil;
import com.odong.fly.model.Task;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.model.request.PhotoRequest;
import com.odong.fly.model.request.VideoRequest;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午12:16
 */
@Component("gui.menuBar")
public class MenuBar {
    @PostConstruct
    void init() {
        menuMap = new HashMap<>();
        menuItemMap = new HashMap<>();
        deviceItemMap = new HashMap<>();

        initItems();
        initEvents();

        scanDevice();
    }


    public void setText() {
        for (JMenu menu : menuMap.values()) {
            menu.setText(message.getMessage("menu." + menu.getName()));
        }
        for (JMenuItem item : menuItemMap.values()) {
            item.setText(message.getMessage("menu." + item.getName()));
        }


        menuItemMap.get("lang." + message.getLocale().toString()).setSelected(true);
    }

    public JMenuBar get() {
        return menuBar;
    }


    @SuppressWarnings("unchecked")
    private void initItems() {
        for (Map<String, Object> map : menuBarItems) {
            addMenu((String) map.get("name"), (String) map.get("type"), (List<String>) map.get("items"));
        }
    }

    private void addMenu(String menuName, String type, List<String> items) {
        JMenu menu = new JMenu();
        menu.setName(menuName);
        switch (type) {
            case "text":
                for (Object obj : items) {
                    if ("".equals(obj)) {
                        menu.addSeparator();
                    } else {
                        JMenuItem item = new JMenuItem();
                        String name = getMenuItemName(menuName, (String) obj);
                        item.setName(name);
                        menu.add(item);
                        menuItemMap.put(name, item);
                    }
                }
                break;
            case "radio":
                ButtonGroup group = new ButtonGroup();
                for (Object obj : items) {
                    if ("".equals(obj)) {
                        menu.addSeparator();
                    } else {
                        JRadioButtonMenuItem item = new JRadioButtonMenuItem();
                        String name = getMenuItemName(menuName, (String) obj);
                        item.setName(name);
                        group.add(item);
                        menu.add(item);
                        menuItemMap.put(name, item);
                    }
                }
                break;
            case "checkBox":
                for (Object obj : items) {
                    if ("".equals(obj)) {
                        menu.addSeparator();
                    } else {
                        JCheckBoxMenuItem item = new JCheckBoxMenuItem();
                        String name = getMenuItemName(menuName, (String) obj);
                        item.setName(name);
                        menu.add(item);
                        menuItemMap.put(name, item);
                    }
                }
                break;
        }
        menuBar.add(menu);
        menuMap.put(menuName, menu);
    }

    private String getMenuItemName(String menuName, String itemName) {
        return menuName + "." + itemName;
    }

    private void initEvents() {

        ActionListener listener = (e) -> {

            JMenuItem item = (JMenuItem) e.getSource();
            if (item.isEnabled()) {
                //logger.debug("点击菜单栏[{}]", item.getName());
                switch (item.getName()) {
                    case "file.gc":
                        dialog.confirm("gc", () -> {
                            logger.info("内存整理");
                            System.gc();
                            dialog.info("success");
                        }, null);
                        break;
                    case "file.scan":
                        dialog.confirm("scanDevice", () -> {
                            scanDevice();
                            dialog.info("success");
                        }, null);
                        break;
                    case "file.exit":
                        dialog.exit();
                        break;
                    case "help.doc":
                        mainPanel.showHelp();
                        break;
                    case "help.aboutMe":
                        dialog.info("aboutMe");
                        break;
                    case "lang.en_US":
                        window.setLocale(Locale.US);
                        break;
                    case "lang.zh_CN":
                        window.setLocale(Locale.SIMPLIFIED_CHINESE);
                        break;
                    default:
                        break;

                }
            }
        };

        for (JMenuItem item : menuItemMap.values()) {
            item.addActionListener(listener);
        }

        deviceItemListener = (e) -> {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
            if (item.isEnabled()) {
                String name = item.getName();
                logger.debug("点击设备[{}][{}]", name, item.isSelected());
                if (name.startsWith("device.serial.")) {
                    String portName = name.substring(14);
                    if (item.isSelected()) {
                        serialDialog.show(portName);
                    } else {

                        for (Task t : storeHelper.listTask(Task.State.SUBMIT)) {
                            if (t.getType() == Task.Type.ON_OFF && portName.equals(((OnOffRequest) t.getRequest()).getPortName())) {
                                dialog.error(MyException.Type.SERIAL_PORT_IN_USE);
                                item.setSelected(true);
                                return;
                            }
                        }

                        serialUtil.close(portName);
                        mainPanel.showHelp();
                    }
                    item.setSelected(serialUtil.isOpen(portName));
                    toolBar.refresh();
                } else if (name.startsWith("device.camera.")) {
                    int deviceId = Integer.parseInt(name.substring(14));
                    if (item.isSelected()) {
                        try {
                            cameraUtil.open(deviceId, item.getText());
                        } catch (IOException ex) {
                            logger.error("打开摄像头[{}]失败", deviceId, ex);
                            dialog.error(MyException.Type.CAMERA_IO_ERROR);
                        }

                    } else {
                        for (Task t : storeHelper.listRunnableTask()) {
                            if (
                                    (t.getType() == Task.Type.PHOTO && deviceId == ((PhotoRequest) t.getRequest()).getDevice()) ||
                                            (t.getType() == Task.Type.VIDEO && deviceId == ((VideoRequest) t.getRequest()).getDevice())
                                    ) {
                                dialog.error(MyException.Type.CAMERA_IN_USE);
                                return;
                            }
                        }
                        try {
                            cameraUtil.close(deviceId);
                        } catch (IOException ex) {
                            logger.error("关闭摄像头失败", ex);
                        }
                        mainPanel.showHelp();
                    }
                    item.setSelected(cameraUtil.isOpen(deviceId));
                    toolBar.refresh();
                }
            }
        };
    }

    private synchronized void scanDevice() {
        logger.info("正在扫描设备列表");
        String menuName = "device";
        JMenu menu = menuMap.get(menuName);
        for (String portName : serialUtil.listPortName()) {
            String name = menuName + ".serial." + portName;
            if (deviceItemMap.get(name) == null) {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(portName);
                item.setName(name);
                if (menu.getItemCount() > 0) {
                    menu.addSeparator();
                }
                item.addActionListener(deviceItemListener);
                menu.add(item);
                deviceItemMap.put(name, item);
            }
        }

        Map<Integer, String> cameraMap = cameraUtil.listDevice();
        for (Integer deviceId : cameraMap.keySet()) {
            String name = menuName + ".camera." + deviceId;
            if (deviceItemMap.get(name) == null) {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(cameraMap.get(deviceId));
                if (menu.getItemCount() > 0) {
                    menu.addSeparator();
                }
                item.setName(name);
                item.addActionListener(deviceItemListener);
                menu.add(item);
                deviceItemMap.put(name, item);
            }
        }
    }

    private ActionListener deviceItemListener;
    private Map<String, JMenu> menuMap;
    private Map<String, JMenuItem> menuItemMap;
    private Map<String, JCheckBoxMenuItem> deviceItemMap;

    @Resource(name = "menuBar")
    private JMenuBar menuBar;
    @Resource
    private Window window;
    @Resource(name = "gui.mainPanel")
    private MainPanel mainPanel;
    @Resource(name = "gui.dialog")
    private Dialog dialog;
    @Resource
    private Message message;
    @Resource(name = "menuBar.items")
    private List<Map<String, Object>> menuBarItems;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private CameraUtil cameraUtil;
    @Resource
    private SerialDialog serialDialog;
    @Resource(name = "gui.toolBar")
    private ToolBar toolBar;
    @Resource
    private StoreHelper storeHelper;
    private final static Logger logger = LoggerFactory.getLogger(MenuBar.class);

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setSerialDialog(SerialDialog serialDialog) {
        this.serialDialog = serialDialog;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setCameraUtil(CameraUtil cameraUtil) {
        this.cameraUtil = cameraUtil;
    }

    public void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void setMenuBarItems(List<Map<String, Object>> menuBarItems) {
        this.menuBarItems = menuBarItems;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
