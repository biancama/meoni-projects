package com.biancama.gui.easyShipment.component.shipmenttable;

import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.swing.components.table.BiancaTableModel;
import com.biancama.gui.swing.components.table.BiancaTextEditorTableColumn;


public class LineLot extends BiancaTextEditorTableColumn {

    public LineLot(String name, BiancaTableModel table) {
        super(name, table);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 3447120087057885181L;
    @Override
    public boolean isEditable(Object obj) {
        return true;
    }
    @Override
    protected void setStringValue(String value, Object object) {
        ShipmentLine shipmentLine = (ShipmentLine) object;
       
            shipmentLine.setLot(value);     
        
    }

    @Override
    protected String getStringValue(Object value) {
       ShipmentLine shipmentLine = (ShipmentLine) value;
       return shipmentLine.getLot();
    }

    @Override
    public boolean isEnabled(Object obj) {
        return true;
    }

    @Override
    public boolean isSortable(Object obj) {
        return false;
    }

    @Override
    public void sort(Object obj, boolean sortingToggle) {
        // DO-NOTHING
        
    }

}
