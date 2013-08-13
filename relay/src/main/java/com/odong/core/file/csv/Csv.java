package com.odong.core.file.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午6:28
 */
public class Csv implements Serializable {
    public void addColumn(Column column){
        if(column.size()!=size){
            throw new IllegalArgumentException("参数个数不对");
        }
        this.columns.add(column);

    }
    public Csv(String name, int size) {
        this.name = name;
        this.size =size;
        this.columns = new ArrayList<>();
    }
    @Deprecated
    public Csv(){
    }

    private static final long serialVersionUID = -1521734064898128058L;
    private String name;
    private int size;
    private List<Column> columns;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }
    @Deprecated
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
