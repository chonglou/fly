package com.odong.core.file.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午4:58
 */
public class Column implements Serializable {
    public Cell cell(int i) {
        return cells.get(i);
    }

    public int size() {
        return cells.size();
    }

    public void addCell(Cell... cells) {
        this.cells.addAll(Arrays.asList(cells));
    }

    @Deprecated
    public Column() {
    }

    public Column(String name) {
        this.name = name;
        this.cells = new ArrayList<>();
        this.wrap = true;
    }

    private static final long serialVersionUID = 8894745419401509063L;
    private String name;
    private boolean wrap;
    private List<Cell> cells;

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Deprecated
    public List<Cell> getCells() {
        return cells;
    }

    @Deprecated
    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }
}
