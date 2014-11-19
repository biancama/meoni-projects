package com.biancama.gui.easyShipment.component.shipmenttable;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.biancama.gui.easyShipment.components.ProductComponent;
import com.biancama.gui.easyShipment.model.Product;
import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.easyShipment.persistence.service.PriceListService;
import com.biancama.gui.swing.components.BiancaFormattedTextField;
import com.biancama.gui.swing.components.table.BiancaComboBoxTableColumn;
import com.biancama.gui.swing.components.table.BiancaTableColumn;
import com.biancama.gui.swing.components.table.BiancaTableModel;

public class LineProduct extends BiancaComboBoxTableColumn {

    /**
     * 
     */
    private static final long serialVersionUID = 2890068732641600968L;

    public LineProduct(String name, BiancaTableModel table) {
        super(name, table, (JComboBox) (new ProductComponent()).getComponent());
    }

    @Override
    protected String getDisplayedValue(Object value) {
        ShipmentLine shipmentLine = (ShipmentLine) value;
        if (shipmentLine.getProduct() != null) {
            return shipmentLine.getProduct().getName();
        } else {
            return null;
        }

    }

    @Override
    public boolean isEditable(Object obj) {
        return isEnabled(obj);
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
    public void setValue(Object value, Object object) {
        ShipmentLine shipmentLine = (ShipmentLine) object;
        Integer valueSelected = (Integer) value;

        if (shipmentLine.getProduct() == null) {
            shipmentLine.setProduct(new Product());
        }
        shipmentLine.getProduct().setName((String) combobox.getItemAt(valueSelected));
        // looking for price

        ShipmentTable shipmentTable = (ShipmentTable) this.getBiancaTableModel().getBiancaTable();
        JPanel shipmentHeaderPanel = shipmentTable.getShipmentLinesPanel().getShipmentHeaderPanel();
        Date shipmentDate = null;
        String customerValue = null;
        
        // looking for customer and data
        for (int i = 0; i < shipmentHeaderPanel.getComponentCount(); i++) {
            String componentName = shipmentHeaderPanel.getComponent(i).getName();
            if (componentName != null && componentName.equals("ShipmentDate")) {
                BiancaFormattedTextField detailedComponent = (BiancaFormattedTextField) shipmentHeaderPanel.getComponent(i); 
                shipmentDate = (Date) detailedComponent.getValue();
            } else if (componentName != null && componentName.equals("CustomerComponent")) {
                JComboBox customerCombo = (JComboBox) shipmentHeaderPanel.getComponent(i);
                customerValue = (String) customerCombo.getSelectedItem();
            }
        }
        PriceListService pricelistService = new PriceListService();
        BigDecimal priceList = null;
        try {
            priceList = pricelistService.getListPrice(customerValue, shipmentDate, (String) combobox.getItemAt(valueSelected));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       for (int i = 0; i < this.getBiancaTableModel().getColumnCount(); i++) {
          BiancaTableColumn biancaTableColumn = this.getBiancaTableModel().getBiancaTableColumn(i);
        
       }
       shipmentLine.setPrice(priceList.stripTrailingZeros());
    }

    @Override
    public void sort(Object obj, boolean sortingToggle) {

    }
}
