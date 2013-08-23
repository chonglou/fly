package com.odong.fly.job;

import com.odong.fly.camera.CameraUtil;
import com.odong.fly.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午5:20
 */
public class PhotoRunner implements Runnable {
    public PhotoRunner(String taskId, StoreHelper storeHelper, CameraUtil cameraUtil) {
        this.storeHelper = storeHelper;
        this.cameraUtil = cameraUtil;
        this.taskId = taskId;
    }

    @Override
    public void run() {
        //FIXME
    }

    private StoreHelper storeHelper;
    private CameraUtil cameraUtil;
    private String taskId;
    private final static Logger logger = LoggerFactory.getLogger(PhotoRunner.class);

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    public void setCameraUtil(CameraUtil cameraUtil) {
        this.cameraUtil = cameraUtil;
    }
}
