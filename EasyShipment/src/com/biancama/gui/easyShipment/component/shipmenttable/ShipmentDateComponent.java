package com.biancama.gui.easyShipment.component.shipmenttable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.biancama.gui.easyShipment.components.BiancaFormattedTextComponent;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;
import com.biancama.gui.swing.components.BiancaFormattedTextField;

public class ShipmentDateComponent extends BiancaFormattedTextComponent {

    
    /**
     * 
     */
    private static final long serialVersionUID = 1700464461045998500L;
    
    public void reset(){
       
        this.field.setValue(new Date());
    }
    
       public ShipmentDateComponent(String identifier){
           super(identifier, new SimpleDateFormat("d/M/yyyy"));
           reset();
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
