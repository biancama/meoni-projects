package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;
import java.util.Date;

import com.biancama.gui.easyShipment.model.PriceListVersion;

public interface PriceListVersionDao {
    public PriceListVersion getPriceListVersion(PriceListVersion priceListVersion) throws SQLException;
    public boolean insert(PriceListVersion priceListVersion) throws SQLException;
    
    public Integer getPriceVersionIdByCustomer(String customerName, Date validFrom) throws SQLException;
    
    public Integer getPriceVersionIdByGroup(String customerName, Date validFrom) throws SQLException;
    
    Integer getPriceVersionIdDefault(Date validFrom) throws SQLException;
}
