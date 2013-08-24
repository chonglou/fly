package com.odong.fly.job;

import com.odong.core.util.JsonHelper;
import com.odong.fly.camera.CameraUtil;
import com.odong.fly.model.Task;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.model.request.PhotoRequest;
import com.odong.fly.model.request.VideoRequest;
import com.odong.fly.serial.Command;
import com.odong.fly.serial.OnOff;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
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

    public void execute() {
        Date now = new Date();
        Command command;


        for (Task task : storeHelper.listTask(Task.State.SUBMIT)) {
            //只判断起始截止时间及总运行次数
            if (now.compareTo(task.getBegin()) >= 0 &&
                    now.compareTo(task.getEnd()) <= 0 &&
                    (task.getTotal() == null || task.getTotal() > task.getIndex())
                    ) {
                logger.debug("开始调度任务[{}]", task.getId());
                switch (task.getType()) {
                    case ON_OFF:
                        OnOffRequest oor = (OnOffRequest) task.getRequest();
                        boolean on = Boolean.valueOf(task.getTemp());
                        command = new OnOff(oor.getChannel(), !on);
                        if (
                                task.getLastStartUp() == null ||
                                        now.getTime() >= task.getLastStartUp().getTime() + 1000*(on?oor.getOnSpace():oor.getOffSpace())
                                ) {
                            Map<String,Object> map = new HashMap<>();
                            map.put("portName", oor.getPortName());
                            map.put("channel", oor.getChannel());
                            map.put("command", command.toString());
                            send(task.getId(), Task.Type.ON_OFF, map);
                        }

                        break;
                    case VIDEO:
                        VideoRequest vr = (VideoRequest) task.getRequest();
                        if(task.getLastStartUp() == null || now.getTime()>=1000*task.getSpace()){
                            Map<String,Object> map = new HashMap<>();
                            map.put("id", vr.getDevice());
                            map.put("rate", vr.getRate());
                            send(task.getId(), Task.Type.VIDEO, map);
                        }
                        break;
                    case PHOTO:
                        PhotoRequest pr = (PhotoRequest)task.getRequest();
                        if(task.getLastStartUp() == null || now.getTime()>=1000*task.getSpace()){
                            Map<String,Object> map = new HashMap<>();
                            map.put("device", pr.getDevice());
                            send(task.getId(), Task.Type.PHOTO,map);

                        }
                        break;
                    default:
                        logger.error("未知的任务类型[{}]", task.getType());
                        break;
                }

            }
        }
    }


    @PostConstruct
    void init() {

    }


    private void send(final String taskId, final Task.Type type, Map<String,Object> map){

        jmsTemplate.convertAndSend(taskQueue, map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty("id", taskId);
                message.setStringProperty("type", type.name());
                message.setJMSCorrelationID(UUID.randomUUID().toString());
                return message;
            }
        });


        logger.debug("发送任务消息[{}]",taskId);
    }

    @Resource
    private JmsTemplate jmsTemplate;
    @Resource(name = "taskQueue")
    private Queue taskQueue;
    @Resource
    private StoreHelper storeHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskJob.class);

    public void setTaskQueue(Queue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }


}
