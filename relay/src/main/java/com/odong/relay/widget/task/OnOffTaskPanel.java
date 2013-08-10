package com.odong.relay.widget.task;

import com.odong.relay.job.Task;
import com.odong.relay.job.TaskJob;
import com.odong.relay.model.Item;
import com.odong.relay.serial.Command;
import com.odong.relay.util.GuiHelper;
import com.odong.relay.util.StoreHelper;
import com.odong.relay.widget.DateTimePanel;
import com.odong.relay.widget.ToolBar;
import com.odong.relay.widget.impl.SimpleDateTimePanelImpl;
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

    @Override
    public void show(String portName) {
        Date now = new Date();
        this.show(guiHelper.getMessage("channel.task.title") + portName,
                UUID.randomUUID().toString(), portName, 1,
                now, new Date(now.getTime() + 1000 * 60 * 60 * 24), 3, 3,
                0);
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

        buttons.get("on").setEnabled(true);
        buttons.get("off").setEnabled(false);
    }

    @Override
    public String name() {
        return Command.Type.ON_OFF.name();
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
                taskJob.putTask(portName, (Integer)channelCB.getSelectedItem(), beginTime.getDate(), endTime.getDate(),
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

        buttons.get("on").setEnabled(!on);
        buttons.get("off").setEnabled(on);

        toolBar.refresh();
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

    }

    @Override
    protected void initPanel() {
        labels = new HashMap<>();
        buttons = new HashMap<>();

        endTime = new SimpleDateTimePanelImpl();
        beginTime = new SimpleDateTimePanelImpl();

        Integer[] ii = new Integer[32];
        for(int i=1; i<=ii.length;i++){
            ii[i-1]= i;
        }
        channelCB = new JComboBox<>(ii);

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


        c.gridx=0;
        c.gridy++;
        c.gridwidth=1;
        c.gridheight=1;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("channel", lbl);
        c.gridx++;
        panel.add(channelCB, c);

        c.gridx=0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        c.gridx++;
        labels.put("beginTime", lbl);
        panel.add(beginTime.get(), c);


        c.gridx=0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("endTime", lbl);
        c.gridx++;
        panel.add(endTime.get(), c);

        c.gridx=0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("onSpace", lbl);
        c.gridx++;
        panel.add(onSpace, c);

        c.gridx=0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("offSpace", lbl);
        c.gridx++;
        panel.add(offSpace, c);

        c.gridx=0;
        c.gridy++;
        lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl, c);
        labels.put("total", lbl);
        c.gridx++;
        panel.add(total, c);

        c.gridx=0;
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
        c.weighty=1.0;
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
