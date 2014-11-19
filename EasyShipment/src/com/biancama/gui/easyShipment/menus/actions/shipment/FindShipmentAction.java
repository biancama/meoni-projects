package com.biancama.gui.easyShipment.menus.actions.shipment;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.model.Shipment;
import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.easyShipment.persistence.service.ShipmentService;
import com.biancama.gui.easyShipment.views.shipment.ShipmentView;
import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.actions.ToolBarAction;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.locale.BiancaL;

public class FindShipmentAction extends ToolBarAction {

    /**
     * 
     */
    private static final long serialVersionUID = -7007510485582293971L;
    private static final String LOCALE_PREFIX = "com.biancama.gui.easyShipment.menus.actions.shipment";

    public FindShipmentAction() {
        super("action.shipment.find", "gui.images.load");
    }

    @Override
    public void onAction(ActionEvent e) {
       String inputUser = UserIO.getInstance().requestInputDialog(0, BiancaL.L(LOCALE_PREFIX + ".insertShipmentNumber", "Insert Shipment Number"), "0");
       // test if it's an integer
       
       ShipmentService shipmentService = new ShipmentService();
       Shipment shipment = null;
       try {
        shipment = shipmentService.getShipment(inputUser, new Date());
    } catch (SQLException e1) {
       BiancaLogger.getLogger().severe("Error in searching shipment " + e1);
    }
    if (shipment == null){
        UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.NO_CANCEL_OPTION, BiancaL.L(LOCALE_PREFIX + ".noShipment", "No shipment found!"));
        return;
    }
    List<ShipmentLine> shipmentLines =null;
    try {
        shipmentLines = shipmentService.getShipmentLines(shipment.getId());
    } catch (SQLException e1) {
        BiancaLogger.getLogger().severe("Error in searching shipment lines" + e1);
    }
       ShipmentView shipmentView = ShipmentView.getInstance();
       SwingGui.getInstance().setContent(shipmentView);
       shipmentView.setPanel(shipment, shipmentLines);
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
