package com.odong.fly.widget;


import com.odong.fly.MyException;
import com.odong.fly.model.Task;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import com.odong.fly.util.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:36
 */
@Component
public class MenuBar {
    @PostConstruct
    void init() {
        menuBar = new JMenuBar();
        menus = new HashMap<>();
        menuItems = new HashMap<>();
        checkBoxMenuItems = new ArrayList<>();

        addMenuItem("file", "gc", "exit");
        Set<String> portNames = serialUtil.getPortNameList();
        addCheckBoxMenuItem("device", portNames.toArray(new String[portNames.size()]));
        addRadioMenuItem("lang", "en_US", "zh_CN");
        addMenuItem("help", "doc", "aboutMe");

        initEvent();
    }


    public void setText() {
        for (String s : menus.keySet()) {
            menus.get(s).setText(guiHelper.getMessage("menu." + s));
        }

        for (String s : menuItems.keySet()) {
            menuItems.get(s).setText(guiHelper.getMessage("menuItem." + s));
        }
        menuItems.get(guiHelper.getLocale().toString()).setSelected(true);
    }

    public JMenuBar get() {
        return menuBar;
    }


    private void addCheckBoxMenuItem(String menu, String... items) {
        JMenu m = new JMenu();
        menuBar.add(m);
        menus.put(menu, m);

        for (int i = 0; ; ) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(items[i]);
            item.setName(menu + "-" + items[i]);
            m.add(item);
            checkBoxMenuItems.add(item);
            i++;
            if (i == items.length) {
                break;
            }
            m.addSeparator();
        }
    }

    private void addRadioMenuItem(String menu, String... items) {
        JMenu m = new JMenu();
        menuBar.add(m);
        menus.put(menu, m);

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; ; ) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem();
            item.setName("item-" + items[i]);
            m.add(item);
            group.add(item);
            this.menuItems.put(items[i], item);
            i++;
            if (i == items.length) {
                break;
            }
            m.addSeparator();

        }
    }

    private void addMenuItem(String menu, String... items) {
        JMenu m = new JMenu();
        menuBar.add(m);
        menus.put(menu, m);

        for (int i = 0; ; ) {
            JMenuItem item = new JMenuItem();
            item.setName("item-" + items[i]);
            m.add(item);
            this.menuItems.put(items[i], item);
            i++;
            if (i == items.length) {
                break;
            }
            m.addSeparator();

        }
    }


    private void initEvent() {

        ActionListener checkBoxListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                if (item.isEnabled()) {
                    if (item.getName().startsWith("device-")) {
                        String portName = item.getName().substring(7);
                        if (item.isSelected()) {
                            serialDialog.show(portName);
                        } else {
                            if (storeHelper.listTask(Task.State.SUBMIT).size() > 0) {
                                guiHelper.showErrorDialog(MyException.Type.SERIAL_PORT_IN_USE);
                            } else {
                                serialUtil.close(portName);
                                cardPanel.showHelp();
                            }
                        }
                        item.setSelected(serialUtil.isOpen(portName));
                        toolBar.refresh();
                    }
                }
            }
        };
        for (JCheckBoxMenuItem item : checkBoxMenuItems) {
            item.addActionListener(checkBoxListener);
        }

        ActionListener itemListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();
                switch (item.getName().split("\\-")[1]) {
                    case "doc":
                        cardPanel.showHelp();
                        break;
                    case "aboutMe":
                        guiHelper.showInfoDialog("aboutMe");
                        break;
                    case "gc":
                        if (guiHelper.showConfirmDialog("gc")) {
                            logger.info("内存整理");
                            System.gc();
                            guiHelper.showInfoDialog("success");
                        }
                        break;
                    case "exit":
                        guiHelper.showExitDialog();
                        break;
                    case "en_US":
                        window.setLocale(Locale.US);
                        break;
                    case "zh_CN":
                        window.setLocale(Locale.SIMPLIFIED_CHINESE);
                        break;
                    default:
                        break;

                }
            }
        };

        for (String s : menuItems.keySet()) {
            menuItems.get(s).addActionListener(itemListener);
        }
    }

    private JMenuBar menuBar;
    private Map<String, JMenu> menus;
    private Map<String, JMenuItem> menuItems;
    private List<JCheckBoxMenuItem> checkBoxMenuItems;

    @Resource
    private CardPanel cardPanel;
    @Resource
    private GuiHelper guiHelper;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private SerialDialog serialDialog;
    @Resource
    private Window window;
    @Resource
    private ToolBar toolBar;
    @Resource
    private StoreHelper storeHelper;

    private final static Logger logger = LoggerFactory.getLogger(MenuBar.class);

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setCardPanel(CardPanel cardPanel) {
        this.cardPanel = cardPanel;
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

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

}
