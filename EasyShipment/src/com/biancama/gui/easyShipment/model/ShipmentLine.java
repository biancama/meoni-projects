package com.biancama.gui.easyShipment.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShipmentLine extends Organization implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5146359842058113337L;
    private Integer id;
    private Integer idB;
    private BigDecimal quantity;
    private String lot;
    private Product product;
    private BigDecimal quantityB;
    
    private Integer line;
    private Integer shipmentId;
    private Integer parentIdB;
    private Integer orderLineId;
    private Integer locatorId;
    private Integer productId;
    private Integer uomId;
    private Integer attributeistanceId;
    // price for order line
    private BigDecimal price;

    public boolean isEmpty(){
        return false;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public String getLot() {
        return lot;
    }
    public void setLot(String lot) {
        this.lot = lot;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public BigDecimal getQuantityB() {
        return quantityB;
    }
    public void setQuantityB(BigDecimal quantityB) {
        this.quantityB = quantityB;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Integer getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(Integer orderLineId) {
        this.orderLineId = orderLineId;
    }

    public Integer getLocatorId() {
        return locatorId;
    }

    public void setLocatorId(Integer locatorId) {
        this.locatorId = locatorId;
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

    public Integer getAttributeistanceId() {
        return attributeistanceId;
    }

    public void setAttributeistanceId(Integer attributeistanceId) {
        this.attributeistanceId = attributeistanceId;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getIdB() {
        return idB;
    }

    public void setIdB(Integer idB) {
        this.idB = idB;
    }

    public Integer getParentIdB() {
        return parentIdB;
    }

    public void setParentIdB(Integer parentIdB) {
        this.parentIdB = parentIdB;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }
    
    
}
