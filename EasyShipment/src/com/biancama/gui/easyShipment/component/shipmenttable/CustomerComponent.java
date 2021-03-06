package com.biancama.gui.easyShipment.component.shipmenttable;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.biancama.gui.easyShipment.components.BiancaComboComponent;
import com.biancama.gui.easyShipment.model.Customer;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.CustomerSqlMapDao;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;

public class CustomerComponent extends BiancaComboComponent<Customer> {

    /**
     * 
     */
    private static final long serialVersionUID = 8532298384334232267L;

 
    public CustomerComponent(String identifier){
      this(identifier, null);
      this.addItemListener(new ItemListener() {
        
        @Override
        public void itemStateChanged(ItemEvent e) {
            ShipmentPanel.getInstance().onUpdating();
        }
    });
    }
    
    public CustomerComponent(String identifier, String customer){
        super(identifier, customer, (new CustomerSqlMapDao()).getAllCustomers());        
    }

    @Override
    public String getValueToDisplay(Customer t) {
        return t.getName();
    }

    @Override
    public String getKeyValue(Customer t) {
        return t.getValue();
    }
 
}
