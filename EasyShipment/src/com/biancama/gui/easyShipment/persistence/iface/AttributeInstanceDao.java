package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.AttributeInstance;

public interface AttributeInstanceDao {
    
    Integer getAttributeInstance(AttributeInstance attributeInstance) throws SQLException;

    Integer getAttributeId(AttributeInstance attributeInstance) throws SQLException;
    
    Integer getAttributeSetId(AttributeInstance attributeInstance) throws SQLException;
    
    boolean insert(AttributeInstance attributeInstance) throws SQLException;
}
