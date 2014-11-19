package com.biancama.gui.easyShipment.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Product extends Organization implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8597877682919913813L;
    private Integer id;
    private String name;
    private String value;
    private String description;
    private BigDecimal price;
    private Integer priceListId;
    private Date versionLastPriceList;
    private Integer taxId;
    private Integer uomId;
    private Integer locatorId;  
    private Date validFrom;
    private BigDecimal rate;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setVersionLastPriceList(Date versionLastPriceList) {
        this.versionLastPriceList = versionLastPriceList;
    }
    public Date getVersionLastPriceList() {
        return versionLastPriceList;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPriceListId(Integer priceListId) {
        this.priceListId = priceListId;
    }
    public Integer getPriceListId() {
        return priceListId;
    }
    public void setTaxId(Integer taxId) {
        this.taxId = taxId;
    }
    public Integer getTaxId() {
        return taxId;
    }
    public void setUomId(Integer uomId) {
        this.uomId = uomId;
    }
    public Integer getUomId() {
        return uomId;
    }
    public void setLocatorId(Integer locatorId) {
        this.locatorId = locatorId;
    }
    public Integer getLocatorId() {
        return locatorId;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
    public Date getValidFrom() {
        return validFrom;
    }
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    public BigDecimal getRate() {
        return rate;
    }
    
    
}
