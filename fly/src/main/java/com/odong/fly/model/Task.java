package com.odong.fly.model;

import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.model.request.PhotoRequest;
import com.odong.fly.model.request.Request;
import com.odong.fly.model.request.VideoRequest;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午3:23
 */
public class Task implements Serializable {


    public enum Type {
        ON_OFF, VIDEO, PHOTO
    }

    public enum State {
        SUBMIT,
        DONE,
        DELETE
    }

    @Override
    public String toString() {
        switch (type) {
            case ON_OFF:
                OnOffRequest oor = (OnOffRequest) request;
                return oor.getPortName() + ":" + oor.getChannel();
            case PHOTO:
                PhotoRequest pr = (PhotoRequest) request;
                return pr.getName();
            case VIDEO:
                VideoRequest vr = (VideoRequest) request;
                return vr.getName();
        }
        return id;
    }

    private static final long serialVersionUID = -1674127878261854260L;
    private String id;
    private Type type;
    private State state;
    private Request request;
    private String temp;
    private Date begin;
    private Date end;
    private Date lastStartUp;
    private Date lastShutDown;
    private Long total;
    private long index;
    private Integer space;
    private Date created;

    public Integer getSpace() {
        return space;
    }

    public void setSpace(Integer space) {
        this.space = space;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastStartUp() {
        return lastStartUp;
    }

    public void setLastStartUp(Date lastStartUp) {
        this.lastStartUp = lastStartUp;
    }

    public Date getLastShutDown() {
        return lastShutDown;
    }

    public void setLastShutDown(Date lastShutDown) {
        this.lastShutDown = lastShutDown;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

}
