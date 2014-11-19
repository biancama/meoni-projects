package com.biancama.gui.easyShipment.component.shipmenttable;



import com.biancama.gui.easyShipment.views.ViewToolbar;
import com.biancama.gui.swing.actions.ToolBarAction;



public class ShipmentToolbar extends ViewToolbar {

    /**
     * 
     */
    private static final long serialVersionUID = -2937265663133870302L;

    public ShipmentToolbar() {
        super("action.shipment.save", "action.shipment.print");
    }
    
    @Override
    public String getButtonConstraint(int i, ToolBarAction action) {
        if (i < 3) {
            return "dock west, sizegroup toolbar, gapright " + (i == 1 ? "10" : "5");
        } else {
            return "dock east, sizegroup toolbar, gapright 3";
        }
    }

}
