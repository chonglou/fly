package com.odong.fly.camera.impl;

import com.odong.fly.camera.CameraUtil;
import com.odong.fly.util.GuiHelper;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午10:24
 */
public class CameraUtilDemoImpl extends CameraUtil {

    @Override
    public boolean isRecorder(int device) {
        return cameraMap.get(device).enable;  //
    }

    @Override
    public void start(int device, String name, int rate) throws IOException {
        try (FileWriter fw = new FileWriter(name)) {
            fw.write(new Date().toString());
        }

        cameraMap.get(device).enable = true;
    }

    @Override
    public void stop(int device) throws IOException {
        cameraMap.get(device).enable = false;
    }

    @Override
    public void photo(int device, String name) throws IOException {
        writeImage(name, new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB));  //
    }

    @Override
    public void show(int device, boolean visible) {
        cameraMap.get(device).frame.setVisible(visible);
    }

    @Override
    public void open(int device) {
        JFrame frame = new JFrame();
        frame.add(new JLabel(guiHelper.getMessage("camera")));


        cameraMap.put(device, new Camera(frame));
    }

    @Override
    public void close(int device) {
        cameraMap.get(device).frame.dispose();
        cameraMap.remove(device);
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
    public Map<Integer, String> listDevice() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            map.put(i, "摄像头 " + i);
        }
        return map;  //
    }

    @Override
    public JFrame getFrame(int device) {
        return cameraMap.get(device).frame;  //
    }

    private Map<Integer, Camera> cameraMap;
    @Resource
    private GuiHelper guiHelper;

    class Camera {
        Camera(JFrame frame) {
            this.frame = frame;
        }

        private JFrame frame;
        private boolean enable;
    }


    public void setGuiHelper(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }
}
