package com.odong.fly.job;

import com.odong.core.util.JsonHelper;
import com.odong.fly.serial.SerialPort;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午11:17
 */
@Component("job.taskTarget")
public class TaskJob {
    public boolean isPortInUse(String portName) {
        for (String tid : tasks) {
            if (storeHelper.getTask(tid).getPortName().equals(portName)) {
                return true;
            }
        }
        return false;
    }

    public String[] getTaskList() {
        return tasks.toArray(new String[tasks.size()]);
    }

    public synchronized void putTask(String portName, int channel, Date begin, Date end, int onSpace, int offSpace, Integer total) {
        if (getTask(portName, channel) == null) {
            Task t = new Task();
            String id = UUID.randomUUID().toString();
            t.setId(id);
            t.setPortName(portName);
            t.setChannel(channel);
            t.setBegin(begin);
            t.setEnd(end);
            t.setOnSpace(onSpace);
            t.setOffSpace(offSpace);
            t.setTotal(total);
            t.setCreated(new Date());
            t.setType(SerialPort.Type.ON_OFF);
            t.setState(Task.State.OFF);
            storeHelper.addTask(t);
            tasks.add(id);
            return;
        }
        logger.error("重复的任务[{}]", jsonHelper.object2json(tasks));

    }


    public void popTask(String id) {
        tasks.remove(id);
    }

    public Task getTask(String portName, int channel) {
        for (String tid : tasks) {
            Task task = storeHelper.getTask(tid);
            if (task.getPortName().equals(portName) && task.getChannel() == channel) {
                return task;
            }
        }
        return null;
    }

    public void execute() {
        //logger.debug(jsonHelper.object2json(taskMap));
        for (String tid : tasks) {
            taskExecutor.execute(new TaskRunner(tid, storeHelper, serialUtil));
        }
    }


    @PostConstruct
    void init() {
        tasks = new LinkedHashSet<>();
    }

    private Set<String> tasks;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private SerialUtil serialUtil;
    private final static Logger logger = LoggerFactory.getLogger(TaskJob.class);

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

}
