package com.odong.fly.util;

import com.odong.fly.model.Log;
import com.odong.fly.model.Task;
import com.odong.fly.model.item.CameraItem;
import com.odong.fly.model.item.SerialItem;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午10:34
 */
public interface StoreHelper {

    void addOnOffTask(String id, String portName, int channel, Date begin, Date end, Long total, int onSpace, int offSpace);

    void addPhotoTask(String id, int device, Date begin, Date end, Long total, int space);

    void addVideoTask(String id, int device, int rate, Date begin, Date end, Long total, int onSpace, int offSpace);

    Task getTask(String id);

    void setTaskState(String taskId, Task.State state);

    List<Task> listTask(Date begin, Date end);

    List<Task> listTask(Task.State state);

    List<Task> listTask(Task.Type type);

    void startUp(String taskId);

    void shutDown(String taskId, String temp, Task.State state);


    void addSerialItem(String taskId, String request, String response);

    void addCameraItem(String taskId, String file);

    List<SerialItem> listSerialItem(String taskId);

    List<CameraItem> listCameraItem(String taskId);


    void put(String key, String val);

    String get(String key);

    void pop(String key);


    List<Log> listLog(int len);

    void addLog(String message);


}
