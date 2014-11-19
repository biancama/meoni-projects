package com.biancama.gui.easyShipment.model;

import java.io.Serializable;

public class Warehouse extends Organization implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5755886575542454245L;

    
    private Integer id;
    private String name;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
}
