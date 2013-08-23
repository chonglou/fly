package com.odong.fly.model.item;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-23
 * Time: 上午9:36
 */
public abstract class Item implements Serializable {
    public enum Type{
        SUCCESS,FAIL
    }
    protected long id;
    protected String task;
    protected Date created;
    protected String reason;
    protected Type type;
    private static final long serialVersionUID = -9113676352027198039L;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
