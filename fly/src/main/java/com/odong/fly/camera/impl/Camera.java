package com.odong.fly.camera.impl;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-10-3
 * Time: 下午4:12
 */
public class Camera {
    public Camera(int device) {
        this.capture = new VideoCapture(device);
        this.device = device;
        this.mat = new Mat();
    }

    public void startUp(String file, int rate) {

        this.recorder = true;
    }

    public void shutDown() {
        this.recorder = false;
    }

    public synchronized void photo(String file) throws IOException {
        if (capture.read(mat)) {
            Highgui.imwrite(file, mat);
        } else {
            throw new IOException("未抓到图片");
        }
    }

    public synchronized BufferedImage photo() throws IOException {
        if (capture.read(mat)) {
            MatOfByte mob = new MatOfByte();
            Highgui.imencode("png", mat, mob);
            try (InputStream in = new ByteArrayInputStream(mob.toArray())) {
                return ImageIO.read(in);
            }
        } else {
            throw new IOException("未抓到图片");
        }
    }

    @Override
    public String toString() {
        return "CAMERA：" + device;
    }

    public void close() {
        mat.release();
        capture.release();
    }

    private final int device;
    private final Mat mat;
    private final VideoCapture capture;
    private boolean recorder;

    public boolean isRecorder() {
        return recorder;
    }

    public void setRecorder(boolean recorder) {
        this.recorder = recorder;
    }
}
