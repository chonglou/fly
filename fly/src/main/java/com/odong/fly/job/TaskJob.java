package com.odong.fly.job;

import com.odong.core.util.JsonHelper;
import com.odong.fly.model.Task;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.model.request.PhotoRequest;
import com.odong.fly.model.request.VideoRequest;
import com.odong.fly.serial.Command;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午11:17
 */
@Component("job.taskJobTarget")
public class TaskJob {

    public void execute() {
        Date now = new Date();
        for (Task task : storeHelper.listRunnableTask()) {
            //只判断起始截止时间及总运行次数
            //logger.debug("开始调度任务[{}]", jsonHelper.object2json(task));
            if (now.compareTo(task.getBegin()) >= 0 &&
                    now.compareTo(task.getEnd()) <= 0 &&
                    (task.getTotal() == 0 || task.getTotal() > task.getIndex())
                    ) {
                switch (task.getType()) {
                    case ON_OFF:
                        OnOffRequest oor = (OnOffRequest) task.getRequest();
                        boolean on = Boolean.valueOf(task.getLastStatus());
                        //logger.debug("ON_OFF任务 设备{}:{} 状态{} nextRun:{}", oor.getPortName(), oor.getChannel(), on,next(now, on ? oor.getOffSpace() : oor.getOnSpace()));
                        storeHelper.setTaskStartUp(task.getId(), next(now, on ? oor.getOffSpace() : oor.getOnSpace()), Boolean.toString(!on));
                        taskSender.sendOnOff(task.getId(), oor.getPortName(), Command.onOff(oor.getChannel(), !on));
                        break;
                    case VIDEO:
                        VideoRequest vr = (VideoRequest) task.getRequest();
                        storeHelper.setTaskStartUp(task.getId(), next(task.getEnd(), 1), null);
                        taskSender.sendVideo(task.getId(), vr.getDevice(), vr.getRate());
                        break;
                    case PHOTO:
                        PhotoRequest pr = (PhotoRequest) task.getRequest();
                        storeHelper.setTaskStartUp(task.getId(), next(now, pr.getSpace()), null);
                        taskSender.sendPhoto(task.getId(), pr.getDevice());
                        break;
                    default:
                        logger.error("未知的任务类型[{}]", task.getType());
                        break;
                }

            }
        }

    }

    private Date next(Date date, int space) {
        return new Date(date.getTime() + 1000 * space);
    }

    @Resource
    private TaskSender taskSender;
    @Resource
    private StoreHelper storeHelper;
    @Resource
    private JsonHelper jsonHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskJob.class);

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setTaskSender(TaskSender taskSender) {
        this.taskSender = taskSender;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }


}
