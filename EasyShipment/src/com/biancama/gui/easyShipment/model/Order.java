package com.biancama.gui.easyShipment.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Order extends Organization implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3892009099237915218L;
    
    private Integer id;
    private String documentNo;
    private Integer salesRep;
    private Integer bpartnerId;
    private Integer bpartnerLocationId;
    // External from PriceList
    private Integer currencyId;
    private Integer paymentTermId;
    private String invoiceRule;
    private String deliveryRule;
    private String deliveryViaRule;
    private Integer warehouseId;
    private Integer pricelistId;
    private Integer billBPartnerId;
    private Integer billLocationId;
    private Integer billUserId;
    private Integer payBPartnerId;
    private Integer payLocationId;
    private BigDecimal grandTotal;
    private BigDecimal totalLines;
    private Date dateordered;

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }
    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }
    public String getDocumentNo() {
        return documentNo;
    }
    public Integer getSalesRep() {
        return salesRep;
    }
    public void setSalesRep(Integer salesRep) {
        this.salesRep = salesRep;
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
    public Integer getWarehouseId() {
        return warehouseId;
    }
    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
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
    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
    public BigDecimal getGrandTotal() {
        return grandTotal;
    }
    public void setDateordered(Date dateordered) {
        this.dateordered = dateordered;
    }
    public Date getDateordered() {
        return dateordered;
    }
    public void setTotalLines(BigDecimal totalLines) {
        this.totalLines = totalLines;
    }
    public BigDecimal getTotalLines() {
        return totalLines;
    }

    
    
}
