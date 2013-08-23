package com.odong.fly.model.item;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午3:24
 */
public class CameraItem extends Item {

    private String file;
    private static final long serialVersionUID = 6930336068343203468L;



    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
