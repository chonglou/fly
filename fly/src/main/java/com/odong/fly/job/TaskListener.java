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
        try {
            if (message instanceof TextMessage) {
                logger.debug("收到文本任务[{}]",((TextMessage) message).getText());
            }
            else if(message instanceof MapMessage){
                MapMessage mm = (MapMessage)message;
                String id = mm.getJMSCorrelationID();
                String taskId = mm.getStringProperty("id");
                logger.debug("收到任务消息[{}]", taskId);
                storeHelper.setStartUp(taskId);
                switch (Task.Type.valueOf(mm.getStringProperty("type"))){
                    case ON_OFF:
                        String portName = mm.getString("portName");
                        int channel = mm.getInt("channel");
                        String command = mm.getString("command");

                        break;
                    case VIDEO:
                        break;
                    case PHOTO:
                        break;
                }


            }
            else {
                logger.error("未知的消息类型");
            }
        } catch (JMSException e) {
            logger.error("处理消息出错", e);
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
