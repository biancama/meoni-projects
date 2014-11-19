package com.biancama.gui.easyShipment.persistence.iface;

import java.util.List;

import com.biancama.gui.easyShipment.model.CustomerLocation;


public interface CustomerLocationDao {
   String getCustomerShipmentLocation(int bpartnerLocationId);
    
    List<CustomerLocation> getAllCustomerShipmentLocations(String customer);
    
}
