package com.odong.fly.job;

import com.odong.core.util.JsonHelper;
import com.odong.fly.camera.CameraUtil;
import com.odong.fly.model.Task;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

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
        for (Task task : storeHelper.listTask(Task.State.SUBMIT)) {
            //只判断起始截止时间及总运行次数
            if (now.compareTo(task.getBegin()) >= 0 &&
                    now.compareTo(task.getEnd()) <= 0 &&
                    (task.getTotal() == null || task.getTotal() > task.getIndex())
                    ) {
                logger.debug("开始调度任务[{}]", task.getId());
                switch (task.getType()) {
                    case ON_OFF:
                        taskExecutor.execute(new OnOffRunner(task.getId(), storeHelper, serialUtil));
                        break;
                    case VIDEO:
                        taskExecutor.execute(new VideoRunner(task.getId(), storeHelper, cameraUtil));
                        break;
                    case PHOTO:
                        taskExecutor.execute(new PhotoRunner(task.getId(), storeHelper, cameraUtil));
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

    @Resource
    private StoreHelper storeHelper;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private SerialUtil serialUtil;
    @Resource
    private CameraUtil cameraUtil;
    private final static Logger logger = LoggerFactory.getLogger(TaskJob.class);

    public void setCameraUtil(CameraUtil cameraUtil) {
        this.cameraUtil = cameraUtil;
    }

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
