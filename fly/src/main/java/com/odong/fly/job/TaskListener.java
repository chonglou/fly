package com.odong.fly.job;

import com.odong.fly.camera.CameraUtil;
import com.odong.fly.model.Task;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-23
 * Time: 上午11:38
 */
@Component("taskListener")
public class TaskListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            text((TextMessage) message);

        } else if (message instanceof MapMessage) {
            map((MapMessage) message);
        } else {
            logger.error("未知的消息类型");
        }

    }


    private void map(MapMessage message) {
        String taskId = null;
        try {
            taskId = message.getStringProperty("taskId");
            Task.Type type = Task.Type.valueOf(message.getStringProperty("type"));
            String id = message.getJMSCorrelationID();

            if (taskId != null) {
                logger.debug("收到任务消息[{}]", taskId);
                storeHelper.setStartUp(taskId);
            }

            switch (type) {
                case ON_OFF:
                    String response = serialUtil.send(message.getString("portName"), message.getString("command"));

                    break;
                case VIDEO:
                    break;
                case PHOTO:
                    break;
            }
        } catch (Exception e) {
            logger.error("处理任务消息出错", e);
        } finally {
            if (taskId != null) {
                logger.debug("收到任务消息[{}]", taskId);
                storeHelper.setShutDown(taskId, null);
            } else {

            }
        }
    }


    private void text(TextMessage message) {
        try {
            logger.debug("收到文本消息[{}]", message.getText());
        } catch (JMSException e) {
            logger.error("处理文本消息出错", e);
        }
    }

    @Resource
    private SerialUtil serialUtil;
    @Resource
    private CameraUtil cameraUtil;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private StoreHelper storeHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskListener.class);

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setSerialUtil(SerialUtil serialUtil) {
        this.serialUtil = serialUtil;
    }

    public void setCameraUtil(CameraUtil cameraUtil) {
        this.cameraUtil = cameraUtil;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
