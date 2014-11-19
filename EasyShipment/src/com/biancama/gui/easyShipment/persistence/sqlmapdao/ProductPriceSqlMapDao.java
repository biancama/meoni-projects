package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.biancama.gui.easyShipment.model.ProductPrice;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.ProductPriceDao;

public class ProductPriceSqlMapDao implements ProductPriceDao {

    @Override
    public boolean insertProductPrice(ProductPrice productPrice) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("insertProductPrice", productPrice);
        return true;
    }

    @Override
    public BigDecimal getProductPrice(String productName, Integer priceVersionId) throws SQLException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("productName", productName);
        parameterMap.put("priceVersionId", priceVersionId);
        
        return (BigDecimal) DaoConfig.getInstance().getSqlMapper().queryForObject("getProductPrice", parameterMap);

    }

}
