package com.odong.fly.service;

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

    void addOnOffTask(String id, String portName, int channel, Date begin, Date end, long total, int onSpace, int offSpace);

    void addPhotoTask(String id, int deviceId, String deviceName, Date begin, Date end, long total, int space);

    void addVideoTask(String id, int deviceId, String deviceName, int rate, Date begin, Date end);

    Task getTask(String id);

    Task getAvailSerialTask(String portName, int channel);

    List<Task> listSerialTask(String portName, Task.State... states);

    void setOnOffTaskInfo(String taskId, Date begin, Date end, long total, int onSpace, int offSpace);

    void setTaskState(String taskId, Task.State state);

    List<Task> listTask(Date begin, Date end);

    List<Task> listRunnableTask();

    List<Task> listTask(Task.State... states);

    List<Task> listAvailableTask(Task.Type... types);


    void setTaskStartUp(String taskId, Date nextRun, String lastStatus);

    void setTaskShutDown(String taskId);

    void filterTask();


    void addSerialItem(String id, String taskId, String request, String response, String reason);

    void addCameraItem(String id, String taskId, String file, String reason);

    List<SerialItem> listSerialItem(String taskId, int size);

    List<CameraItem> listCameraItem(String taskId, int size);


    void put(String key, String val);

    String get(String key);

    void pop(String key);


    List<Log> listLog(int len);

    void addLog(String message);


}
