package com.odong.fly.camera.impl;

import com.odong.fly.camera.CameraUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午10:24
 */
public class CameraUtilDemoImpl extends CameraUtil {


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
        return cameraMap.get(device).enable;  //
    }

    @Override
    public void start(int device, String name, int rate) throws IOException {
        logger.debug("开始录像[{}]", device);
        try (FileWriter fw = new FileWriter(name)) {
            fw.write(new Date().toString());
        }

        cameraMap.get(device).enable = true;
    }

    @Override
    public void stop(int device) throws IOException {
        logger.debug("停止录像[{}]", device);
        cameraMap.get(device).enable = false;
    }

    @Override
    public void photo(int device, String name) throws IOException {
        logger.debug("开始拍照[{}]", device);
        writeImage(name, new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB));  //
    }

    @Override
    public void show(int device, boolean visible) {
        cameraMap.get(device).frame.setVisible(visible);
    }

    @Override
    public void open(int device, String name) {
        logger.debug("打开摄像头[{}]", device);
        JFrame frame = new JFrame();
        frame.add(new JLabel(name));
        cameraMap.put(device, new Camera(frame));
    }

    @Override
    public void close(int device) {
        cameraMap.get(device).frame.dispose();
        cameraMap.remove(device);
        logger.debug("关闭摄像头[{}]", device);
    }

    @Override
    public void init() {
        cameraMap = new HashMap<>();
    }

    @Override
    public void destroy() {
        for (int i : cameraMap.keySet()) {
            close(i);
        }
    }

    @Override
    public boolean isOpen(int device) {
        return cameraMap.containsKey(device);  //
    }

    @Override
    public Set<Integer> listDevice() {
        Set<Integer> map = new HashSet<>();
        for (int i = 0; i < 2; i++) {
            map.add(i);
        }
        return map;  //
    }

    @Override
    public JFrame getFrame(int device) {
        return cameraMap.get(device).frame;  //
    }

    private Map<Integer, Camera> cameraMap;

    class Camera {
        Camera(JFrame frame) {
            this.frame = frame;
        }

        private JFrame frame;
        private boolean enable;
    }

    private final static Logger logger = LoggerFactory.getLogger(CameraUtilDemoImpl.class);

}
