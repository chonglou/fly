package com.odong.relay.widget.impl;

import com.odong.relay.widget.DateTimePanel;
import org.joda.time.DateTime;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午10:28
 */
public class SimpleDateTimePanelImpl extends DateTimePanel {

    public SimpleDateTimePanelImpl() {
        super();
        labels = new HashMap<>();
        comboBoxes = new HashMap<>();
        initPanel();
        bindEvent();
    }

    @Override
    public void setText(Map<String, String> map) {
        for (String s : labels.keySet()) {
            labels.get(s).setText(map.get("s"));
        }
    }

    @Override
    public void setDate(Date date, int space) {
        DateTime dt = new DateTime(date.getTime() + space * 1000);
        setComboBox("year", dt.getYear());
        setComboBox("month", dt.getMonthOfYear());
        setComboBox("day", dt.getDayOfMonth());
        setComboBox("hour", dt.getHourOfDay());
        setComboBox("minute", dt.getMinuteOfHour());
        setComboBox("second", dt.getSecondOfMinute());
    }

    @Override
    public Date getDate() {
        return new DateTime()
                .withYear(getComboBox("year"))
                .withMonthOfYear(getComboBox("month"))
                .withDayOfMonth(getComboBox("day"))
                .withHourOfDay(getComboBox("hour"))
                .withMinuteOfHour(getComboBox("minute"))
                .withSecondOfMinute(getComboBox("second"))
                .withMillisOfSecond(0)
                .toDate();
    }


    private void initPanel() {
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        java.util.List<Integer> list = new ArrayList<>();
        for (int i = 2013; i <= 2050; i++) {
            list.add(i);
        }
        addItem("year", list.toArray(new Integer[1]));

        list.clear();
        for (int i = 1; i <= 12; i++) {
            list.add(i);
        }
        addItem("month", list.toArray(new Integer[1]));

        list.clear();
        for (int i = 1; i <= 31; i++) {
            list.add(i);
        }
        addItem("day", list.toArray(new Integer[1]));

        list.clear();
        for (int i = 1; i <= 24; i++) {
            list.add(i);
        }
        addItem("hour", list.toArray(new Integer[1]));


        list.clear();
        for (int i = 1; i <= 60; i++) {
            list.add(i);
        }
        addItem("minute", list.toArray(new Integer[1]));


        list.clear();
        for (int i = 1; i <= 60; i++) {
            list.add(i);
        }
        addItem("second", list.toArray(new Integer[1]));
    }

    private void bindEvent() {
        //TODO 月份 天数对应
    }

    private void addItem(String key, Integer... items) {
        JComboBox<Integer> cb = new JComboBox<>();
        JLabel lbl = new JLabel();
        for (Integer i : items) {
            cb.addItem(i);
        }
        panel.add(cb);
        panel.add(lbl);
        labels.put(key, lbl);
        comboBoxes.put(key, cb);
    }


    private int getComboBox(String name) {
        return (Integer) (comboBoxes.get(name).getSelectedItem());
    }

    private void setComboBox(String name, int val) {
        comboBoxes.get(name).setSelectedItem(val);
    }

    private JPanel panel;
    private Map<String, JLabel> labels;
    private Map<String, JComboBox> comboBoxes;

    public JPanel get() {
        return panel;
    }
}
