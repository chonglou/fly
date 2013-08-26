package com.odong.fly.gui;

import com.odong.fly.MyException;
import com.odong.fly.serial.SerialPort;
import com.odong.fly.serial.SerialUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午3:56
 */
@Component("gui.serialDialog")
public class SerialDialog {
    public void show(String portName) {

        this.portName = portName;
        dialog.setTitle(message.getMessage("dialog.serial.title") + "[" + portName + "]");
        dialog.pack();
        dialog.setVisible(true);
    }


    public void hide() {
        dialog.setVisible(false);
    }

    public void setText() {
        for (String s : labels.keySet()) {
            labels.get(s).setText(message.getMessage("serial." + s) + "：");
        }

        for (String s : new String[]{"submit", "cancel"}) {
            buttons.get(s).setText(message.getMessage("button." + s));
        }
    }

    @PostConstruct
    void init() {

        dialog = new JDialog(mainFrame, "", true);
        dialog.setIconImage(message.getIcon());
        Container container = dialog.getContentPane();
        container.setLayout(new BorderLayout(20, 20));

        initMainPanel();
        container.add(mainP, BorderLayout.CENTER);

        for (String s : new String[]{
                BorderLayout.SOUTH, BorderLayout.NORTH, BorderLayout.WEST, BorderLayout.EAST
        }) {
            container.add(new JLabel(), s);
        }

        initEvents();

        dialog.setLocationRelativeTo(mainFrame);
        dialog.setResizable(false);
    }

    @SafeVarargs
    private final <T> void addLine(String name, Class<T> clazz, T... items) {
        JLabel lbl = new JLabel();
        mainP.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        labels.put(name, lbl);

        JComboBox<T> cb = new JComboBox<T>(items);
        cb.setSelectedIndex(0);
        mainP.add(cb);
        comboBoxes.put(name, cb);
    }

    private void initMainPanel() {
        mainP = new JPanel();
        mainP.setLayout(new GridLayout(6, 2, 20, 20));

        labels = new HashMap<>();
        comboBoxes = new HashMap<>();

        addLine("deviceType", SerialPort.Type.class, SerialPort.Type.values());
        addLine("dataBaud", Integer.class, 9600);
        addLine("dataBits", Integer.class, 8);
        addLine("stopBits", Integer.class, 1);
        addLine("parity", String.class, "None");


        buttons = new HashMap<>();
        JButton btn;

        btn = new JButton();
        mainP.add(btn);
        buttons.put("submit", btn);

        btn = new JButton();
        mainP.add(btn);
        buttons.put("cancel", btn);

    }

    private void initEvents() {
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.setVisible(false);
            }
        });

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == buttons.get("submit")) {

                    try {
                        serialUtil.open(portName, (Integer) comboBoxes.get("dataBaud").getSelectedItem(), true);
                        serialUtil.setType(portName, (SerialPort.Type) comboBoxes.get("deviceType").getSelectedItem());
                    } catch (Exception ex) {
                        logger.debug("打开端口出错", ex);
                        if (ex instanceof MyException) {
                            dialogHelper.error(((MyException) ex).getType());
                        }
                    }

                    if (serialUtil.isOpen(portName)) {
                        hide();
                        mainPanel.showSerial(portName);
                    }
                } else {
                    hide();
                }

            }
        };
        for (String s : buttons.keySet()) {
            buttons.get(s).addActionListener(listener);
        }
    }

    private String portName;
    private JDialog dialog;
    private JPanel mainP;
    private Map<String, JLabel> labels;
    private Map<String, JButton> buttons;
    private Map<String, JComboBox> comboBoxes;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private Message message;
    @Resource
    private JFrame mainFrame;
    @Resource(name = "gui.mainPanel")
    private MainPanel mainPanel;
    @Resource
    private Dialog dialogHelper;
    private final static Logger logger = LoggerFactory.getLogger(SerialDialog.class);

    public void setDialogHelper(Dialog dialogHelper) {
        this.dialogHelper = dialogHelper;
    }

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }
}
