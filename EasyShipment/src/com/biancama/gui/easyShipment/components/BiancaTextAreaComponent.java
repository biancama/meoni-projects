package com.biancama.gui.easyShipment.components;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.KeyStroke;
import javax.swing.event.DocumentListener;

import com.biancama.gui.swing.components.BiancaTextArea;

public abstract class BiancaTextAreaComponent extends BiancaComponentAbstract {

    /**
     * 
     */
    private static final long serialVersionUID = 2826776654214796896L;
    protected BiancaTextArea field;
    
    public BiancaTextAreaComponent(String identifier){
        this(identifier, null, 1);
        
    }
    public BiancaTextAreaComponent(String identifier, String value){
        this(identifier, value, 1);
               
    }
    
    public BiancaTextAreaComponent(String identifier,String value, int rows){
        super(identifier);
        field = new BiancaTextArea();
        field.setName(identifier);
        field.setText(value); 
        field.setRows(rows);
        // change Default Tab Beahivour
        changeDefaultTabBehviour();
    }
    private void changeDefaultTabBehviour() {
        Set<KeyStroke> strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
        field.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
        strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
        field.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);

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
    public void removeActionListener(DocumentListener l) {
        field.getDocument().removeDocumentListener(l);
    }
}
