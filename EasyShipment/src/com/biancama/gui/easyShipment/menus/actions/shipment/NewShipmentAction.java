package com.biancama.gui.easyShipment.menus.actions.shipment;

import java.awt.event.ActionEvent;

import com.biancama.gui.easyShipment.views.shipment.ShipmentView;
import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.actions.ThreadedAction;
import com.biancama.gui.swing.actions.ToolBarAction;

public class NewShipmentAction extends ToolBarAction {

    /**
     * 
     */
    private static final long serialVersionUID = -7007510485582293971L;

    public NewShipmentAction() {
        super("action.shipment.new", "gui.images.new");
    }

    @Override
    public void onAction(ActionEvent e) {
       ShipmentView shipmentView = ShipmentView.getInstance();
//        SwingGui.getInstance().setContent(shipmentView);
       shipmentView.resetPanel();
       SwingGui.getInstance().setContent(shipmentView);
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initDefaults() {
        // TODO Auto-generated method stub

    }

}
