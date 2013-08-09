package com.odong.relay.widget;

import com.odong.relay.MyException;
import com.odong.relay.serial.Command;
import com.odong.relay.serial.SerialPort;
import com.odong.relay.serial.SerialUtil;
import com.odong.relay.util.GuiHelper;
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
 * Date: 13-8-7
 * Time: 上午10:50
 */
@Component
public class SerialDialog {
    public void show(String portName) {
        this.portName = portName;
        dialog.setTitle(guiHelper.getMessage("dialog.serial.title") + "[" + portName + "]");
        dialog.pack();
        dialog.setVisible(true);
    }

    public void hide() {
        dialog.setVisible(false);
    }

    public void setText() {
        for (String s : labels.keySet()) {
            labels.get(s).setText(guiHelper.getMessage("serial." + s) + "：");
        }

        for (String s : new String[]{"submit", "cancel"}) {
            buttons.get(s).setText(guiHelper.getMessage("button." + s));
        }
    }

    @PostConstruct
    void init() {
        JFrame window = guiHelper.getWindow();
        dialog = new JDialog(window, "", true);
        dialog.setIconImage(guiHelper.getIconImage());
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

        dialog.setLocationRelativeTo(window);
        dialog.setResizable(false);
    }

    private <T> void addLine(String name, Class<T> clazz, T... items) {
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

        addLine("deviceType", Command.Type.class, Command.Type.values());
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
                        serialUtil.open(
                                portName,
                                (Integer) comboBoxes.get("dataBaud").getSelectedItem(),
                                new SerialPort.Callback() {
                                    @Override
                                    public void process(byte[] buffer) {
                                        //TODO 处理返回
                                        logger.debug("返回: " + new String(buffer));
                                    }
                                });
                    } catch (Exception ex) {
                        logger.debug("打开端口出错", ex);
                        if (ex instanceof MyException) {
                            guiHelper.showErrorDialog(((MyException) ex).getType());
                        }
                    }

                    if (serialUtil.isOpen(portName)) {
                        //操作成功 返回
                        hide();
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
    private GuiHelper guiHelper;
    @Resource
    private SerialUtil serialUtil;
    private final static Logger logger = LoggerFactory.getLogger(SerialDialog.class);

    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }
}
