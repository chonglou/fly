package com.odong.fly.job;

import com.odong.fly.MyException;
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

        try {
            String taskId = message.getStringProperty("taskId");
            Task.Type type = Task.Type.valueOf(message.getStringProperty("type"));
            String itemId = message.getJMSCorrelationID();

            if (taskId != null) {
                logger.debug("收到任务消息[{}]", taskId);
            }


            String reason = null;
            String response = null;
            switch (type) {
                case ON_OFF:
                    String portName = message.getString("portName");
                    String request = message.getString("command");
                    try {
                        if (serialUtil.isOpen(portName)) {
                            response = serialUtil.send(portName, request);
                            if (taskId != null) {
                                storeHelper.setTaskShutDown(taskId, null);
                            }
                        } else {
                            reason = MyException.Type.SERIAL_PORT_NOT_VALID.name();
                        }
                    } catch (Exception e) {
                        logger.error("处理任务[{}]出错", taskId, e);
                        reason = e.getMessage();
                    } finally {
                        storeHelper.addSerialItem(itemId, taskId, request, response, reason);
                    }

                    break;
                case VIDEO:
                    //storeHelper.setTaskShutDown();
                    break;
                case PHOTO:
                    //storeHelper.addCameraItem();
                    break;
            }
        } catch (JMSException e) {
            logger.error("任务消息结构错误", e);

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
