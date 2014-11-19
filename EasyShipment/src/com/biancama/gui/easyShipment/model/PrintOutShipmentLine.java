package com.biancama.gui.easyShipment.model;

import java.io.Serializable;

public class PrintOutShipmentLine implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 3072738504888675855L;
    private Integer id;
    private String qty;
    private String uom; 
    private String productName;
    private String lot;
    private Integer shipmentId;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getQty() {
        return qty;
    }
    public void setQty(String qty) {
        this.qty = qty;
    }
    public String getUom() {
        return uom;
    }
    public void setUom(String uom) {
        this.uom = uom;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getLot() {
        return lot;
    }
    public void setLot(String lot) {
        this.lot = lot;
    }
    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }
    public Integer getShipmentId() {
        return shipmentId;
    }
    
}
