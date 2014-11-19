package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.ShipmentLine;

public interface ShipmentLineDao {
    boolean insert(ShipmentLine shipmentLine) throws SQLException;
    boolean update(ShipmentLine shipmentLine) throws SQLException;
    ShipmentLine getShipmentLine(Integer id) throws SQLException;
    List<ShipmentLine> getShipmentLines(Integer shipmentId)  throws SQLException;
    boolean remove(Integer id)  throws SQLException;
    List<Integer> getShipmentLinesIds(Integer shipmentId) throws SQLException;
    void removeShipmentLinesId(List<Integer> shipmentLinesIds) throws SQLException;
}
