package com.odong.fly.job;

import com.odong.fly.gui.BootingBar;
import com.odong.fly.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-23
 * Time: 上午11:23
 */
@Component
public class TaskSender {

    public void sendPhoto(String taskId, int device) {
        Map<String, Object> map = new HashMap<>();
        map.put("device", device);
        send(taskId, Task.Type.PHOTO, map);
    }

    public void sendVideo(String taskId, int device, int rate) {
        Map<String, Object> map = new HashMap<>();
        map.put("device", device);
        map.put("rate", rate);
        send(taskId, Task.Type.VIDEO, map);
    }

    public void sendOnOff(String taskId, String portName, String command) {
        Map<String, Object> map = new HashMap<>();
        map.put("portName", portName);
        map.put("command", command);
        send(taskId, Task.Type.ON_OFF, map);
    }

    public void send(final String message) {
        jmsTemplate.send(taskQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                return session.createTextMessage(message);  //
            }
        });
        logger.debug("发送文本消息[{}]", message);
    }


    private void send(final String taskId, final Task.Type type, Map<String, Object> map) {

        jmsTemplate.convertAndSend(taskQueue, map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty("taskId", taskId);
                message.setStringProperty("type", type.name());
                message.setJMSCorrelationID(taskId == null ? UUID.randomUUID().toString() : taskId);
                return message;
            }
        });


        logger.debug("发送任务消息[{}]", taskId);
    }

    @PostConstruct
    void init(){
        bootingBar.next();
    }

    @Resource
    private JmsTemplate jmsTemplate;
    @Resource(name = "taskQueue")
    private Queue taskQueue;
    @Resource
    private BootingBar bootingBar;
    private final static Logger logger = LoggerFactory.getLogger(TaskSender.class);

    public void setBootingBar(BootingBar bootingBar) {
        this.bootingBar = bootingBar;
    }

    public void setTaskQueue(Queue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
