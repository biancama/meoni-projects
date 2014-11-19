package com.biancama.gui.easyShipment.component.shipmenttable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.biancama.gui.easyShipment.components.BiancaTextComponent;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;
import com.biancama.utils.gui.ValidatorUtils;

public class ItemNoComponent extends BiancaTextComponent {

    
    /**
     * 
     */
    private static final long serialVersionUID = 1700464461045998500L;
    
       public ItemNoComponent(String identifier){
           super(identifier);
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
                   // NOTHING
               }
               private void enableSaveButton(){
                   ShipmentPanel.getInstance().onUpdating();
               }
           });
 
       }
       
    
    
}
