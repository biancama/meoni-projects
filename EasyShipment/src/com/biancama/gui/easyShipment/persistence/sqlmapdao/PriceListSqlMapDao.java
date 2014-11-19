package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.PriceList;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.PriceListDao;

public class PriceListSqlMapDao implements PriceListDao {

    @Override
    public PriceList getPriceListByName(String name) throws SQLException {
        return (PriceList) DaoConfig.getInstance().getSqlMapper().queryForObject("getPriceListByName", name);
    }

}
