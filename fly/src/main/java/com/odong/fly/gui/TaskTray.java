package com.odong.fly.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午12:16
 */
@Component("gui.taskTray")
public class TaskTray {
    @PostConstruct
    void init() {
        if (!SystemTray.isSupported()) {
            logger.error("不支持系统托盘");
            return;
        }
        menuItemMap = new HashMap<>();

        Image img = message.getIcon();

        tray = new TrayIcon(img, "", createPopMenu());
        tray.setImageAutoSize(true);

        initEvents();
        try {
            SystemTray.getSystemTray().add(tray);
        } catch (AWTException e) {
            logger.error("添加托盘图标失败", e);
        }
        show();
    }

    private PopupMenu createPopMenu() {

        PopupMenu menu = new PopupMenu();

        for (String s : taskTrayItems) {
            if ("".equals(s)) {
                menu.addSeparator();
            } else {
                MenuItem item = new MenuItem();
                item.setName(s);
                item.setFont(new Font("宋体",Font.PLAIN,14));
                menu.add(item);
                menuItemMap.put(s, item);
            }
        }
        return menu;
    }


    public void setText() {
        for (MenuItem item : menuItemMap.values()) {
            item.setLabel(message.getMessage("tray." + item.getName()));
        }
        tray.setToolTip(message.getMessage("title"));
    }

    public void show() {
        boolean show = mainFrame.isVisible();
        menuItemMap.get("show").setEnabled(show);
        menuItemMap.get("hide").setEnabled(!show);
        mainFrame.setVisible(!show);
    }

    private void initEvents() {
        ActionListener listener = (e) -> {
            if (e.getSource() instanceof MenuItem) {
                MenuItem item = (MenuItem) e.getSource();
                if (item.isEnabled()) {
                    switch (item.getName()) {
                        case "exit":
                            dialog.exit();
                            break;
                        case "show":
                        case "hide":
                            show();
                            break;
                    }
                }
            } else if (e.getSource() instanceof TrayIcon) {
                show();
            }
        };
        for (String s : menuItemMap.keySet()) {
            menuItemMap.get(s).addActionListener(listener);
        }
        tray.addActionListener(listener);
        tray.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                show();
            }
        });
    }


    private TrayIcon tray;
    private Map<String, MenuItem> menuItemMap;
    @Resource
    private Message message;
    @Resource
    private Dialog dialog;
    @Resource
    private JFrame mainFrame;
    @Resource(name = "taskTray.items")
    private java.util.List<String> taskTrayItems;
    private final static Logger logger = LoggerFactory.getLogger(TaskTray.class);

    public void setTaskTrayItems(List<String> taskTrayItems) {
        this.taskTrayItems = taskTrayItems;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
