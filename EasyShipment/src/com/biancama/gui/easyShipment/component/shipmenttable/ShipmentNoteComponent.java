package com.biancama.gui.easyShipment.component.shipmenttable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.biancama.gui.easyShipment.components.BiancaTextAreaComponent;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;
import com.biancama.gui.swing.actions.ActionController;

public class ShipmentNoteComponent extends BiancaTextAreaComponent {

    
    /**
     * 
     */
    private static final long serialVersionUID = 1700464461045998500L;
    
    public ShipmentNoteComponent(String identifier){
        super(identifier, null, 2);
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
