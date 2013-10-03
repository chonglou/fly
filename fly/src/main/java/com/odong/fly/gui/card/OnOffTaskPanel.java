package com.odong.fly.gui.card;

import com.odong.fly.MyException;
import com.odong.fly.gui.Dialog;
import com.odong.fly.gui.Message;
import com.odong.fly.gui.ToolBar;
import com.odong.fly.model.Task;
import com.odong.fly.model.item.SerialItem;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.serial.Command;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        this.show(message.getMessage("channel.task.title") + portName,
                null, portName, null,
                now, new Date(now.getTime() + 1000 * 60 * 60 * 24),
                0, 3, 3
        );

        setStart(false);
        channelCB.setEnabled(true);
        buttons.get("delete").setVisible(false);

        setTaskMode(false);
        logModel.clear();
    }

    @Override
    public void show(Task task) {
        OnOffRequest r = (OnOffRequest) task.getRequest();
        this.show(message.getMessage("channel.task.title") + task.toString(),
                task.getId(), r.getPortName(), r.getChannel(),
                task.getBegin(), task.getEnd(), task.getTotal(), r.getOnSpace(), r.getOffSpace()
        );

        channelCB.setEnabled(false);
        setStart(task.getState() == Task.State.SUBMIT);
        buttons.get("delete").setVisible(true);

        setTaskMode(true);
        refreshLogList();
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
            labels.get(s).setText(message.getMessage("channel.task." + s) + "：");
        }

        /*
        Map<String, String> map = new HashMap<>();
        for (String s : new String[]{"year", "month", "day", "hour", "minute", "second"}) {
            map.put(s, message.getMessage("dateTimeP." + s));
        }
        beginTime.setText(map);
        endTime.setText(map);
        */

        for (String s : buttons.keySet()) {
            buttons.get(s).setText(message.getMessage("button." + s));
        }

    }


    private void setTaskMode(boolean taskMode) {
        buttons.get("new").setVisible(!taskMode);
        buttons.get("on").setVisible(!taskMode);
        buttons.get("off").setVisible(!taskMode);

        buttons.get("start").setVisible(taskMode);
        buttons.get("stop").setVisible(taskMode);
        buttons.get("delete").setVisible(taskMode);
        buttons.get("refresh").setVisible(taskMode);
    }

    private void setStart(boolean start) {
        buttons.get("stop").setEnabled(start);

        buttons.get("start").setEnabled(!start);
        buttons.get("delete").setEnabled(!start);

        beginTime.setEnabled(!start);
        endTime.setEnabled(!start);
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
        this.beginTime.setValue(begin);
        this.endTime.setValue(end);
        this.onSpace.setText(Integer.toString(onSpace));
        this.offSpace.setText(Integer.toString(offSpace));
        this.total.setText(Long.toString(total));
    }


    private void refreshLogList() {
        logModel.removeAllElements();
        if (taskId != null) {
            for (SerialItem l : storeHelper.listSerialItem(taskId, logSize)) {
                logModel.addElement(dtFormat.format(l.getCreated()) + ":" + l.getRequest() + "," + l.getResponse());
            }
        }
    }

    private synchronized void send(int channel, boolean on) {
        Task task = storeHelper.getAvailSerialTask(portName, channel);
        if (task == null || task.getState() != Task.State.SUBMIT) {
            if (serialUtil.isOpen(portName)) {
                try {
                    String request = Command.onOff(channel, on);
                    addLog("请求" + request);
                    addLog("返回" + serialUtil.send(portName, request));
                } catch (MyException e) {
                    addLog("IO出错" + e.getType());
                }
            } else {
                addLog("端口未打开");
            }
        } else {
            addLog("通道" + channel + "被占用");
        }
    }

    private void addLog(String msg) {
        logModel.add(0, dtFormat.format(new Date()) + "：" + msg);
    }

    @Override
    protected void initEvents() {
        MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                int ch = (Integer) channelCB.getSelectedItem();
                switch (btn.getName()) {
                    case "btn-on":
                        send(ch, true);
                        break;
                    case "btn-off":
                        send(ch, false);
                        break;
                    case "btn-new":
                    case "btn-start":
                        try {
                            Date begin = (Date) beginTime.getValue();
                            Date end = (Date) endTime.getValue();
                            String totalS = total.getText();
                            long t = "".equals(totalS) ? 0 : Long.parseLong(totalS);
                            int onS = Integer.parseInt(onSpace.getText());
                            int offS = Integer.parseInt(offSpace.getText());

                            if (begin.compareTo(end) >= 0 ||
                                    end.compareTo(new Date()) <= 0 ||
                                    onS <= 0 ||
                                    offS <= 0 ||
                                    t < 0) {
                                //logger.error("启动时间：[{}] \t 截止时间[{}]",begin,end);
                                throw new IllegalArgumentException("输入有误");
                            }
                            if (taskId == null) {
                                if (storeHelper.getAvailSerialTask(portName, ch) != null) {
                                    dialog.error(MyException.Type.SERIAL_CHANNEL_IN_USE);
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
                            dialog.error("inputNonValid");
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

    private JSpinner crateDateTimeField() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd hh:mm:ss");
        spinner.setEditor(editor);
        return spinner;
    }

    @Override
    protected void initPanel() {
        labels = new HashMap<>();
        buttons = new HashMap<>();

        endTime = crateDateTimeField();
        beginTime = crateDateTimeField();

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
        c.insets = new Insets(5, 20, 5, 20);
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
        panel.add(beginTime, c);


        c.gridx = 0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("endTime", lbl);
        c.gridx++;
        panel.add(endTime, c);

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
        for (String s : new String[]{"new", "start", "stop", "delete", "on", "off", "refresh"}) {
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
    private JSpinner beginTime;
    private JSpinner endTime;
    private JTextField onSpace;
    private JTextField offSpace;
    private JTextField total;
    private JComboBox<Integer> channelCB;
    private DefaultListModel<String> logModel;
    private JLabel title;
    private String portName;
    private String taskId;
    @Resource
    private Message message;
    @Resource
    private StoreHelper storeHelper;
    @Resource(name = "gui.toolBar")
    private ToolBar toolBar;
    @Resource
    private Dialog dialog;
    @Resource
    private SerialUtil serialUtil;
    @Value("${log.size}")
    private int logSize;
    private final DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final static Logger logger = LoggerFactory.getLogger(OnOffTaskPanel.class);

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

    public void setLogSize(int logSize) {
        this.logSize = logSize;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}
