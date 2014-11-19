package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.MLocator;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.MLocatorDao;
import com.biancama.log.BiancaLogger;

public class MLocatorSqlMapDao implements MLocatorDao {

    @Override
    public Integer getDefault() {
        Integer result = null;
        MLocator mLocator = new MLocator();        
        try {
            result = (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getDefaultLocator", mLocator);
        } catch (SQLException e) {
            BiancaLogger.getLogger().severe("Error query get Default warehouse: " + e);
        }
        
        return result;
    }

}
