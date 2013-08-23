package com.odong.fly.model.request;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午8:59
 */
public class PhotoRequest extends Request {
    public PhotoRequest(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Deprecated
    public PhotoRequest() {
    }

    private static final long serialVersionUID = -2859334715004023639L;
    private int id;
    private String name;

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
}
