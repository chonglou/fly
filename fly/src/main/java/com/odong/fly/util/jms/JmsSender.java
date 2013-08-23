package com.odong.fly.util.jms;

import com.odong.fly.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
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
public class JmsSender {
    public void send(String taskId, String portName, int channel, String command){

        Map<String,Object> map= new HashMap<>();
        map.put("portName", portName);
        map.put("channel", channel);
        map.put("command", command);

        send(taskId, Task.Type.ON_OFF, map);

    }
    private void send(final String taskId, final Task.Type type, Map<String,Object>map){
        logger.debug("发送任务消息{}",taskId);
        jmsTemplate.convertAndSend(destination, map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty("id", taskId);
                message.setObjectProperty("type", type);
                message.setJMSCorrelationID(UUID.randomUUID().toString());
                return message;
            }
        });
    }

    @Resource
    private JmsTemplate jmsTemplate;
    @Resource
    private Destination destination;
    private final static Logger logger = LoggerFactory.getLogger(JmsSender.class);

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
