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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午11:17
 */
@Component("job.taskTarget")
public class TaskJob {
    public void putTask(String portName, int channel, Date begin, Date end, int onSpace, int offSpace, Integer total) {

        Task t = new Task();
        t.setId(UUID.randomUUID().toString());
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
        taskMap.put(Task.getName(portName, channel), t);
    }


    public void popTask(String portName, int channel) {
        taskMap.remove(Task.getName(portName, channel));
    }

    public void execute() {
        //logger.debug(jsonHelper.object2json(taskMap));
        for (Task t : taskMap.values()) {
            taskExecutor.execute(new TaskRunner(t, storeHelper, serialUtil));
        }
    }


    @PostConstruct
    void init() {
        taskMap = new HashMap<>();
    }

    Map<String, Task> taskMap;
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
