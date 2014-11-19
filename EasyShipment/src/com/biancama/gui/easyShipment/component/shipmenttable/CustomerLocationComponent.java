package com.biancama.gui.easyShipment.component.shipmenttable;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.biancama.gui.easyShipment.components.BiancaComboComponent;
import com.biancama.gui.easyShipment.model.Customer;
import com.biancama.gui.easyShipment.model.CustomerLocation;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.CustomerLocationSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.CustomerSqlMapDao;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;

public class CustomerLocationComponent extends BiancaComboComponent<CustomerLocation> {

    /**
     * 
     */
    private static final long serialVersionUID = 8532298384334232267L;

 

    public CustomerLocationComponent(String identifier){
      super(identifier, null);
      this.addItemListener(new ItemListener() {
        
        @Override
        public void itemStateChanged(ItemEvent e) {
            ShipmentPanel.getInstance().onUpdating();
        }
    });
    }
    
    public CustomerLocationComponent(String fullAddress, String customer){
        super(fullAddress, null);        
    }
    
    

    @Override
    public String getValueToDisplay(CustomerLocation t) {
        return t.getFullAddress();
    }

    @Override
    public String getKeyValue(CustomerLocation t) {
        return String.valueOf(t.getId());
    }
    
    public void reload(String customer){
        reloadEntries((new CustomerLocationSqlMapDao()).getAllCustomerShipmentLocations(customer));
    }
 
}
