package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.PrintOutShipment;
import com.biancama.gui.easyShipment.model.PrintOutShipmentLine;

public interface PrintOutShipmentDao {
    PrintOutShipment getShipment(Integer id) throws SQLException;
    List<PrintOutShipmentLine>  getShipmentLine(Integer shipmentId) throws SQLException;
}
