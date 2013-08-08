package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
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
        //todo 关闭串口
        conn = null;
        dialog.setVisible(false);
        cardPanel.hide();
    }

    public boolean isOpen() {
        return conn != null;
    }

    public void setLocale(Locale locale) {
        dialog.setTitle(labelHelper.getMessage("dialog.serial.title", locale));
        for (String s : new String[]{"commPort", "dataBaud", "stopBits", "parity"}) {
            labels.get(s).setText(labelHelper.getMessage("serial." + s, locale) + "：");
        }

        for (String s : new String[]{"submit", "cancel"}) {
            buttons.get(s).setText(labelHelper.getMessage("button." + s, locale));
        }
    }

    @PostConstruct
    void init() {
        dialog = new JDialog(window.get(), "", true);
        Container container = dialog.getContentPane();
        container.setLayout(new BorderLayout(20, 20));


        container.add(getMainPanel(), BorderLayout.CENTER);

        for(String s : new String[]{
                BorderLayout.SOUTH,BorderLayout.NORTH,BorderLayout.WEST,BorderLayout.EAST
        } ){
            container.add(new JLabel(), s);
        }

        initEvents();

        dialog.setLocationRelativeTo(window.get());
        dialog.setResizable(false);
    }

    private JPanel getMainPanel(){

        JPanel mainP = new JPanel();
        mainP.setLayout(new GridLayout(5, 2, 20, 20));

        labels = new HashMap<>();
        JLabel lbl;
        JComboBox cb;

        lbl = new JLabel();
        mainP.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb = new JComboBox<>(new String[]{"COM1", "COM2", "COM3"});
        mainP.add(cb);
        cb.setSelectedIndex(0);
        labels.put("commPort", lbl);

        lbl = new JLabel();
        mainP.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb = new JComboBox<>(new Integer[]{1});
        mainP.add(cb);
        cb.setSelectedIndex(0);
        labels.put("stopBits", lbl);


        lbl = new JLabel();
        mainP.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb = new JComboBox<>(new String[]{"None"});
        mainP.add(cb);
        cb.setSelectedIndex(0);
        labels.put("parity", lbl);

        lbl = new JLabel();
        mainP.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb = new JComboBox<>(new Integer[]{9600});
        cb.setSelectedIndex(0);
        mainP.add(cb);
        labels.put("dataBaud", lbl);

        buttons = new HashMap<>();
        JButton btn;

        btn = new JButton();
        mainP.add(btn);
        buttons.put("submit", btn);

        btn = new JButton();
        mainP.add(btn);
        buttons.put("cancel", btn);

        return mainP;
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
                    //TODO 打开串口
                    conn = new Object();
                }

                dialog.setVisible(false);

            }
        };
        for (String s : buttons.keySet()) {
            buttons.get(s).addActionListener(listener);
        }
    }

    private Object conn;
    private JDialog dialog;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private Window window;
    @Resource
    private CardPanel cardPanel;
    private Map<String, JLabel> labels;
    private Map<String, JButton> buttons;

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
