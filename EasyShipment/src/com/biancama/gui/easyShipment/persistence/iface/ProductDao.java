package com.biancama.gui.easyShipment.persistence.iface;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.Product;

public interface ProductDao {
    List<Product> getAllProducts();
    
    Product fillWithPrice(Product p) throws SQLException;
    
    Product findById(Integer id) throws SQLException;
    
    String getLot(Integer attributeInstance) throws SQLException;
}
