package com.odong.fly.job;

import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-24
 * Time: 上午12:08
 */
@Component("job.taskFilterTarget")
public class TaskFilter {
    public void execute() {

        logger.debug("清理任务状态");
        storeHelper.filterTask();
    }

    @Resource
    private StoreHelper storeHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskFilter.class);

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}
