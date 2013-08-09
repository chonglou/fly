package com.odong.relay.job;

import com.odong.core.util.JsonHelper;
import com.odong.relay.serial.SerialUtil;
import com.odong.relay.util.StoreHelper;
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
    public String[] getTaskList() {
        return tasks.toArray(new String[1]);
    }

    public void putTask(String portName, int channel, Date begin, Date end, int onSpace, int offSpace, Integer total) {
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
        t.setType(Task.Type.ON_OFF);
        t.setState(Task.State.OFF);
        storeHelper.addTask(t);
        tasks.add(id);
    }


    public void popTask(String id) {
        tasks.remove(id);
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
