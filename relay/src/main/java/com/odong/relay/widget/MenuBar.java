package com.odong.relay.widget;


import com.odong.relay.util.LabelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        items = new HashMap<>();

        JMenu file = new JMenu();
        menuBar.add(file);
        menus.put("file", file);

        addMenu("file", "open", "close", "exit");
        addRadioMenu("lang", "en_US", "zh_CN");
        addMenu("help", "doc", "aboutMe");

        for (String s : items.keySet()) {
            bindEvent(s);
        }

    }


    public void setLocale(Locale locale) {
        for (String s : menus.keySet()) {
            menus.get(s).setText(labelHelper.getMessage("menu." + s, locale));
        }

        for (String s : items.keySet()) {
            items.get(s).setText(labelHelper.getMessage("menuItem." + s, locale));
        }
        //logger.debug(locale.toString());
        items.get(locale.toString()).setSelected(true);
    }

    private void addRadioMenu(String menu, String... items) {
        JMenu m = new JMenu();
        menuBar.add(m);
        menus.put(menu, m);

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; ; ) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem();
            m.add(item);
            group.add(item);
            this.items.put(items[i], item);
            i++;
            if (i == items.length) {
                break;
            }
            m.addSeparator();

        }
    }

    private void addMenu(String menu, String... items) {
        JMenu m = new JMenu();
        menuBar.add(m);
        menus.put(menu, m);

        for (int i = 0; ; ) {
            JMenuItem item = new JMenuItem();
            m.add(item);
            this.items.put(items[i], item);
            i++;
            if (i == items.length) {
                break;
            }
            m.addSeparator();

        }
    }

    private void bindEvent(String s) {
        items.get(s).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (String s : items.keySet()) {
                    JMenuItem item = items.get(s);
                    if (item.equals(e.getSource())) {
                        switch (s) {
                            case "exit":
                                exitDialog.show();
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
                        break;
                    }
                }
            }
        });
    }

    private JMenuBar menuBar;
    private Map<String, JMenu> menus;
    private Map<String, JMenuItem> items;
    @Resource
    private LabelHelper labelHelper;
    private final static Logger logger = LoggerFactory.getLogger(MenuBar.class);
    @Resource
    private Window window;
    @Resource
    private ExitDialog exitDialog;

    public void setExitDialog(ExitDialog exitDialog) {
        this.exitDialog = exitDialog;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public JMenuBar get() {
        return menuBar;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
