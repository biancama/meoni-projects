package com.biancama.gui.easyShipment.persistence.iface;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.ProductPrice;

public interface ProductPriceDao {
    public boolean insertProductPrice(ProductPrice productPrice) throws SQLException;
    
    public BigDecimal getProductPrice(String productName, Integer priceVersionId) throws SQLException;
}
