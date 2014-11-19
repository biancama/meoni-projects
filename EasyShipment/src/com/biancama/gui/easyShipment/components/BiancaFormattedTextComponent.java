package com.biancama.gui.easyShipment.components;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.text.Format;

import javax.swing.event.DocumentListener;

import com.biancama.gui.swing.components.BiancaFormattedTextField;
import com.biancama.gui.swing.components.BiancaTextField;

public abstract class BiancaFormattedTextComponent extends BiancaComponentAbstract {

    /**
     * 
     */
    private static final long serialVersionUID = 2826776654214796896L;
    protected BiancaFormattedTextField field;
    public BiancaFormattedTextComponent(String identifier){
        super(identifier);
    }
   
    public BiancaFormattedTextComponent(String identifier, String value){        
        this(identifier);
        field.setText(value);        
    }
    public BiancaFormattedTextComponent(String identifier, Format value){
        this(identifier);
        field =  new BiancaFormattedTextField(value);       
        field.setName(identifier);

    }
    @Override
    public Component getComponent() {       
        return field;
    }
    
    public Object getValue(){
        return field.getValue();
    }

    public void setValue(Object value){
        field.setValue(value);
    }
    public void addDocumentListener(DocumentListener l) {
        field.getDocument().addDocumentListener(l);
    }
    public void removeDocumentListener(DocumentListener l) {
        field.getDocument().removeDocumentListener(l);
    }

    public void addActionListener(ActionListener l) {
        field.addActionListener(l);
    }
    public void removeActionListener(ActionListener l) {
        field.removeActionListener(l);
    }
    
    public void addFocusListener(FocusListener l) {
        field.addFocusListener(l);
    }
    public void removeFocusListener(FocusListener l) {
        field.removeFocusListener(l);
    }
}
