package com.odong.core.file.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午4:55
 */
public class Excel implements Serializable {
    public void addTable(Table...tables){
        this.tables.addAll(Arrays.asList(tables));
    }
    public Excel(String name) {
        this.name = name;
        this.tables = new ArrayList<>();
    }

    @Deprecated
    public Excel(){
    }
    private static final long serialVersionUID = -3092853476405553295L;
    private String name;
    private List<Table> tables;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
