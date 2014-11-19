package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.Product;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.ProductDao;
import com.biancama.log.BiancaLogger;

public class ProductSqlMapDao implements ProductDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<Product> getAllProducts() {
        List<Product> result = null;
        try {
            result = DaoConfig.getInstance().getSqlMapper().queryForList("getProductList");
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query all products: " + e);
        }
        return result;

    }

    @Override
    public Product fillWithPrice(Product p) throws SQLException {
        return (Product) DaoConfig.getInstance().getSqlMapper().queryForObject("getProduct", p);
    }

    @Override
    public Product findById(Integer id) throws SQLException {
        return (Product) DaoConfig.getInstance().getSqlMapper().queryForObject("getProductById", id);
    }

    @Override
    public String getLot(Integer attributeInstance) throws SQLException {      
        return  (String) DaoConfig.getInstance().getSqlMapper().queryForObject("getLotById", attributeInstance);
    }

}
