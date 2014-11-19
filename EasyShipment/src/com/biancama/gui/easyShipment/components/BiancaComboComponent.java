package com.biancama.gui.easyShipment.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

public abstract class BiancaComboComponent<T> extends BiancaComponentAbstract {
    Map<String, String> entryMap; 
    /**
     * 
     */
    private static final long serialVersionUID = -1322757689801163917L;
    private JComboBox combo;
    @Override
    public Component getComponent() {
        return combo;
    }
    
    public BiancaComboComponent(String identifier, List<T> entries ){
        this(identifier, null,entries);
    }
    public BiancaComboComponent(String identifier){
        super(identifier);
    }
    public BiancaComboComponent(String identifier, String value, List<T> entries ){
        this(identifier);
        entryMap= new HashMap<String, String>();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("");
        if (entries != null){
            for (T t : entries) {
                model.addElement(getValueToDisplay(t));
                // fill entry
                entryMap.put(getKeyValue(t).toUpperCase(), getValueToDisplay(t));
            }            
        }
        if (value != null){
            model.setSelectedItem(value);
        }else{
            model.setSelectedItem("");
        }
        combo = new JComboBox(model);
        combo.setEditable(true);
        combo.setName(identifier);
        AbstractAction doNothing = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
            
        };
        combo.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
                                    "doNothing");
        combo.getActionMap().put("doNothing",
                                     doNothing);

        JTextField jtf = (JTextField)combo.getEditor().getEditorComponent();
        jtf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
                                    "doNothing");
        jtf.getActionMap().put("doNothing",
                                     doNothing);
 
        jtf.addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
               
                
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField jtf = (JTextField)combo.getEditor().getEditorComponent();
                String s = jtf.getText();
                jtf.setText(FindAutoCompleteText(s));             
               
            }
            
        });
        
        jtf.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\t'){
                    JTextField jtf = (JTextField)combo.getEditor().getEditorComponent();
                    String s = jtf.getText();
                    jtf.setText(FindAutoCompleteText(s));                  
                }
                
                
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
        
    }


   public void reloadEntries(List<T> entries ){
        if (entries == null){
            return;
        }
        entryMap= new HashMap<String, String>();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        boolean isFirst = true;
        for (T t : entries) {
            model.addElement(getValueToDisplay(t));
            // fill entry
            entryMap.put(getKeyValue(t).toUpperCase(), getValueToDisplay(t));
        }
         if (model.getSize() == 0){
             return;
         }
        combo.setSelectedIndex(0);
        combo.setModel(model);
        
    }

    
    
    private String FindAutoCompleteText(String s)
    //________________________________________________________________________
    {   
        if (s == null || s.trim().equals("")){
            combo.setSelectedIndex(0);
            return "";
        }
        String value = entryMap.get(s.toUpperCase());
        if (value != null){
            combo.setSelectedItem(value);
            return value;
        }
        for (String key : entryMap.keySet()) {
            if ( Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(key).find()){
                combo.setSelectedItem(entryMap.get(key));
                return entryMap.get(key);
            }
        }
        
        
        for( int i = 0; i < combo.getItemCount(); i++ )
        {   
            Object o = combo.getItemAt( i );
            String sTemp = o.toString();
            // Don't do anything if the text exactly matches...
            if(sTemp.equals(s))
            {   
                return sTemp;
            }
            if ( Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(sTemp).find())
            {
                combo.setSelectedIndex(i);
                JTextComponent jtc = (JTextComponent)combo.getEditor().getEditorComponent();
                // Insert the suggested text
                jtc.setText(sTemp);
                // Select the inserted text from the end to the current
                // edit position.
                Caret c = jtc.getCaret();
                c.setDot(sTemp.length());
                c.moveDot(s.length());
                return sTemp;
            }
        }
            

            combo.setSelectedIndex(0);
            return "";

    }

    public abstract String getValueToDisplay(T t);
    public abstract String getKeyValue(T t);

    public String getValue(){
        return (String) combo.getSelectedItem();
    }
    
    public void setValue(String value){
        combo.setSelectedItem(value);
    }
    public String getKey(){
        for (String key : entryMap.keySet()) {
            if (entryMap.get(key).equals(this.getValue())){
                return key;
            }
        }
        return null;
    }
    public void addItemListener(ItemListener l) {
        combo.addItemListener(l);
    }
    public void removeItemListener(ItemListener l) {
        combo.removeItemListener(l);
    }
}
