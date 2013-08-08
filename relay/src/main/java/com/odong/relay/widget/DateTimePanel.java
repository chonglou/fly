package com.odong.relay.widget;

import com.odong.relay.util.LabelHelper;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午6:20
 */

public class DateTimePanel {
    public DateTimePanel(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        labels = new HashMap<>();
        comboBoxes = new HashMap<>();

        java.util.List<Integer> list = new ArrayList<>();
        for(int i=2013; i<=2050;i++){
            list.add(i);
        }
        addItem("year", list.toArray(new Integer[1]));

        list.clear();
        for(int i=1;i<=12; i++){
            list.add(i);
        }
        addItem("month", list.toArray(new Integer[1]));

        list.clear();
        for(int i=1; i<=31; i++){
            list.add(i);
        }
        addItem("day", list.toArray(new Integer[1]));

        list.clear();
        for(int i=1; i<=24;i++){
            list.add(i);
        }
        addItem("hour", list.toArray(new Integer[1]));


        list.clear();
        for(int i=1; i<=60;i++){
            list.add(i);
        }
        addItem("minute", list.toArray(new Integer[1]));


        list.clear();
        for(int i=1; i<=60;i++){
            list.add(i);
        }
        addItem("second", list.toArray(new Integer[1]));
    }

    private void addItem(String key, Integer...items){
        JComboBox<Integer> cb = new JComboBox<>();
        JLabel lbl = new JLabel();
        for(Integer i : items){
            cb.addItem(i);
        }
        panel.add(cb);
        panel.add(lbl);
        labels.put(key, lbl);
    }


    public void setLocale(Locale locale){
        for(String s : labels.keySet()){
            labels.get(s).setText(labelHelper.getMessage("dateTimeP."+s, locale));
        }

    }

    private JPanel panel;
    private LabelHelper labelHelper;
    private Map<String, JLabel> labels;
    private Map<String, JComboBox> comboBoxes;

    public void setLabelHelper(LabelHelper labelHelper) {
        this.labelHelper = labelHelper;
    }

    public JPanel get() {
        return panel;
    }
}
