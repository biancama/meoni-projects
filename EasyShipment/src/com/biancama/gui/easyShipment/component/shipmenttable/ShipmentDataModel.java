package com.biancama.gui.easyShipment.component.shipmenttable;

import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.swing.components.table.BiancaTableModel;
import com.biancama.utils.locale.BiancaL;

public class ShipmentDataModel extends BiancaTableModel {
    
    public ShipmentDataModel(String configname) {
        super(configname);
   
    }


    private final static String LOCALE_PREFIX = "com.biancama.gui.easyShipment.component.shipmenttable."; 
   
    
    // index
    private final static int QTY_INDEX = 0;
    private final static int PRODUCT_INDEX = 1;
    private final static int LOT_INDEX = 2;
    private final static int QTYB_INDEX = 3;
    /**
     * 
     */
    private static final long serialVersionUID = -8629626857284843204L;
    



    
    public void addEmptyRow() {
        ShipmentLine shipmentLine = new ShipmentLine();

        if (list.size() > 0){
            ShipmentLine shipmentLineLastOne = (ShipmentLine) list.get(list.size() - 1);
            shipmentLine.setLot(shipmentLineLastOne.getLot());
        }
        list.add(shipmentLine);
        fireTableRowsInserted(
           list.size() - 1,
           list.size() - 1);
    }
    public void addRow(ShipmentLine shipmentLine){
        list.add(shipmentLine);
        fireTableRowsInserted(
           list.size() - 1,
           list.size() - 1);
    }


    @Override
    protected void initColumns() {
        addColumn(new LineProduct(BiancaL.getLocaleString(LOCALE_PREFIX + "product", "Product"), this));
        addColumn(new LineQuantity(BiancaL.getLocaleString(LOCALE_PREFIX + "qty", "Quantity"), this));  
        addColumn(new LineLot(BiancaL.getLocaleString(LOCALE_PREFIX + "lot", "Lot"), this));  
        addColumn(new LinePrice(BiancaL.getLocaleString(LOCALE_PREFIX + "price", "Price"), this));  
        addColumn(new LineBQuantity(BiancaL.getLocaleString(LOCALE_PREFIX + "qtyB", ""), this));  
    }


    @Override
    public void refreshModel() {
        // TODO Auto-generated method stub
        
    }
    

}
