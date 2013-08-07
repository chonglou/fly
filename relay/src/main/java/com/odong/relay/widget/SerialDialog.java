package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
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
    public void open(){
        dialog.pack();
        dialog.setVisible(true);

        conn = new Object();
    }
    public void close(){
        conn = null;
        dialog.setVisible(false);
    }
    public boolean isOpen(){
        return conn!=null;
    }

    public void setLocale(Locale locale){
        dialog.setTitle(labelHelper.getMessage("dialog.serial.title", locale));
        for(String s : new String[]{"commPort", "dataBaud", "stopBits", "parity"}){
            labels.get(s).setText(labelHelper.getMessage("serial."+s,locale)+"：");
        }

        for(String s : new String[]{"submit", "cancel"}){
            buttons.get(s).setText(labelHelper.getMessage("button."+s, locale));
        }
    }

    @PostConstruct
    void init(){
        dialog = new JDialog(window.get(),"", true);
        Container container = dialog.getContentPane();
        container.setLayout(new GridLayout(5, 2));

        labels = new HashMap<>();
        JLabel lbl;
        JComboBox cb;

        lbl= new JLabel();
        container.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb = new JComboBox<>(new String[]{"COM1","COM2", "COM3"});
        container.add(cb);
        cb.setSelectedIndex(0);
        labels.put("commPort", lbl);

        lbl = new JLabel();
        container.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb=new JComboBox<>(new Integer[]{1});
        container.add(cb);
        cb.setSelectedIndex(0);
        labels.put("stopBits", lbl);


        lbl=new JLabel();
        container.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb = new JComboBox<>(new String[]{"None"});
        container.add(cb);
        cb.setSelectedIndex(0);
        labels.put("parity", lbl);

        lbl=new JLabel();
        container.add(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cb=new JComboBox<>(new Integer[]{9600});
        cb.setSelectedIndex(0);
        container.add(cb);
        labels.put("dataBaud", lbl);

        buttons = new HashMap<>();
        JButton btn;

        btn = new JButton();
        container.add(btn);
        buttons.put("submit", btn);

        btn = new JButton();
        container.add(btn);
        buttons.put("cancel", btn);

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(window.get());
        dialog.setResizable(false);
    }

    private Object conn;
    private JDialog dialog;
    @Resource
    private LabelHelper labelHelper;
    @Resource
    private Window window;
    private Map<String,JLabel> labels;
    private Map<String, JButton> buttons;

    public void setWindow(Window window) {
        this.window = window;
    }

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }
}
