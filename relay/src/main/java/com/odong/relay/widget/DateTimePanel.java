package com.odong.relay.widget;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午6:20
 */

public interface DateTimePanel {
    void setText(Map<String, String> map);

    void setDate(Date date, int space);

    Date getDate();
}
