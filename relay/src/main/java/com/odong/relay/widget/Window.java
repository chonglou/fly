package com.odong.relay.widget;

import com.jtattoo.plaf.luna.LunaLookAndFeel;
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
import java.util.Properties;

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
        initStyle();

        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        Container container = frame.getContentPane();


        container.add(toolBar.get(), BorderLayout.NORTH);
        container.add(channel.getPanel(), BorderLayout.CENTER);


        frame.setJMenuBar(menuBar.get());


           initEvent();
        setLocale(Locale.SIMPLIFIED_CHINESE);
        show();
    }



    public void setLocale(Locale locale) {
        frame.setTitle(labelHelper.getMessage("title", locale));
        channel.setLocale(locale);
        toolBar.setLocale(locale);
        menuBar.setLocale(locale);
        exitDlg.setLocale(locale);
    }

    public void show() {
        //frame.setLocationRelativeTo( null);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(d.width / 2, d.height / 2);
        frame.setLocation(d.width / 4, d.height / 4);

        frame.pack();
        frame.setVisible(true);
    }
    public JFrame get(){
        return frame;
    }

    private void initStyle() {
        // mac
        // com.jtattoo.plaf.mcwin.McWinLookAndFeel
        // winxp
        // com.jtattoo.plaf.luna.LunaLookAndFeel
        // aero
        // com.jtattoo.plaf.aero.AeroLookAndFeel

        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            Properties props = new Properties();
            props.put("logoString", "relay");
            LunaLookAndFeel.setCurrentTheme(props);
            UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
        } catch (Exception e) {
            logger.error("未找到风格", e);
        }
    }


    private void initEvent(){
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitDlg.show();
            }
        });
    }


    private JFrame frame;

    private final static Logger logger = LoggerFactory.getLogger(Window.class);
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private ToolBar toolBar;
    @Resource
    private Channel channel;
    @Resource
    private MenuBar menuBar;
    @Resource
    private ExitDialog exitDlg;

    public void setExitDlg(ExitDialog exitDlg) {
        this.exitDlg = exitDlg;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
