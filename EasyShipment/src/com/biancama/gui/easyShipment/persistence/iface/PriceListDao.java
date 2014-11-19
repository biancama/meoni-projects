package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.PriceList;

public interface PriceListDao {
    public PriceList getPriceListByName(String name) throws SQLException;
}
