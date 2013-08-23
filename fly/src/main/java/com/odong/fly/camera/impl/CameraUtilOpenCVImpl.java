package com.odong.fly.camera.impl;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.videoInputLib;
import com.odong.fly.camera.CameraUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午10:24
 */
public class CameraUtilOpenCVImpl extends CameraUtil {
    @Override
    public boolean isRecorder(int device) {
        return cameraMap.get(device).enable;
    }

    @Override
    public synchronized void start(final int device, final String name, final int rate) throws IOException {

        final Camera camera = cameraMap.get(device);
        if (camera.enable) {
            throw new IOException("摄像头[" + device + "]已经在录像");
        }
        logger.debug("开始录像[{}]", device);
        try {
            opencv_core.IplImage image = camera.grabber.grab();
            if (image == null) {
                throw new IllegalArgumentException("摄像头出错");
            }
            final FrameRecorder recorder = new OpenCVFrameRecorder(name, image.width(), image.height());
            //recorder.setCodecID(CV_FOURCC('M','J','P','G'));
            recorder.setFrameRate(rate);
            //recorder.setPixelFormat(1);
            recorder.start();

            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.enable = true;
                        opencv_core.IplImage image;
                        while (camera.enable && ((image = camera.grabber.grab()) != null)) {
                            if (camera.frame.isVisible()) {
                                camera.frame.showImage(image);
                            }
                            recorder.record(image);
                        }
                    } catch (Exception e) {
                        logger.error("摄像头[{}]录像出错", device, e);
                        camera.enable = false;
                    } finally {
                        try {
                            recorder.stop();
                        } catch (Exception e) {
                            logger.error("停止摄像头[{}]录像出错", device, e);
                        }
                    }
                }
            });

        } catch (Exception e) {
            throw new IOException("摄像头[" + device + "]未就绪", e);

        }

    }

    @Override
    public synchronized void stop(int device) throws IOException {

        Camera camera = cameraMap.get(device);
        if (!camera.enable) {
            throw new IOException("摄像头[" + device + "]尚未开始录像");
        }
        logger.debug("停止录像[{}]", device);
        camera.enable = false;
    }

    @Override
    public synchronized void photo(int device, String name) throws IOException {
        logger.debug("开始拍照[{}]", device);
        try {
            opencv_core.IplImage iImg = cameraMap.get(device).grabber.grab();
            if (iImg == null) {
                throw new IllegalAccessException("空图片");
            }
            writeImage(name, iImg.getBufferedImage());
        } catch (Exception e) {
            throw new IOException("摄像头[" + device + "]拍照出错", e);
        }

    }

    @Override
    public void show(int device, boolean visible) {
        cameraMap.get(device).frame.setVisible(visible);
    }


    @Override
    public synchronized void open(int device) throws IOException {
        logger.debug("打开摄像头[{}]", device);
        try {
            //OpenCV在windows下使用Video，win7下可能无法正常工作，需要启用DirectShow
            FrameGrabber grabber = isWindows ? new VideoInputFrameGrabber(device) : new OpenCVFrameGrabber(device);
            grabber.start();

            CanvasFrame frame = new CanvasFrame("摄像头[" + getCameraName(device) + "]");

            cameraMap.put(device, new Camera(frame, grabber));

        } catch (Exception e) {
            throw new IOException("打开摄像头[" + device + "]失败", e);
        }
    }

    @Override
    public synchronized void close(int device) throws IOException {
        logger.debug("关闭摄像头[{}]", device);
        try {
            Camera camera = cameraMap.get(device);
            camera.frame.dispose();
            camera.grabber.stop();

        } catch (Exception e) {
            throw new IOException("关闭摄像头[" + device + "]失败", e);
        } finally {
            cameraMap.remove(device);
        }
    }

    @Override
    public void init() {
        isWindows = System.getProperty("os.name").contains("Windows");
        cameraMap = new HashMap<>();
    }

    @Override
    public void destroy() {
        for (Integer device : cameraMap.keySet()) {
            try {
                close(device);
            } catch (IOException e) {
                logger.error("关闭摄像头[" + device + "]出错", e);
            }
        }
    }

    @Override
    public synchronized boolean isOpen(int device) {
        return cameraMap.containsKey(device);  //
    }

    @Override
    public Map<Integer, String> listDevice() {
        int count = videoInputLib.videoInput.listDevices();
        Map<Integer, String> devices = new HashMap<>();
        for (int i = 0; i < count; i++) {
            devices.put(i, getCameraName(i));
        }
        return devices;  //
    }

    @Override
    public JFrame getFrame(int device) {
        return cameraMap.get(device).frame;
    }

    private String getCameraName(int device) {
        return videoInputLib.videoInput.getDeviceName(device);
    }


    class Camera {
        Camera(CanvasFrame frame, FrameGrabber grabber) {
            this.frame = frame;
            this.grabber = grabber;
        }

        private final CanvasFrame frame;
        private final FrameGrabber grabber;
        private boolean enable;
    }

    private Map<Integer, Camera> cameraMap;
    private boolean isWindows;
    @Resource
    private TaskExecutor taskExecutor;
    private final static Logger logger = LoggerFactory.getLogger(CameraUtilOpenCVImpl.class);

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
