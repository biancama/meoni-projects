package com.biancama.gui.easyShipment.components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.biancama.gui.easyShipment.model.Product;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.ProductSqlMapDao;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;

public class ProductComponent extends BiancaComboComponent<Product> { 
    

    /**
     * 
     */
    private static final long serialVersionUID = 8532298384334232267L;
    
    public ProductComponent(){
        this(null);
        this.addItemListener(new ItemListener() {
          
          @Override
          public void itemStateChanged(ItemEvent e) {
              ShipmentPanel.getInstance().onUpdating();
          }
      });
    }
    
    public ProductComponent(String product){
        super(product, (new ProductSqlMapDao()).getAllProducts());   
    }
    @Override
    public String getValueToDisplay(Product t) {
       return t.getName();
    }

    @Override
    public String getKeyValue(Product t) {
        return t.getValue();
    }
 }
