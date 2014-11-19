package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;
import java.util.Date;

public interface SequenceDao {
    int getNextId(String name) throws SQLException;
    int getNextDocNumber(String name, Date sequenceDate) throws SQLException;
    void updateDocNumber(String docNumber, String sequenceName, Date date) throws SQLException;
}
