package com.biancama.gui.easyShipment.model;

import java.io.Serializable;

public class PriceList extends Organization implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7653321512746200086L;
    private Integer id;
    private Integer currencyId;
    private String name;
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }
    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
    public Integer getCurrencyId() {
        return currencyId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    
}
