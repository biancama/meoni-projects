package com.biancama.gui.easyShipment.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderLine extends Organization implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3892009099237915218L;
    
    private Integer id;
    private Integer orderId;
    private Integer bpartnerId;
    private Integer bpartnerLocationId;
    // External from PriceList
    private Integer currencyId;
    private Integer warehouseId;
    private Integer line;
    private Integer productId;
    private Integer uomId;
    private BigDecimal qty;
    private BigDecimal price;
    private Integer taxId;
    private BigDecimal rate;
    
    public BigDecimal getLinenetamt(){
        if (price != null && qty != null){
            return price.multiply(qty);            
        } else {
            return new BigDecimal(0);
        }
    }
    
    public void setLinenetamt(BigDecimal value){}

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }
    public Integer getBpartnerId() {
        return bpartnerId;
    }
    public void setBpartnerId(Integer bpartnerId) {
        this.bpartnerId = bpartnerId;
    }
    public Integer getBpartnerLocationId() {
        return bpartnerLocationId;
    }
    public void setBpartnerLocationId(Integer bpartnerLocationId) {
        this.bpartnerLocationId = bpartnerLocationId;
    }
    public Integer getCurrencyId() {
        return currencyId;
    }
    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
    public Integer getWarehouseId() {
        return warehouseId;
    }
    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }
    public Integer getLine() {
        return line;
    }
    public void setLine(Integer line) {
        this.line = line;
    }
    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    public Integer getUomId() {
        return uomId;
    }
    public void setUomId(Integer uomId) {
        this.uomId = uomId;
    }
    public BigDecimal getQty() {
        return qty;
    }
    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Integer getTaxId() {
        return taxId;
    }
    public void setTaxId(Integer taxId) {
        this.taxId = taxId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }
    
    
}
