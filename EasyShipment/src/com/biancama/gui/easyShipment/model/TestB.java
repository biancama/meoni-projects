package com.biancama.gui.easyShipment.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestB {
    private Integer id;
    private Integer shipmentId;
    private Date dateOrdered;
    private String documentNo;
    private String customer;
    
    public String getCalendarYear(){
        if (dateOrdered == null){
            return null;
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String calendarYear = sdf.format(dateOrdered);
            return calendarYear;
        }
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getShipmentId() {
        return shipmentId;
    }
    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }
    public Date getDateOrdered() {
        return dateOrdered;
    }
    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }
    public String getDocumentNo() {
        return documentNo;
    }
    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    
    
}
