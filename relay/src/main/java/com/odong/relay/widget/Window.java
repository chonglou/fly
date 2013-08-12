package com.odong.relay.widget;

import com.odong.relay.util.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午10:49
 */
@Component
public class Window {
    @PostConstruct
    void init() {
        JFrame frame = guiHelper.getWindow();
        frame.setLayout(new BorderLayout());

        Container container = frame.getContentPane();
        container.add(toolBar.get(), BorderLayout.PAGE_START);
        container.add(cardPanel.get(), BorderLayout.CENTER);

        frame.setJMenuBar(menuBar.get());
        frame.setIconImage(guiHelper.getIconImage());

        initStyle();
        initEvent();
        setLocale(Locale.SIMPLIFIED_CHINESE);

        guiHelper.show(true);
        cardPanel.showHelp();
    }


    public void setLocale(Locale locale) {
        guiHelper.setLocale(locale);
        toolBar.setText();
        menuBar.setText();
        serialDialog.setText();
        taskTray.setText();
        cardPanel.setText();
    }

    private void initStyle() {
        JFrame frame = guiHelper.getWindow();
        /*
        //菜单栏bug
        frame.setLocationByPlatform(true);
         */
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width / 4, d.height / 4);
        frame.setSize(800, 600);
        //frame.pack();
    }


    private void initEvent() {
        JFrame frame = guiHelper.getWindow();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                taskTray.show();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                guiHelper.showExitDialog();
            }
        });
    }


    @Resource
    private GuiHelper guiHelper;
    @Resource
    private ToolBar toolBar;
    @Resource
    private CardPanel cardPanel;
    @Resource
    private MenuBar menuBar;
    @Resource
    private SerialDialog serialDialog;
    @Resource
    private TaskTray taskTray;
    private final static Logger logger = LoggerFactory.getLogger(Window.class);


    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setCardPanel(CardPanel cardPanel) {
        this.cardPanel = cardPanel;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public void setSerialDialog(SerialDialog serialDialog) {
        this.serialDialog = serialDialog;
    }

    public void setTaskTray(TaskTray taskTray) {
        this.taskTray = taskTray;
    }
}
