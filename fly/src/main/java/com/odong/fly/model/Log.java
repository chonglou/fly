package com.odong.fly.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午10:33
 */
public class Log implements Serializable {
    @Override
    public String toString() {
        return created.toString() + "：" + message;
    }

    private static final long serialVersionUID = 9201693672487578059L;
    private long id;
    private Date created;
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
