package com.biancama.gui.easyShipment.model;

import java.io.Serializable;
import java.util.Date;

public class PriceListVersion extends Organization implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -621552273411766079L;

    private Integer id;
    private String name;
    private String description;
    private Integer priceListId;
    private Date validFrom;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getPriceListId() {
        return priceListId;
    }
    public void setPriceListId(Integer priceListId) {
        this.priceListId = priceListId;
    }
    public Date getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
    
}
