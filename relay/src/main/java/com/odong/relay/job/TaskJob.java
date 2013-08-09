package com.odong.relay.job;

import com.odong.core.util.JsonHelper;
import com.odong.relay.MyException;
import com.odong.relay.model.OnOffCommand;
import com.odong.relay.serial.SerialHelper;
import com.odong.relay.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午11:17
 */
@Component("job.taskTarget")
public class TaskJob {
    public void putOnOffTask(int port, Date begin, Date end, int onSpace, int offSpace, Integer total) {
        Task t = new Task();
        t.setPort(port);
        t.setBegin(begin);
        t.setEnd(end);
        t.setOnSpace(onSpace);
        t.setOffSpace(offSpace);
        t.setTotal(total);
        t.setCreated(new Date());
        t.setType(Task.Type.ON_OFF);
        t.setState(Task.State.OFF);
        taskMap.put(getTaskName(port, Task.Type.ON_OFF), t);
    }

    public String getTaskName(int port, Task.Type type) {
        return "task://" + type.name() + "/" + port;
    }

    public void popOnOffTask(int port) {
        taskMap.remove(getTaskName(port, Task.Type.ON_OFF));
    }

    public void execute() {
        final Date now = new Date();
        //logger.debug(getStatus());
        if (serialHelper.isOpen()) {
            for (Task t : taskMap.values()) {
                if (now.compareTo(t.getBegin()) >= 0 && now.compareTo(t.getEnd()) <= 0) {
                    //超过运行次数
                    if (t.getTotal() != null && t.getTotal() >= t.getVersion()) {
                        continue;
                    }
                    //时间间隔
                    if (t.getLastRun() != null) {
                        switch (t.getType()) {
                            case ON_OFF:
                                if (now.getTime() < (t.getLastRun().getTime() + (t.getState() == Task.State.ON ? t.getOnSpace() : t.getOffSpace()) * 1000)) {
                                    continue;
                                }
                                break;
                        }
                    }
                    //开始运行
                    try {
                        switch (t.getType()) {
                            case ON_OFF:
                                serialHelper.send(new OnOffCommand(t.getPort(), t.getState() == Task.State.ON).toString().getBytes());
                                t.setState(t.getState() == Task.State.ON ? Task.State.OFF : Task.State.ON);
                                break;
                        }
                    } catch (Exception e) {
                        logger.error("执行任务失败", e);
                        if (e instanceof MyException) {
                            storeHelper.add(t.getPort(), ((MyException) e).getType().name());
                        }
                    } finally {
                        t.setLastRun(new Date());
                        t.setVersion(t.getVersion() + 1);
                    }
                    //运行结束
                }
            }
        }
    }

    public String getStatus() {
        return jsonHelper.object2json(taskMap);
    }

    @PostConstruct
    void init() {
        taskMap = new HashMap<>();
    }

    @Resource
    private StoreHelper storeHelper;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    SerialHelper serialHelper;
    @Resource
    private JsonHelper jsonHelper;
    @Value("${channel.size}")
    private int size;
    Map<String, Task> taskMap;
    private final static Logger logger = LoggerFactory.getLogger(TaskJob.class);


    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setSerialHelper(SerialHelper serialHelper) {
        this.serialHelper = serialHelper;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
