package com.odong.fly.camera;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午10:22
 */
public abstract class CameraUtil {
    public abstract void open();
    public abstract void close();

    protected String randomFileName(String suffix){
        return format.format(new Date())+"."+suffix;
    }
    private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
}
