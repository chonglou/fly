package com.odong.fly.widget;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.OpenCVFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午11:53
 */
public class CameraPanel {

    public CameraPanel(int device) throws Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(device);

        grabber.start();
        opencv_core.IplImage image = grabber.grab();
        if (image == null) {
            throw new IllegalArgumentException("摄像头出错");
        }
        FrameRecorder recorder = new OpenCVFrameRecorder("out.avi", image.width(), image.height());
        //recorder.setCodecID(CV_FOURCC('M','J','P','G'));
        //recorder.setFrameRate(15);
        //recorder.setPixelFormat(1);
        recorder.start();

        CanvasFrame frame = new CanvasFrame("摄像头" + device);
        while (frame.isVisible() && ((image = grabber.grab()) != null)) {

            frame.showImage(image);
            recorder.record(image);

            /*
            if(frame.waitKey(10) != null){
                break;
            }
            */
        }

        frame.dispose();
        recorder.stop();
        grabber.stop();

    }

    private final static Logger logger = LoggerFactory.getLogger(CameraPanel.class);
}
