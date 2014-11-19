package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.OrderLine;

public interface OrderLineDao {
    boolean insert(OrderLine orderLine) throws SQLException;
    boolean update(OrderLine orderLine) throws SQLException;
    boolean delete(Integer id) throws SQLException;
    void removeOrderLinesIds(List<Integer> shipmentLinesIds) throws SQLException;
    boolean remove(Integer id) throws SQLException;
    
}
