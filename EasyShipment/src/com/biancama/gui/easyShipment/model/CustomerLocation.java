package com.biancama.gui.easyShipment.model;

import java.io.Serializable;

public class CustomerLocation extends Organization implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 7841967027068995646L;

    private Integer id;
    private Integer customerId;
    private boolean isShipAddress;
    private String fullAddress;
    private String customer;
    
    
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public boolean isShipAddress() {
        return isShipAddress;
    }
    public void setShipAddress(boolean isShipAddress) {
        this.isShipAddress = isShipAddress;
    }
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
    public String getFullAddress() {
        return fullAddress;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public String getCustomer() {
        return customer;
    }
    
    
}
