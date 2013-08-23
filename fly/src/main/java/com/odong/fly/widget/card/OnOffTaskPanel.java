package com.odong.fly.widget.card;

import com.odong.fly.MyException;
import com.odong.fly.model.Task;
import com.odong.fly.model.item.SerialItem;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.service.StoreHelper;
import com.odong.fly.util.GuiHelper;
import com.odong.fly.widget.DateTimePanel;
import com.odong.fly.widget.ToolBar;
import com.odong.fly.widget.impl.SimpleDateTimePanelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 下午12:46
 */
@Component
public class OnOffTaskPanel extends TaskPanel {
    public OnOffTaskPanel() {
        super();
    }

    public void show(String portName) {


        Date now = new Date();
        this.show(guiHelper.getMessage("channel.task.title") + portName,
                null, portName, null,
                now, new Date(now.getTime() + 1000 * 60 * 60 * 24),
                0, 3, 3
        );

        setStart(false);
        channelCB.setEnabled(true);
        buttons.get("delete").setVisible(false);
    }

    @Override
    public void show(Task task) {
        OnOffRequest r = (OnOffRequest) task.getRequest();
        this.show(guiHelper.getMessage("channel.task.title") + task.toString(),
                task.getId(), r.getPortName(), r.getChannel(),
                task.getBegin(), task.getEnd(), task.getTotal(), r.getOnSpace(), r.getOffSpace()
        );

        channelCB.setEnabled(false);
        setStart(task.getState() == Task.State.SUBMIT || task.getState() == Task.State.PROCESSING);
        buttons.get("delete").setVisible(true);
    }


    private void setStart(boolean start) {
        buttons.get("stop").setEnabled(start);

        buttons.get("on").setEnabled(!start);
        buttons.get("off").setEnabled(!start);
        buttons.get("start").setEnabled(!start);
        buttons.get("delete").setEnabled(!start);

        beginTime.setEnable(!start);
        endTime.setEnable(!start);
        onSpace.setEnabled(!start);
        offSpace.setEnabled(!start);
        total.setEnabled(!start);
    }

    private void show(String title, String taskId, String portName, Integer channel, Date begin, Date end, long total, int onSpace, int offSpace) {
        this.taskId = taskId;
        this.portName = portName;

        if (channel != null) {
            this.channelCB.setSelectedItem(channel);
        }

        this.title.setText("<html><h1>" + title + "</h1></html>");
        this.beginTime.setDate(begin);
        this.endTime.setDate(end);
        this.onSpace.setText(Integer.toString(onSpace));
        this.offSpace.setText(Integer.toString(offSpace));
        this.total.setText(Long.toString(total));
    }


    @PostConstruct
    void init() {
        setStart(false);
    }

    @Override
    public String name() {
        return Task.Type.ON_OFF.name();
    }

    @Override
    public void setText() {
        for (String s : labels.keySet()) {
            labels.get(s).setText(guiHelper.getMessage("channel.task." + s) + "：");
        }

        Map<String, String> map = new HashMap<>();
        for (String s : new String[]{"year", "month", "day", "hour", "minute", "second"}) {
            map.put(s, guiHelper.getMessage("dateTimeP." + s));
        }
        beginTime.setText(map);
        endTime.setText(map);

        for (String s : buttons.keySet()) {
            buttons.get(s).setText(guiHelper.getMessage("button." + s));
        }

    }


    private void refreshLogList() {
        logModel.removeAllElements();
        if (taskId != null) {
            for (SerialItem l : storeHelper.listSerialItem(taskId)) {
                logModel.addElement(l.getCreated().toString() + ":" + l.getRequest() + "," + l.getResponse());
            }
        }
    }

    @Override
    protected void initEvents() {
        MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                switch (btn.getName()) {
                    case "btn-on":
                        break;
                    case "btn-off":
                        break;
                    case "btn-start":
                        try {
                            int ch = (Integer) channelCB.getSelectedItem();
                            Date begin = beginTime.getDate();
                            Date end = endTime.getDate();
                            String totalS = total.getText();
                            long t = "".equals(totalS) ? 0 : Long.parseLong(totalS);
                            int onS = Integer.parseInt(onSpace.getText());
                            int offS = Integer.parseInt(offSpace.getText());

                            if (begin.compareTo(end) >= 0 || onS <= 0 || offS <= 0 || t < 0) {
                                throw new IllegalArgumentException();
                            }
                            if (taskId == null) {
                                if (storeHelper.getAvailSerialTask(portName, ch) != null) {
                                    guiHelper.showErrorDialog(MyException.Type.SERIAL_CHANNEL_IN_USE);
                                    return;
                                }

                                String tid = UUID.randomUUID().toString();
                                storeHelper.addOnOffTask(tid, portName, ch, begin, end, t, onS, offS);
                                taskId = tid;

                            } else {
                                storeHelper.setOnOffTaskInfo(taskId, begin, end, t, onS, offS);
                                storeHelper.setTaskState(taskId, Task.State.SUBMIT);
                            }
                        } catch (Exception ex) {
                            logger.error("数据输入出错", ex);
                            guiHelper.showErrorDialog("inputNonValid");
                        }
                        if (taskId != null) {
                            show(storeHelper.getTask(taskId));
                        }
                        toolBar.refresh();
                        break;
                    case "btn-stop":
                        storeHelper.setTaskState(taskId, Task.State.DONE);
                        setStart(false);
                        toolBar.refresh();
                        break;
                    case "btn-delete":
                        storeHelper.setTaskState(taskId, Task.State.DELETE);
                        show(portName);
                        toolBar.refresh();
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

        /*
        channelCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int ch = (Integer) ((JComboBox) e.getSource()).getSelectedItem();
                    setStart(storeHelper.getAvailSerialTask(portName, ch) = null);
                }
            }
        });
        */

    }


    @Override
    protected void initPanel() {
        labels = new HashMap<>();
        buttons = new HashMap<>();

        endTime = new SimpleDateTimePanelImpl();
        beginTime = new SimpleDateTimePanelImpl();

        channelCB = new JComboBox<>();
        for (int i = 1; i <= 32; i++) {
            channelCB.addItem(i);
        }

        total = new JTextField();
        onSpace = new JTextField();
        offSpace = new JTextField();
        logModel = new DefaultListModel<>();


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 20, 20, 20);
        c.weightx = 0.3;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;

        JLabel lbl;
        JPanel p;
        JButton btn;


        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        panel.add(title, c);


        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.gridheight = 1;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("channel", lbl);
        c.gridx++;
        panel.add(channelCB, c);

        c.gridx = 0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        c.gridx++;
        labels.put("beginTime", lbl);
        panel.add(beginTime.get(), c);


        c.gridx = 0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("endTime", lbl);
        c.gridx++;
        panel.add(endTime.get(), c);

        c.gridx = 0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("onSpace", lbl);
        c.gridx++;
        panel.add(onSpace, c);

        c.gridx = 0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("offSpace", lbl);
        c.gridx++;
        panel.add(offSpace, c);

        c.gridx = 0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("total", lbl);
        c.gridx++;
        panel.add(total, c);

        c.gridx = 0;
        c.gridy++;
        p = new JPanel(new FlowLayout());
        for (String s : new String[]{"start", "stop", "delete", "on", "off", "refresh"}) {
            btn = new JButton();
            btn.setName("btn-" + s);
            p.add(btn);
            buttons.put(s, btn);
        }
        c.gridheight = 1;
        c.gridwidth = 2;
        panel.add(p, c);


        c.gridheight = 8;
        c.gridwidth = 2;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        p = new JPanel(new BorderLayout());
        lbl = new JLabel();
        p.add(lbl, BorderLayout.PAGE_START);
        labels.put("logList", lbl);
        JList logList = new JList<>(logModel);
        JScrollPane jp = new JScrollPane(logList);
        p.add(jp, BorderLayout.CENTER);
        panel.add(p, c);
        c.weightx = 0.3;
    }


    private Map<String, JLabel> labels;
    private Map<String, JButton> buttons;
    private DateTimePanel beginTime;
    private DateTimePanel endTime;
    private JTextField onSpace;
    private JTextField offSpace;
    private JTextField total;
    private JComboBox<Integer> channelCB;
    private DefaultListModel<String> logModel;
    private JLabel title;
    private String portName;
    private String taskId;
    @Resource
    private GuiHelper guiHelper;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private ToolBar toolBar;


    private final static Logger logger = LoggerFactory.getLogger(OnOffTaskPanel.class);

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }


    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}
