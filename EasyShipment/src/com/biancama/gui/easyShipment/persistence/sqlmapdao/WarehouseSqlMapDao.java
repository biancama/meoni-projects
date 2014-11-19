package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.Warehouse;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.WarehouseDao;
import com.biancama.log.BiancaLogger;

public class WarehouseSqlMapDao implements WarehouseDao {

    @Override
    public Integer getDefault() {
        Integer result = null;
        Warehouse dummyWarehouse = new Warehouse(); 
        try {
            result = (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getDefaultWarehouse", dummyWarehouse);
        } catch (SQLException e) {
            BiancaLogger.getLogger().severe("Error query get Default warehouse: " + e);
        }
        
        return result;
    }

}
