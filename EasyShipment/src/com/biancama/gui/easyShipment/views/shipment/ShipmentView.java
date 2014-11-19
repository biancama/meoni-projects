package com.biancama.gui.easyShipment.views.shipment;

import java.util.List;

import javax.swing.Icon;

import com.biancama.gui.easyShipment.model.Shipment;
import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.swing.interfaces.View;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class ShipmentView extends View {

    /**
     * 
     */
    private static final long serialVersionUID = -1362383328100478066L;

    private static final String IDENT_PREFIX = "com.biancama.gui.easyShipment.views.shipmentview.";
    private static ShipmentView INSTANCE = null;
    
    public static synchronized ShipmentView getInstance(){
        if (INSTANCE == null){
            INSTANCE = new  ShipmentView();
        }
        return INSTANCE;
    }
    
    private ShipmentView(){
        super();
        ShipmentPanel.getInstance().onNewing();
        this.setContent(ShipmentPanel.getInstance());
    }
    @Override
    public Icon getIcon() {
        return BiancaTheme.II("gui.images.taskpanes.shipment", ICON_SIZE, ICON_SIZE);
    }

    @Override
    public String getTitle() {
        return BiancaL.L(IDENT_PREFIX + "tab.title", "Shipment");
    }

    @Override
    public String getTooltip() {
        return BiancaL.L(IDENT_PREFIX + "tab.tooltip", "Create Shipment");
    }

    @Override
    protected void onHide() {
    }

    @Override
    protected void onShow() {
    }
    
    public void resetPanel(){
        ShipmentPanel.getInstance().onNewing();
    }
    
    public void setPanel(Shipment shipment, List<ShipmentLine> shipmentLines){
        ShipmentPanel.getInstance().setShipment(shipment);
        if (shipmentLines != null){
            ShipmentPanel.getInstance().setShipmentLine(shipmentLines);            
        }
        ShipmentPanel.getInstance().onSaving();
    }
}
