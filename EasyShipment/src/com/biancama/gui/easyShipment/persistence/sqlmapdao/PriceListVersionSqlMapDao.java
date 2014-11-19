package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.biancama.gui.easyShipment.model.PriceListVersion;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.PriceListVersionDao;

public class PriceListVersionSqlMapDao implements PriceListVersionDao {

    @Override
    public PriceListVersion getPriceListVersion(PriceListVersion priceListVersion) throws SQLException {
        return (PriceListVersion) DaoConfig.getInstance().getSqlMapper().queryForObject("getPriceListVesionByDate", priceListVersion);
    }

    @Override
    public boolean insert(PriceListVersion priceListVersion) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("insertPriceListVersion", priceListVersion);
        return true;
    }

    @Override
    public Integer getPriceVersionIdByCustomer(String customerName, Date validFrom) throws SQLException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("customerName", customerName);
        parameterMap.put("validFrom", validFrom);
        
        Integer id =  (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("selectPriceListVersionByCustomer", parameterMap);
        return id;
        
    }

    @Override
    public Integer getPriceVersionIdByGroup(String customerName, Date validFrom) throws SQLException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("customerName", customerName);
        parameterMap.put("validFrom", validFrom);
        
        Integer id =  (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("selectPriceListVersionByGroup", parameterMap);
        return id;
    }

    @Override
    public Integer getPriceVersionIdDefault(Date validFrom) throws SQLException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("validFrom", validFrom);
        
        Integer id =  (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("selectPriceListVersionByDefault", parameterMap);
        return id;
    }

}
