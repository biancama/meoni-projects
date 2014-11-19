package com.biancama.gui.easyShipment.components;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.event.DocumentListener;

import com.biancama.gui.swing.components.BiancaTextField;

public abstract class BiancaTextComponent extends BiancaComponentAbstract {

    /**
     * 
     */
    private static final long serialVersionUID = 2826776654214796896L;
    protected BiancaTextField field;
    
    public BiancaTextComponent(String identifier){
        super(identifier);
        field = new BiancaTextField();
        field.setName(identifier);
        
    }
    public BiancaTextComponent(String identifier, String value){
        this(identifier);
        field.setText(value);        
    }

    @Override
    public Component getComponent() {       
        return field;
    }
    
    public String getValue(){
        return field.getText();
    }

    public void setValue(String value){
        field.setText(value);
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
