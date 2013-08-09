package com.odong.relay.job;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午11:30
 */
public class Task implements Serializable {
    public static String getName(String portName, int channel) {
        return String.format("serial://%s/%d", portName, channel);
    }

    public static String getPortName(String name) {
        return name.split("/")[2];
    }

    public static int getChannel(String name) {
        return Integer.parseInt(name.split("/")[3]);
    }

    public enum Type {
        ON_OFF
    }

    public enum State {
        ON, OFF
    }

    private static final long serialVersionUID = -6708892526002678347L;
    private String id;
    private String portName;
    private int channel;
    private int onSpace;
    private int offSpace;
    private Date begin;
    private Date end;
    private Date lastRun;
    private Date created;
    private Type type;
    private Integer total;
    private int version;
    private State state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
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

    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
