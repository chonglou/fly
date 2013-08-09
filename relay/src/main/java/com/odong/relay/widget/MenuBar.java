package com.odong.relay.widget;


import com.odong.relay.job.TaskJob;
import com.odong.relay.util.GuiHelper;
import com.odong.relay.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
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


    public void setText() {
        for (String s : menus.keySet()) {
            menus.get(s).setText(guiHelper.getMessage("menu." + s));
        }

        for (String s : items.keySet()) {
            items.get(s).setText(guiHelper.getMessage("menuItem." + s));
        }
        items.get(guiHelper.getLocale().toString()).setSelected(true);
    }

    public JMenuBar get() {
        return menuBar;
    }

    private void addRadioMenu(String menu, String... items) {
        JMenu m = new JMenu();
        menuBar.add(m);
        menus.put(menu, m);

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; ; ) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem();
            item.setName("item-" + items[i]);
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
            item.setName("item-" + items[i]);
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
                JMenuItem item = (JMenuItem) e.getSource();

                switch (item.getName().split("\\-")[1]) {
                    case "open":
                        //TODO Open
                        break;
                    case "close":
                        //TODO close
                        break;
                    case "doc":
                        guiHelper.showInfoDialog("doc");
                        break;
                    case "aboutMe":
                        guiHelper.showInfoDialog("aboutMe");
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
        });
    }

    private JMenuBar menuBar;
    private Map<String, JMenu> menus;
    private Map<String, JMenuItem> items;
    @Resource
    private GuiHelper guiHelper;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private TaskJob taskJob;
    @Resource
    private Window window;
    private final static Logger logger = LoggerFactory.getLogger(MenuBar.class);

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setTaskJob(TaskJob taskJob) {
        this.taskJob = taskJob;
    }
}
