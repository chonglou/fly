package com.odong.fly.job;

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

                        if (
                                task.getLastStartUp() == null ||
                                        now.getTime() >= task.getLastStartUp().getTime() + 1000 * (on ? oor.getOnSpace() : oor.getOffSpace())
                                ) {
                            taskSender.sendOnOff(task.getId(), oor.getPortName(), Command.onOff(oor.getChannel(), !on));
                        }

                        break;
                    case VIDEO:
                        VideoRequest vr = (VideoRequest) task.getRequest();
                        if (task.getLastStartUp() == null || now.getTime() >= 1000 * task.getSpace()) {
                            taskSender.sendVideo(task.getId(), vr.getDevice(), vr.getRate());
                        }
                        break;
                    case PHOTO:
                        PhotoRequest pr = (PhotoRequest) task.getRequest();
                        if (task.getLastStartUp() == null || now.getTime() >= 1000 * task.getSpace()) {
                            taskSender.sendPhoto(task.getId(), pr.getDevice());
                        }
                        break;
                    default:
                        logger.error("未知的任务类型[{}]", task.getType());
                        break;
                }

            }
        }

    }


    @Resource
    private TaskSender taskSender;
    @Resource
    private StoreHelper storeHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskJob.class);


    public void setTaskSender(TaskSender taskSender) {
        this.taskSender = taskSender;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }


}
