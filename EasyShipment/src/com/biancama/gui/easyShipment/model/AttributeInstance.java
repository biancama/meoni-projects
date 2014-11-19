package com.biancama.gui.easyShipment.model;

public class AttributeInstance extends Organization {
    private Integer id;
    private String value;
    private Integer attributeId;
    private Integer attributeSetId;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Integer getAttributeId() {
        return attributeId;
    }
    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }
    public void setAttributeSetId(Integer attributesetId) {
        this.attributeSetId = attributesetId;
    }
    public Integer getAttributeSetId() {
        return attributeSetId;
    }
    
    
}
