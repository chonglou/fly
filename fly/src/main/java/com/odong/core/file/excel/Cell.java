package com.odong.core.file.excel;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午5:01
 */
public class Cell implements Serializable {
    public Cell(Object value) {
        this.value = value;
    }

    public Cell(Object value, String link) {
        this.link = link;
        this.value = value;
    }

    @Deprecated
    public Cell() {
    }

    private static final long serialVersionUID = -2553631274279565238L;
    private String link;
    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
