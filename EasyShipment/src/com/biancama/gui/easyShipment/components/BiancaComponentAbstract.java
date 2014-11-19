package com.biancama.gui.easyShipment.components;


public abstract class BiancaComponentAbstract implements BiancaComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 2116583882212317702L;
    protected final String identifier;
    public BiancaComponentAbstract(String identifier) {
        super();
        this.identifier = identifier;
    }
    
    public String getIdentifier(){
        return identifier;
    }
}
