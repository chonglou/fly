package com.odong.relay.widget;

import com.odong.relay.model.Log;
import com.odong.relay.util.LabelHelper;
import com.odong.relay.util.LogService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午7:29
 */
public class ChannelPanel {

    public ChannelPanel(String name, int port, Locale locale, ToolBar toolBar, LabelHelper labelHelper, LogService logService) {
        this.port = port;
        this.toolBar = toolBar;
        this.labelHelper = labelHelper;
        this.logService = logService;

        panel = new JPanel(new GridBagLayout());
        panel.setName(name);

        initTaskPanel();
        initEvents();

        setLocale(locale);

        //setOn(false);
    }

    public void setLocale(Locale locale) {
        for (String s : labels.keySet()) {
            labels.get(s).setText(labelHelper.getMessage("channel.task." + s, locale) + "：");
        }
        beginTime.setLocale(locale);
        endTime.setLocale(locale);

        for (String s : buttons.keySet()) {
            buttons.get(s).setText(labelHelper.getMessage("button." + s, locale));
        }
        title.setText(labelHelper.getMessage("channel.task.title", locale) + port);
    }


    public void refreshLogList() {
        logModel.removeAllElements();
        for (Log l : logService.list(port, 100)) {
            logModel.addElement(l.toString());
        }
    }

    public void setOn(boolean on) {
        if(on){
             //TODO 启动
        }
        else {
            //TODO 停止
        }
        this.on = on;
        buttons.get("on").setEnabled(!on);
        buttons.get("off").setEnabled(on);
        toolBar.setOn(port, on);
    }

    private void initEvents() {
        MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                switch (btn.getName()) {
                    case "btn-on":
                        setOn(true);
                        break;
                    case "btn-off":
                        setOn(false);
                        break;
                    case "btn-refresh":
                        refreshLogList();
                        break;
                }
            }
        };
        for (String s : buttons.keySet()) {
            buttons.get(s).addMouseListener(listener);
        }
    }


    private void initTaskPanel() {
        labels = new HashMap<>();
        buttons = new HashMap<>();

        endTime = new DateTimePanel(labelHelper);
        beginTime = new DateTimePanel(labelHelper);
        times = new JTextField("0");
        upSpace = new JTextField("3");
        downSpace = new JTextField("3");
        logModel = new DefaultListModel<>();


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 20, 20, 20);
        c.weightx = 0.3;
        c.weighty = 1.0;

        JLabel lbl;
        JPanel p;
        JButton btn;


        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0;
        c.gridy = 1;
        panel.add(lbl, c);
        labels.put("beginTime", lbl);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(beginTime.get(), c);


        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0;
        c.gridy = 2;
        panel.add(lbl, c);
        labels.put("endTime", lbl);
        c.gridx = 1;
        c.gridy = 2;
        panel.add(endTime.get(), c);

        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0;
        c.gridy = 3;
        panel.add(lbl, c);
        labels.put("onSpace", lbl);
        c.gridx = 1;
        c.gridy = 3;
        panel.add(upSpace, c);

        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0;
        c.gridy = 4;
        panel.add(lbl, c);
        labels.put("offSpace", lbl);
        c.gridx = 1;
        c.gridy = 4;
        panel.add(downSpace, c);

        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0;
        c.gridy = 5;
        panel.add(lbl, c);
        labels.put("times", lbl);
        c.gridx = 1;
        c.gridy = 5;
        panel.add(times, c);

        p = new JPanel(new BorderLayout());
        lbl = new JLabel();
        p.add(lbl, BorderLayout.PAGE_START);
        labels.put("logList", lbl);
        JList logList = new JList<>(logModel);
        JScrollPane jp = new JScrollPane(logList);
        p.add(jp, BorderLayout.CENTER);
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 6;
        c.gridwidth = 2;
        c.weightx = 1.0;
        panel.add(p, c);
        c.weightx = 0.3;

        p = new JPanel(new FlowLayout());
        btn = new JButton();
        btn.setName("btn-on");
        p.add(btn);
        buttons.put("on", btn);
        btn = new JButton();
        btn.setName("btn-off");
        p.add(btn);
        buttons.put("off", btn);
        btn = new JButton();
        btn.setName("btn-refresh");
        p.add(btn);
        buttons.put("refresh", btn);
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        panel.add(p, c);

        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 3;
        panel.add(title, c);
    }


    private int port;
    private Map<String, JLabel> labels;
    private Map<String, JButton> buttons;
    private DateTimePanel beginTime;
    private DateTimePanel endTime;
    private JPanel panel;
    private JTextField upSpace;
    private JTextField downSpace;
    private JTextField times;
    private DefaultListModel<String> logModel;
    private JLabel title;
    private ToolBar toolBar;
    private LabelHelper labelHelper;
    private LogService logService;
    private boolean on;

    public boolean isOn() {
        return on;
    }

    public JPanel get() {
        return panel;
    }
}
