package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;
import java.util.Date;

import com.biancama.gui.easyShipment.model.Shipment;

public interface ShipmentDao {
    String getDocumentNo(Date shipmentDate);
    
    boolean insert(Shipment shipment) throws SQLException;
    boolean update(Shipment shipment) throws SQLException;
    Shipment getShipmentFromDocumentNo(String docNumber, Date d) throws SQLException; 
    Shipment getShipmentFromId(Integer id) throws SQLException; 
    boolean complete(Integer id) throws SQLException; 
} 
