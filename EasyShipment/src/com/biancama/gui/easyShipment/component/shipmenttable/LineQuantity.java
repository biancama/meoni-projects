package com.biancama.gui.easyShipment.component.shipmenttable;

import java.math.BigDecimal;

import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.swing.components.table.BiancaTable;
import com.biancama.gui.swing.components.table.BiancaTableModel;
import com.biancama.gui.swing.components.table.BiancaTextEditorTableColumn;
import com.biancama.utils.gui.ValidatorUtils;

public class LineQuantity extends BiancaTextEditorTableColumn {

    /**
     * 
     */
    private static final long serialVersionUID = 22347085956318007L;

    public LineQuantity(String name, BiancaTableModel table) {
        super(name, table);
    }
    @Override
    public boolean isEditable(Object obj) {
        return true;
    }
    @Override
    protected void setStringValue(String value, Object object) {
        ShipmentLine shipmentLine = (ShipmentLine) object;
        if (value != null && !value.equals("")){
            BigDecimal valueChecked = ValidatorUtils.checkNumber(value);
            shipmentLine.setQuantity(valueChecked);     
            if (valueChecked == null){
                BiancaTable table = this.getBiancaTableModel().getBiancaTable();
//                table.setS
            }
        }else{
            shipmentLine.setQuantity(null);
        }
        
    }

    @Override
    protected String getStringValue(Object value) {
       ShipmentLine shipmentLine = (ShipmentLine) value;
       if (shipmentLine.getQuantity() == null){
           return null;
       }else{
           return shipmentLine.getQuantity().toString();           
       }
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
