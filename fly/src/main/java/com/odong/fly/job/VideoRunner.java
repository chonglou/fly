package com.odong.fly.job;

import com.odong.fly.camera.CameraUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午5:20
 */
public class VideoRunner implements Runnable {
    public VideoRunner(String taskId, StoreHelper storeHelper, CameraUtil cameraUtil) {
        this.taskId = taskId;
        this.storeHelper = storeHelper;
        this.cameraUtil = cameraUtil;
    }

    @Override
    public void run() {
        //FIXME
    }

    private String taskId;
    private StoreHelper storeHelper;
    private CameraUtil cameraUtil;
    private final static Logger logger = LoggerFactory.getLogger(VideoRunner.class);

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setCameraUtil(CameraUtil cameraUtil) {
        this.cameraUtil = cameraUtil;
    }
}
