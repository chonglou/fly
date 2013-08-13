package com.odong.core.file.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午4:56
 */
public class Table implements Serializable {
    public void addColumn(Column...columns){
        for(Column c : columns){
            if(c.size() != size){
                throw new IllegalArgumentException("Column中cell个数一致");
            }
            this.columns.add(c);

        }

    }
    public Table(String name, int size) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.size = size;
    }

    @Deprecated
    public Table() {
    }

    private static final long serialVersionUID = -8782860636768810016L;
    private String name;
    private List<Column> columns;
    private int size;

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
