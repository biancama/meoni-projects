package com.biancama.gui.easyShipment.menus.actions.shipment;

import java.awt.event.ActionEvent;

import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.model.Shipment;
import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;
import com.biancama.gui.swing.actions.ToolBarAction;
import com.biancama.utils.locale.BiancaL;

public class SaveShipmentAction extends ToolBarAction {

    /**
     * 
     */
    private static final long serialVersionUID = -3278645338313169616L;
    public static final String LOCALE_PREFIX = "com.biancama.gui.easyShipment.views.shipment";

    private static SaveShipmentAction instance;
    
    public static synchronized SaveShipmentAction getInstance(){
        if (instance == null){
            instance = new SaveShipmentAction();
        }
        return instance;
    }
    
    private SaveShipmentAction(){
        super("action.shipment.save", "gui.images.save");
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initDefaults() {
        // TODO Auto-generated method stub

    }
    
    @Override
    public void onAction(ActionEvent e) {
        Shipment currentShipment = ShipmentPanel.getInstance().getShipment();
        boolean isSaved = ShipmentPanel.getInstance().saveShipment(currentShipment);
        if (isSaved){
            ShipmentPanel.getInstance().onSaving();
            int prevCountDown = UserIO.getInstance().getCountdownTime();
            UserIO.getInstance().setCountdownTime(2);
            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.saved", "Shipment Saved"));
            UserIO.getInstance().setCountdownTime(prevCountDown);
           
        }else{
            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.notsaved", "error: Shipment not saved !"));
        }
    }

}
