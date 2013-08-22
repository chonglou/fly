package com.odong.fly.util;

import com.odong.fly.job.Task;
import com.odong.fly.model.Item;
import com.odong.fly.model.Log;
import com.odong.fly.model.Photo;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午10:34
 */
public interface StoreHelper {
    void addTask(Task t);

    Task getTask(String id);

    List<Task> listTask(Date begin, Date end);

    void put(String key, String val);

    String get(String key);

    void pop(String key);

    void addItem(String taskId, String request, String response);

    List<Item> listItem(String taskId);

    List<Log> listLog(int len);

    void addLog(String task, String message);

    Photo getPhoto(long id);

    void addPhoto(byte[] image);
}
