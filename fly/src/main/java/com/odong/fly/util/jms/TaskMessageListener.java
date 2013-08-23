package com.odong.fly.util.jms;

import com.odong.fly.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-23
 * Time: 上午11:38
 */
@Component("jmsMessageListener")
public class TaskMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                logger.debug(((TextMessage) message).getText());
            }
            else if(message instanceof MapMessage){
                MapMessage mm = (MapMessage)message;
                String id = mm.getJMSCorrelationID();
                String taskId = mm.getStringProperty("id");
                Task.Type type = (Task.Type)mm.getObjectProperty("type");
                String portName = mm.getString("portName");
                logger.debug("收到任务消息{}", taskId);
            }
            else {
                logger.error("未知的消息类型");
            }
        } catch (JMSException e) {
            logger.error("处理消息出错", e);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(TaskMessageListener.class);
}
