package com.odong.fly.widget.card;

import com.odong.fly.job.Task;
import com.odong.fly.job.TaskJob;
import com.odong.fly.model.Item;
import com.odong.fly.serial.SerialPort;
import com.odong.fly.util.GuiHelper;
import com.odong.fly.util.StoreHelper;
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
import java.awt.event.*;
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

    @Override
    public void show(String portName, int channel) {
        Task t = taskJob.getTask(portName, channel);
        if (t == null) {
            Date now = new Date();
            this.show(guiHelper.getMessage("channel.task.title") + portName,
                    UUID.randomUUID().toString(), portName, channel,
                    now, new Date(now.getTime() + 1000 * 60 * 60 * 24), 3, 3,
                    0);
        } else {
            show(t);
        }
    }

    @Override
    public void show(Task task) {
        this.show(guiHelper.getMessage("channel.task.title") + task.toString(),
                task.getId(), task.getPortName(), task.getChannel(),
                task.getBegin(), task.getEnd(), task.getOnSpace(), task.getOffSpace(),
                task.getTotal());
    }


    private void show(String title, String taskId, String portName, int channel, Date begin, Date end, int onSpace, int offSpace, int total) {
        this.taskId = taskId;
        this.portName = portName;

        this.channelCB.setSelectedItem(channel);
        this.title.setText("<html><h1>" + title + "</h1></html>");
        this.beginTime.setDate(begin);
        this.endTime.setDate(end);
        this.onSpace.setText(Integer.toString(onSpace));
        this.offSpace.setText(Integer.toString(offSpace));
        this.total.setText(total == 0 ? null : Integer.toString(total));
    }


    @PostConstruct
    void init() {
        setButtonOn(false);
    }

    @Override
    public String name() {
        return SerialPort.Type.ON_OFF.name();
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


    private void setOn(boolean on) {
        if (on) {
            try {
                String total = this.total.getText();
                Date begin = beginTime.getDate();
                Date end = endTime.getDate();
                if (begin.compareTo(end) >= 0) {
                    throw new IllegalArgumentException("起始时间必须小于结束时间");
                }
                taskJob.putTask(portName, (Integer) channelCB.getSelectedItem(), beginTime.getDate(), endTime.getDate(),
                        Integer.parseInt(onSpace.getText()),
                        Integer.parseInt(offSpace.getText()),
                        "".equals(total) ? 0 : Integer.parseInt(total));
            } catch (Exception e) {
                logger.error("添加任务出错", e);
                guiHelper.showErrorDialog("inputNonValid");
                return;
            }

        } else {
            taskJob.popTask(taskId);
        }

        setButtonOn(on);
        toolBar.refresh();
    }

    private void setButtonOn(boolean on) {

        buttons.get("on").setEnabled(!on);
        buttons.get("off").setEnabled(on);
    }

    private void refreshLogList() {
        logModel.removeAllElements();
        for (Item l : storeHelper.listItem(taskId)) {
            logModel.addElement(l.toString());
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

        channelCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int ch = (Integer) ((JComboBox) e.getSource()).getSelectedItem();
                    Task t = taskJob.getTask(portName, ch);
                    setButtonOn(t != null);
                }
            }
        });

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
    private TaskJob taskJob;
    @Resource
    private ToolBar toolBar;


    private final static Logger logger = LoggerFactory.getLogger(OnOffTaskPanel.class);

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void setTaskJob(TaskJob taskJob) {
        this.taskJob = taskJob;
    }

    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}
