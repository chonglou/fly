package com.odong.core.file.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午6:38
 */
public class Column implements Serializable {
    public int size(){
        return items.size();
    }
    public Object item(int i){
        return items.get(i);
    }
    public void addItem(Object obj){
        if(
                (type == Type.STRING && obj instanceof String)||
                (type == Type.DATE && obj instanceof Date)||
                (type == Type.BOOL && obj instanceof Boolean)
                ){
            this.items.add(obj);
        }
        else {
            throw new IllegalArgumentException("参数类型错误");
        }
    }
    public enum Type{
        DATE,BOOL,STRING
    }
    public Column(String name, Type type) {
        this.name = name;
        this.type = type;
        this.items = new ArrayList<>();
    }
    @Deprecated
    public Column() {
    }

    private static final long serialVersionUID = -5993494080343186343L;
    private boolean optional;
    private String name;
    private List<Object> items;
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Deprecated
    public List<Object> getItems() {
        return items;
    }
    @Deprecated
    public void setItems(List<Object> items) {
        this.items = items;
    }
}
