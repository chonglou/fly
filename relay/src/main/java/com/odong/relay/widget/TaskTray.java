package com.odong.relay.widget;

import com.odong.relay.util.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午4:15
 */
@Component
public class TaskTray {
    @PostConstruct
    void init() {
        if (!SystemTray.isSupported()) {
            logger.error("不支持系统托盘");
            return;
        }

        Image img = guiHelper.getIconImage();

        items = new HashMap<>();
        PopupMenu menu = new PopupMenu();

        for (String s : new String[]{"win", "exit"}) {
            MenuItem item = new MenuItem();
            menu.add(item);
            items.put(s, item);
        }
        icon = new TrayIcon(img, "", menu);
        icon.setImageAutoSize(true);
        initEvents();
        try {
            SystemTray.getSystemTray().add(icon);
        } catch (AWTException e) {
            logger.error("添加托盘图标失败", e);
        }
    }

    public void setText() {
        icon.setToolTip(guiHelper.getMessage("title"));
        items.get("exit").setLabel(guiHelper.getMessage("trayItem.exit"));
        items.get("win").setLabel(guiHelper.getMessage("trayItem.hide"));
        lblShow = guiHelper.getMessage("trayItem.show");
        lblHide = guiHelper.getMessage("trayItem.hide");
    }

    public void show() {
        boolean show = guiHelper.getWindow().isVisible();
        guiHelper.show(!show);
        items.get("win").setLabel(show ? lblShow : lblHide);
    }

    private void initEvents() {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == items.get("exit")) {
                    guiHelper.showExitDialog();
                } else if (e.getSource() == items.get("win")) {
                    show();
                }
            }
        };
        for (String s : items.keySet()) {
            items.get(s).addActionListener(listener);
        }
        icon.addActionListener(listener);
        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                show();
            }
        });
    }


    private TrayIcon icon;
    private Map<String, MenuItem> items;
    private String lblShow;
    private String lblHide;
    @Resource
    private GuiHelper guiHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskTray.class);

    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }
}
