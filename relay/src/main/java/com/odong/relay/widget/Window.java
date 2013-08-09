package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
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

        initLookAndFeel();

        frame = new JFrame();
        frame.setLayout(new BorderLayout());

        Container container = frame.getContentPane();
        container.add(toolBar.get(), BorderLayout.PAGE_START);
        container.add(cardPanel.get(), BorderLayout.CENTER);

        frame.setJMenuBar(menuBar.get());
        frame.setIconImage(labelHelper.getIconImage());

        initEvent();
        setLocale(Locale.SIMPLIFIED_CHINESE);
        show(true);

    }


    public void setLocale(Locale locale) {
        frame.setTitle(labelHelper.getMessage("title", locale));
        cardPanel.setLocale(locale);
        toolBar.setLocale(locale);
        menuBar.setLocale(locale);
        exitDialog.setLocale(locale);
        messageDialog.setLocale(locale);
        serialDialog.setLocale(locale);
        taskTray.setLocale(locale);
    }

    public void show(boolean visible) {
        if (visible) {
            //frame.setLocationRelativeTo( null);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            //frame.setSize(d.width / 2, d.height / 2);
            frame.setSize(800, 600);
            frame.setLocation(d.width / 4, d.height / 4);

            //frame.pack();

        }
        frame.setVisible(visible);
    }

    public JFrame get() {
        return frame;
    }

    private void initLookAndFeel() {


        // JFrame.setDefaultLookAndFeelDecorated(true);
        //  JDialog.setDefaultLookAndFeelDecorated(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("加载系统风格失败", e);
        }
        /*
        Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource)

                UIManager.put(key, f);

        }

        */

    }


    private void initEvent() {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                taskTray.show();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                exitDialog.show();
            }
        });
    }


    private JFrame frame;

    private final static Logger logger = LoggerFactory.getLogger(Window.class);
    @Resource
    private MessageDialog messageDialog;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private ToolBar toolBar;
    @Resource
    private CardPanel cardPanel;
    @Resource
    private MenuBar menuBar;
    @Resource
    private ExitDialog exitDialog;
    @Resource
    private SerialDialog serialDialog;
    @Resource
    private TaskTray taskTray;

    public void setTaskTray(TaskTray taskTray) {
        this.taskTray = taskTray;
    }

    public void setSerialDialog(SerialDialog serialDialog) {
        this.serialDialog = serialDialog;
    }

    public void setMessageDialog(MessageDialog messageDialog) {
        this.messageDialog = messageDialog;
    }

    public void setExitDialog(ExitDialog exitDialog) {
        this.exitDialog = exitDialog;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public void setCardPanel(CardPanel cardPanel) {
        this.cardPanel = cardPanel;
    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
