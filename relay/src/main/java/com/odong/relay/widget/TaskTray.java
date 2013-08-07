package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
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
import java.util.Locale;
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

        Image img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tray.png"));

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

    public void setLocale(Locale locale) {
        icon.setToolTip(labelHelper.getMessage("title", locale));
        items.get("exit").setLabel(labelHelper.getMessage("trayItem.exit", locale));
        items.get("win").setLabel(labelHelper.getMessage("trayItem.hide", locale));
        lblShow = labelHelper.getMessage("trayItem.show", locale);
        lblHide = labelHelper.getMessage("trayItem.hide", locale);
    }

    public void show() {
        boolean show = window.get().isVisible();
        window.show(!show);
        items.get("win").setLabel(show ? lblShow : lblHide);
    }

    private void initEvents() {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == items.get("exit")) {
                    exitDialog.show();
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
    private LabelHelper labelHelper;
    @Resource
    private ExitDialog exitDialog;
    @Resource
    private Window window;
    private final static Logger logger = LoggerFactory.getLogger(TaskTray.class);

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setExitDialog(ExitDialog exitDialog) {
        this.exitDialog = exitDialog;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
