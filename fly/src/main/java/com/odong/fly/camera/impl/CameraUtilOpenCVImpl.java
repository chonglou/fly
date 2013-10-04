package com.odong.fly.camera.impl;

import com.odong.fly.camera.CameraUtil;
import org.opencv.highgui.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.videoInputLib;
 */


/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午10:24
 */
public class CameraUtilOpenCVImpl extends CameraUtil {


    @Override
    public Set<Integer> getStatus() {
        Set<Integer> set = new HashSet<>();
        for (Integer i : cameraMap.keySet()) {
            set.add(i);
        }
        return set;
    }

    @Override
    public boolean hasOpen() {
        return cameraMap.size() > 0;  //
    }

    @Override
    public boolean isRecorder(int device) {
        return cameraMap.get(device).isRecorder();
    }

    @Override
    public synchronized void start(final int device, final String name, final int rate) throws IOException {
        logger.debug("开始录像[{}]", device);

    }

    @Override
    public synchronized void stop(int device) throws IOException {

        logger.debug("停止录像[{}]", device);
    }

    @Override
    public synchronized void photo(int device, String file) throws IOException {
        logger.debug("开始拍照[{}]", device);
        cameraMap.get(device).photo(file);
    }


    @Override
    public synchronized void open(int device) throws IOException {
        Camera camera = cameraMap.get(device);
        if (camera == null) {
            cameraMap.put(device, new Camera(device));
        }
    }


    @Override
    public synchronized void close(int device) throws IOException {
        logger.debug("关闭摄像头[{}]", device);
        Camera camera = cameraMap.get(device);
        if (camera != null) {
            camera.close();
        }
        cameraMap.remove(device);
    }

    @Override
    public synchronized boolean isOpen(int device) {
        return cameraMap.containsKey(device);  //
    }

    @Override
    public Set<Integer> listDevice() {
        Set<Integer> devices = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            VideoCapture capture = new VideoCapture();
            capture.open(i);
            if (!capture.isOpened()) {
                break;
            }
            capture.release();
            devices.add(i);
        }
        return devices;  //

    }


    @Override
    public void init() {
        cameraMap = new HashMap<>();
    }

    @Override
    public void destroy() {
        for (Integer device : cameraMap.keySet()) {
            try {
                close(device);
            } catch (IOException e) {
                logger.error("销毁失败", e);
            }
        }
    }

    private Map<Integer, Camera> cameraMap;

    @Resource
    private TaskExecutor taskExecutor;
    private final static Logger logger = LoggerFactory.getLogger(CameraUtilOpenCVImpl.class);

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
