package com.odong.fly.model.item;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午3:23
 */
public class SerialItem implements Serializable {
    private static final long serialVersionUID = -7789316459750166683L;
    private long id;
    private String task;
    private String request;
    private String response;
    private Date created;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
