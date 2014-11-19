package com.biancama.gui.easyShipment.model;

import java.io.Serializable;

public class Customer extends Organization implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3068060720923097408L;
    private String value;
    private Integer id;
    private String name;
    private Integer salesRep;
    private Integer locationId;
    private Integer paymentTermId;
    private String invoiceRule;
    private String deliveryRule;
    private String deliveryViaRule;
    private Integer pricelistId;
    private Integer billBPartnerId;
    private Integer billLocationId;
    private Integer billUserId;
    private Integer payBPartnerId;
    private Integer payLocationId;
    private Integer currencyId;

    
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
    public Integer getSalesRep() {
        return salesRep;
    }
    public void setSalesRep(Integer salesRep) {
        this.salesRep = salesRep;
    }
    public Integer getPaymentTermId() {
        return paymentTermId;
    }
    public void setPaymentTermId(Integer paymentTermId) {
        this.paymentTermId = paymentTermId;
    }
    public String getInvoiceRule() {
        return invoiceRule;
    }
    public void setInvoiceRule(String invoiceRule) {
        this.invoiceRule = invoiceRule;
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
    public Integer getPricelistId() {
        return pricelistId;
    }
    public void setPricelistId(Integer pricelistId) {
        this.pricelistId = pricelistId;
    }
    public Integer getBillBPartnerId() {
        return billBPartnerId;
    }
    public void setBillBPartnerId(Integer billBPartnerId) {
        this.billBPartnerId = billBPartnerId;
    }
    public Integer getBillLocationId() {
        return billLocationId;
    }
    public void setBillLocationId(Integer billLocationId) {
        this.billLocationId = billLocationId;
    }
    public Integer getBillUserId() {
        return billUserId;
    }
    public void setBillUserId(Integer billUserId) {
        this.billUserId = billUserId;
    }
    public Integer getPayBPartnerId() {
        return payBPartnerId;
    }
    public void setPayBPartnerId(Integer payBPartnerId) {
        this.payBPartnerId = payBPartnerId;
    }
    public Integer getPayLocationId() {
        return payLocationId;
    }
    public void setPayLocationId(Integer payLocationId) {
        this.payLocationId = payLocationId;
    }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    public Integer getLocationId() {
        return locationId;
    }
    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
    public Integer getCurrencyId() {
        return currencyId;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    } 
    
    
    

}
