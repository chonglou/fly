package com.odong.relay.job;

import com.odong.relay.MyException;
import com.odong.relay.serial.OnOff;
import com.odong.relay.serial.SerialUtil;
import com.odong.relay.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午11:18
 */
public class TaskRunner implements Runnable {
    public TaskRunner(String tid, StoreHelper storeHelper, SerialUtil serialUtil) {
        this.tid = tid;
        this.serialUtil = serialUtil;
        this.storeHelper = storeHelper;
    }

    @Override
    public void run() {
        final Date now = new Date();
        Task task = storeHelper.getTask(tid);
        if (serialUtil.isOpen(task.getPortName())) {
            if (now.compareTo(task.getBegin()) >= 0 && now.compareTo(task.getEnd()) <= 0) {
                //超过运行次数
                if (task.getTotal() != 0 && task.getTotal() >= task.getVersion()) {
                    return;
                }
                //时间间隔
                if (task.getLastRun() != null) {
                    switch (task.getType()) {
                        case ON_OFF:
                            if (now.getTime() < (task.getLastRun().getTime() + (task.getState() == Task.State.ON ? task.getOnSpace() : task.getOffSpace()) * 1000)) {
                                return;
                            }
                            break;
                    }
                }
                //开始运行
                try {
                    switch (task.getType()) {
                        case ON_OFF:
                            serialUtil.send(task.getPortName(), new OnOff(task.getChannel(), task.getState() == Task.State.ON));
                            task.setState(task.getState() == Task.State.ON ? Task.State.OFF : Task.State.ON);
                            break;
                    }
                } catch (Exception e) {
                    logger.error("执行任务失败", e);
                    if (e instanceof MyException) {
                        storeHelper.addLog(task.getId(), ((MyException) e).getType().name());
                    }
                } finally {
                    task.setLastRun(new Date());
                    task.setVersion(task.getVersion() + 1);
                }
                //运行结束
            }
        }
    }

    private SerialUtil serialUtil;
    private StoreHelper storeHelper;
    private String tid;
    private final static Logger logger = LoggerFactory.getLogger(TaskRunner.class);
}
