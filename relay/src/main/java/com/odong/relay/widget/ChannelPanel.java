package com.odong.relay.widget;

import com.odong.relay.job.Task;
import com.odong.relay.job.TaskJob;
import com.odong.relay.model.Log;
import com.odong.relay.util.GuiHelper;
import com.odong.relay.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public ChannelPanel(String name, int port,
                        Locale locale,
                        ToolBar toolBar,
                        GuiHelper labelHelper,
                        StoreHelper logService,
                        MessageDialog messageDialog,
                        TaskJob taskJob) {
        this.port = port;
        this.toolBar = toolBar;
        this.labelHelper = labelHelper;
        this.logService = logService;
        this.messageDialog = messageDialog;
        this.taskJob = taskJob;

        panel = new JPanel(new GridBagLayout());
        panel.setName(name);

        initTaskPanel();
        initEvents();

        setLocale(locale);

        buttons.get("on").setEnabled(true);
        buttons.get("off").setEnabled(false);
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

        title.setText("<html><h1>" + labelHelper.getMessage("channel.task.title", locale) + port + "</h1></html>");
    }


    public void refreshLogList() {
        logModel.removeAllElements();
        for (Log l : logService.list(port, 100)) {
            logModel.addElement(l.toString());
        }
    }

    public void setOn(boolean on) {
        if (on) {
            try {
                int total = Integer.parseInt(this.total.getText());
                taskJob.putOnOffTask(port, beginTime.toDate(), endTime.toDate(),
                        Integer.parseInt(onSpace.getText()),
                        Integer.parseInt(offSpace.getText()),
                        total == 0 ? null : total);
            } catch (Exception e) {
                messageDialog.error("inputNonValid");
                logger.error("添加任务出错", e);
                return;
            }
        } else {
            taskJob.popOnOffTask(port);
        }

        buttons.get("on").setEnabled(!on);
        buttons.get("off").setEnabled(on);
        toolBar.setOn(port, on);
    }

    public boolean isOn() {
        return taskJob.getTaskName(port, Task.Type.ON_OFF) == null;
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
        total = new JTextField("0");
        onSpace = new JTextField("3");
        offSpace = new JTextField("3");
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
        panel.add(onSpace, c);

        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0;
        c.gridy = 4;
        panel.add(lbl, c);
        labels.put("offSpace", lbl);
        c.gridx = 1;
        c.gridy = 4;
        panel.add(offSpace, c);

        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0;
        c.gridy = 5;
        panel.add(lbl, c);
        labels.put("total", lbl);
        c.gridx = 1;
        c.gridy = 5;
        panel.add(total, c);

        p = new JPanel(new BorderLayout());
        lbl = new JLabel();
        p.add(lbl, BorderLayout.PAGE_START);
        labels.put("logList", lbl);
        JList logList = new JList<>(logModel);
        JScrollPane jp = new JScrollPane(logList);
        p.add(jp, BorderLayout.CENTER);
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 7;
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
    private JTextField onSpace;
    private JTextField offSpace;
    private JTextField total;
    private DefaultListModel<String> logModel;
    private JLabel title;
    private ToolBar toolBar;

    private GuiHelper labelHelper;
    private StoreHelper logService;
    private TaskJob taskJob;
    private final static Logger logger = LoggerFactory.getLogger(ChannelPanel.class);

    public JPanel get() {
        return panel;
    }


}
