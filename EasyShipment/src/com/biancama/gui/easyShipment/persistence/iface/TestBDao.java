package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.TestB;
import com.biancama.gui.easyShipment.model.TestLineB;

public interface TestBDao {
    
    Integer insert(TestB testB) throws SQLException;
    TestB getTestB(String docNumber) throws SQLException;
    Integer insertLine(TestLineB testLineB) throws SQLException;
    public boolean updateLine(TestLineB testLineB) throws SQLException;
    boolean update(TestB testB) throws SQLException;
    Integer getShipmentLineB(Integer shipmentId) throws SQLException;
}
