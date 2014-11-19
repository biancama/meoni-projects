package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.Order;

public interface OrderDao {
    boolean insert(Order order) throws SQLException;
    
    Integer getDefaultPaymentTerm() throws SQLException;
    
    boolean updateGrandTotal(Order order) throws SQLException;
    boolean update(Order order) throws SQLException;
    
    Order getOrder(Integer id) throws SQLException;
    
    boolean complete(Integer id) throws SQLException;
}
