package com.biancama.gui.easyShipment.component.shipmenttable;

import java.util.Date;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.biancama.gui.easyShipment.components.BiancaTextComponent;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.ShipmentSqlMapDao;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;

public class ShipmentComponent extends BiancaTextComponent {

    
    /**
     * 
     */
    private static final long serialVersionUID = 1700464461045998500L;
    
    public ShipmentComponent(String identifier){
        super(identifier, (new ShipmentSqlMapDao()).getDocumentNo(new Date()));
        this.addDocumentListener(new DocumentListener() {
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                enableSaveButton();
                
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSaveButton();
                
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                // TODO Auto-generated method stub
                
            }
            private void enableSaveButton(){
                ShipmentPanel.getInstance().onUpdating();
            }
        });
            
       
    }
    
    
}
