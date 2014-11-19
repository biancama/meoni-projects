package com.biancama.gui.easyShipment.model;

import java.io.Serializable;

public class Sequence extends Organization implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -2127225089394077087L;

    
    private String tableName;
    private Integer id;
    private Integer currentValue;
    private Integer incrementNumber;
    private String startNewYear;
    private String year;
    private String docName;
    
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }
    public Integer getCurrentValue() {
        return currentValue;
    }
    public void setIncrementNumber(Integer incrementNumber) {
        this.incrementNumber = incrementNumber;
    }
    public Integer getIncrementNumber() {
        return incrementNumber;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getYear() {
        return year;
    }
    public void setDocName(String docName) {
        this.docName = docName;
    }
    public String getDocName() {
        return docName;
    }
    public void setStartNewYear(String startNewYear) {
        this.startNewYear = startNewYear;
    }
    public String getStartNewYear() {
        return startNewYear;
    }
 
 
    
    
}
