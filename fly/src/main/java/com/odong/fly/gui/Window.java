package com.odong.fly.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
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
 * Date: 13-8-25
 * Time: 下午12:12
 */
@Component("gui.window")
public class Window  {

    public void setLocale(Locale locale){
        mainFrame.setTitle(message.getMessage("title"));
        message.setLocale(locale);

        ctx.getBean(ToolBar.class).setText();
        ctx.getBean(MenuBar.class).setText();
        ctx.getBean(MainPanel.class).setText();

        serialDialog.setText();
        taskTray.setText();
    }

    @PostConstruct
    void init(){
        bootingBar.next();
        initFrame();
        initStyle();
        initEvent();
        setLocale(Locale.getDefault());

        mainFrame.setVisible(true);
        bootingBar.dispose();
    }

    private void initFrame() {
        mainFrame.setLayout(new BorderLayout());

        Container container = mainFrame.getContentPane();
        container.add(toolBar, BorderLayout.PAGE_START);
        container.add(mainPanel, BorderLayout.CENTER);

        mainFrame.setJMenuBar(menuBar);
        mainFrame.setIconImage(message.getIcon());
    }


    private void initStyle() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setLocation(d.width / 4, d.height / 4);
        mainFrame.setSize(800, 600);
    }


    private void initEvent() {
        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                taskTray.show();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                dialog.exit();
            }
        });
    }

    @Resource(name = "toolBar")
    private JToolBar toolBar;
    @Resource(name = "menuBar")
    private JMenuBar menuBar;
    @Resource
    private BootingBar bootingBar;
    @Resource
    private JFrame mainFrame;
    @Resource(name = "mainPanel")
    private JPanel mainPanel;
    @Resource
    private Message message;
    @Resource(name = "gui.dialog")
    private Dialog dialog;
    @Resource(name = "gui.taskTray")
    private TaskTray taskTray;
    @Resource
    private ApplicationContext ctx;
    @Resource
    private SerialDialog serialDialog;
    private final static Logger logger = LoggerFactory.getLogger(Window.class);

    public void setSerialDialog(SerialDialog serialDialog) {
        this.serialDialog = serialDialog;
    }

    public void setToolBar(JToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public void setBootingBar(BootingBar bootingBar) {
        this.bootingBar = bootingBar;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void setTaskTray(TaskTray taskTray) {
        this.taskTray = taskTray;
    }

    public void setCtx(ApplicationContext ctx) {
        this.ctx = ctx;
    }
}
