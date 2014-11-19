package com.biancama.gui.easyShipment.persistence.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import com.biancama.gui.easyShipment.persistence.iface.PriceListVersionDao;
import com.biancama.gui.easyShipment.persistence.iface.ProductPriceDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.PriceListVersionSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.ProductPriceSqlMapDao;

public class PriceListService {
   public BigDecimal getListPrice(String customerName, Date shipmentDate, String productName) throws SQLException{
        PriceListVersionDao priceListDao = new PriceListVersionSqlMapDao();
        Integer priceVersionId = priceListDao.getPriceVersionIdByCustomer(customerName, shipmentDate);
        
        if (priceVersionId == null){
            priceVersionId = priceListDao.getPriceVersionIdByGroup(customerName, shipmentDate);
        }

        if (priceVersionId == null){
            priceVersionId = priceListDao.getPriceVersionIdDefault(shipmentDate);
        }
        
        ProductPriceDao productPriceDao = new ProductPriceSqlMapDao();
        
        return productPriceDao.getProductPrice(productName, priceVersionId);
        
    }
}
