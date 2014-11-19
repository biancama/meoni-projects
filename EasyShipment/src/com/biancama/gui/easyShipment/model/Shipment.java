package com.biancama.gui.easyShipment.model;

import java.io.Serializable;
import java.util.Date;

public class Shipment extends Organization implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -7948109699867112946L;
    private Integer id;
    private String documentNo;
    private Integer orderId;
    private Integer bpartnerId;
    private Integer bpartnerLocationId;
    private Integer warehouseId;
    private String deliveryRule;
    private String deliveryViaRule;
    private Integer salesRep;
    private String description;
    private String calendarYear;
    private Integer shipperId;
    private Integer itemNo;
    private Integer idB;
    private String docStatus;
    private Date shipmentDate;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDocumentNo() {
        return documentNo;
    }
    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }
    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
    public Integer getWarehouseId() {
        return warehouseId;
    }
    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }
    public String getDeliveryRule() {
        return deliveryRule;
    }
    public void setDeliveryRule(String deliveryRule) {
        this.deliveryRule = deliveryRule;
    }
    public String getDeliveryViaRule() {
        return deliveryViaRule;
    }
    public void setDeliveryViaRule(String deliveryViaRule) {
        this.deliveryViaRule = deliveryViaRule;
    }
    public Integer getSalesRep() {
        return salesRep;
    }
    public void setSalesRep(Integer salesRep) {
        this.salesRep = salesRep;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setCalendarYear(String calendarYear) {
        this.calendarYear = calendarYear;
    }
    public String getCalendarYear() {
        return calendarYear;
    }
    public void setShipperId(Integer shipperId) {
        this.shipperId = shipperId;
    }
    public Integer getShipperId() {
        return shipperId;
    }
    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }
    public Integer getItemNo() {
        return itemNo;
    }
    public void setIdB(Integer idB) {
        this.idB = idB;
    }
    public Integer getIdB() {
        return idB;
    }
    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }
    public String getDocStatus() {
        return docStatus;
    }
    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }
    public Date getShipmentDate() {
        return shipmentDate;
    }

    
}
