package com.biancama.gui.easyShipment.component.shipmenttable;

import java.math.BigDecimal;

import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.swing.components.table.BiancaTable;
import com.biancama.gui.swing.components.table.BiancaTableModel;
import com.biancama.gui.swing.components.table.BiancaTextEditorTableColumn;
import com.biancama.utils.gui.ValidatorUtils;

public class LinePrice extends BiancaTextEditorTableColumn {

    /**
     * 
     */
    private static final long serialVersionUID = 22347085956318007L;

    public LinePrice(String name, BiancaTableModel table) {
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
            shipmentLine.setPrice(valueChecked);     
            if (valueChecked == null){
                BiancaTable table = this.getBiancaTableModel().getBiancaTable();
//                table.setS
            }
        }else{
            shipmentLine.setPrice(null);
        }
        
    }

    @Override
    protected String getStringValue(Object value) {
       ShipmentLine shipmentLine = (ShipmentLine) value;
       if (shipmentLine.getPrice() == null){
           return null;
       }else{
           return shipmentLine.getPrice().toString();           
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
