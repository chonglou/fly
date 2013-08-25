package com.odong.fly.model.request;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午8:59
 */
public class PhotoRequest extends Request {
    public PhotoRequest(int device, String name, int space) {
        this.device = device;
        this.name = name;
        this.space = space;
    }

    @Deprecated
    public PhotoRequest() {
    }

    private static final long serialVersionUID = -2859334715004023639L;
    private int device;
    private String name;
    private int space;

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

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
}
