package com.odong.fly.model.request;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午8:59
 */
public class VideoRequest extends Request {
    public VideoRequest(int id, String name, int rate, int onSpace, int offSpace) {
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.onSpace = onSpace;
        this.offSpace = offSpace;
    }

    @Deprecated
    public VideoRequest() {
    }

    private static final long serialVersionUID = -6913527920970031626L;
    private int id;
    private String name;
    private int rate;
    private int onSpace;
    private int offSpace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getOnSpace() {
        return onSpace;
    }

    public void setOnSpace(int onSpace) {
        this.onSpace = onSpace;
    }

    public int getOffSpace() {
        return offSpace;
    }

    public void setOffSpace(int offSpace) {
        this.offSpace = offSpace;
    }
}
