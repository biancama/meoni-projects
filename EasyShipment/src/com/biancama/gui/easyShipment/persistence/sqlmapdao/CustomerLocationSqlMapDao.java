package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.CustomerLocation;
import com.biancama.gui.easyShipment.model.Warehouse;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.CustomerLocationDao;
import com.biancama.log.BiancaLogger;

public class CustomerLocationSqlMapDao implements CustomerLocationDao {

    @SuppressWarnings("unchecked")
    @Override
    public String getCustomerShipmentLocation(int bpartnerLocationId) {
        String result = null;
        CustomerLocation dummyCustomerLocation = new CustomerLocation(); 
        dummyCustomerLocation.setId(bpartnerLocationId);
        try {
            result = (String) DaoConfig.getInstance().getSqlMapper().queryForObject("getCustomerLocationList", dummyCustomerLocation);
        } catch (SQLException e) {
            BiancaLogger.getLogger().severe("Error query get Default warehouse: " + e);
        }
        
 
            return result;
  
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerLocation> getAllCustomerShipmentLocations(String customer) {
        
        List<CustomerLocation> result = null;
        CustomerLocation dummyCustomerLocation = new CustomerLocation(); 
        dummyCustomerLocation.setCustomer(customer);
        try {
            result = DaoConfig.getInstance().getSqlMapper().queryForList("getAllCustomerShipmentLocations", dummyCustomerLocation);
        } catch (SQLException e) {
            BiancaLogger.getLogger().severe("Error query get Default warehouse: " + e);
        }
        
       
        return result;
       
    }

}
