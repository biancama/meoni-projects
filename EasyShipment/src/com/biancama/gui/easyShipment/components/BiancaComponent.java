package com.biancama.gui.easyShipment.components;

import java.awt.Component;
import java.io.Serializable;

public interface BiancaComponent extends Serializable{
    
    public Component getComponent();
    
    public String getIdentifier();
    
}
