package com.biancama.gui.easyShipment.menus;

import com.biancama.gui.easyShipment.menus.actions.shipment.FindShipmentAction;
import com.biancama.gui.easyShipment.menus.actions.shipment.NewShipmentAction;
import com.biancama.gui.easyShipment.menus.actions.shipment.SaveShipmentAction;
import com.biancama.gui.menu.BiancaStartMenu;

public class ShipmentMenu extends BiancaStartMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 4407809070776485581L;

    public ShipmentMenu() {
        super("gui.menu.shipment", "gui.images.new");
        this.add(new NewShipmentAction());
        this.add(new FindShipmentAction());
        this.add(SaveShipmentAction.getInstance());
    }

}
