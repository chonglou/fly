package com.odong.relay.widget;

import com.odong.relay.MyException;
import com.odong.relay.serial.SerialHelper;
import com.odong.relay.util.LabelHelper;
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
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午10:50
 */
@Component
public class SerialDialog {
    public void open() {
        dialog.pack();
        dialog.setVisible(true);
    }

    public void close() {
        dialog.setVisible(false);
        cardPanel.hide();
    }

    public void setLocale(Locale locale) {
        dialog.setTitle(labelHelper.getMessage("dialog.serial.title", locale));
        for (String s : labels.keySet()) {
            labels.get(s).setText(labelHelper.getMessage("serial." + s, locale) + "：");
        }

        for (String s : new String[]{"submit", "cancel"}) {
            buttons.get(s).setText(labelHelper.getMessage("button." + s, locale));
        }
    }

    @PostConstruct
    void init() {
        dialog = new JDialog(window.get(), "", true);
        dialog.setIconImage(labelHelper.getIconImage());
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

        dialog.setLocationRelativeTo(window.get());
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

        addLine("commPort", String.class, serialHelper.listPortNames().toArray(new String[1]));
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
        //dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                        serialHelper.open(
                                (String) comboBoxes.get("commPort").getSelectedItem(),
                                (Integer) comboBoxes.get("dataBaud").getSelectedItem(),
                                new SerialHelper.Callback() {
                                    @Override
                                    public void process(byte[] buffer) {
                                        //TODO
                                        logger.debug("返回: " + new String(buffer));
                                    }
                                });
                    } catch (Exception ex) {
                        logger.debug("打开端口出错", ex);
                        if (ex instanceof MyException) {
                            messageDialog.error(((MyException) ex).getType());
                        }
                    }

                    if (serialHelper.isOpen()) {
                        close();
                    }
                } else {
                    close();
                }

            }
        };
        for (String s : buttons.keySet()) {
            buttons.get(s).addActionListener(listener);
        }
    }

    private JDialog dialog;
    private JPanel mainP;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private Window window;
    @Resource
    private CardPanel cardPanel;
    @Resource
    private SerialHelper serialHelper;
    @Resource
    private MessageDialog messageDialog;
    private Map<String, JLabel> labels;
    private Map<String, JButton> buttons;
    private Map<String, JComboBox> comboBoxes;
    private final static Logger logger = LoggerFactory.getLogger(SerialDialog.class);

    public void setMessageDialog(MessageDialog messageDialog) {
        this.messageDialog = messageDialog;
    }

    public void setSerialHelper(SerialHelper serialHelper) {
        this.serialHelper = serialHelper;
    }

    public void setCardPanel(CardPanel cardPanel) {
        this.cardPanel = cardPanel;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
