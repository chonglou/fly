package com.odong.fly.model.request;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午8:59
 */
public class VideoRequest extends Request {
    public VideoRequest(int device, String name, int rate) {
        this.device = device;
        this.rate = rate;
        this.name = name;
    }

    @Deprecated
    public VideoRequest() {
    }

    private static final long serialVersionUID = -6913527920970031626L;
    private int device;
    private int rate;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

}
