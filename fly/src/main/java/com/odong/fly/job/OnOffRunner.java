package com.odong.fly.job;

import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午11:18
 */
public class OnOffRunner implements Runnable {
    public OnOffRunner(String taskId, StoreHelper storeHelper, SerialUtil serialUtil) {
        this.taskId = taskId;
        this.storeHelper = storeHelper;
        this.serialUtil = serialUtil;
    }

    @Override
    public void run() {
        //FIXME

    }

    private String taskId;
    private StoreHelper storeHelper;
    private SerialUtil serialUtil;
    private final static Logger logger = LoggerFactory.getLogger(OnOffRunner.class);
}
