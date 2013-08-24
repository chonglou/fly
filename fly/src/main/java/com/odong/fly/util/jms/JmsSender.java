package com.odong.fly.util.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-23
 * Time: 上午11:23
 */
@Component
public class JmsSender {
    public void send(final String message){
        jmsTemplate.send(taskQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                return session.createTextMessage(message);  //
            }
        });
        logger.debug("发送文本消息[{}]", message);
    }


    @Resource
    private JmsTemplate jmsTemplate;
    @Resource(name = "taskQueue")
    private Queue taskQueue;
    private final static Logger logger = LoggerFactory.getLogger(JmsSender.class);

    public void setTaskQueue(Queue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
