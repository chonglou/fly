package com.odong.relay.widget.impl;

import com.odong.relay.widget.DateTimePanel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
            labels.get(s).setText(map.get(s));
        }
    }

    @Override
    public void setDate(Date date) {
        DateTime dt = new DateTime(date.getTime());
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
        ItemListener yearMonthListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Integer year = (Integer) comboBoxes.get("year").getSelectedItem();
                    Integer month = (Integer) comboBoxes.get("month").getSelectedItem();
                    int days;
                    switch (month) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                            days = 31;
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            days = 30;
                            break;
                        case 2:
                            days = (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) ? 29 : 28;
                            break;
                        default:
                            throw new IllegalArgumentException("月份不正确");


                    }
                    JComboBox<Integer> dayCB = comboBoxes.get("day");
                    dayCB.removeAllItems();
                    for (int i = 1; i <= days; i++) {
                        dayCB.addItem(i);
                    }
                }
            }
        };
        comboBoxes.get("year").addItemListener(yearMonthListener);
        comboBoxes.get("month").addItemListener(yearMonthListener);
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

    private Map<String, JLabel> labels;
    private Map<String, JComboBox<Integer>> comboBoxes;
    private final static Logger logger = LoggerFactory.getLogger(SimpleDateTimePanelImpl.class);

    public JPanel get() {
        return panel;
    }
}
