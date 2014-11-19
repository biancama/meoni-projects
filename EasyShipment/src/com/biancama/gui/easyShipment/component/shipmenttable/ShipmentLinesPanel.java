package com.biancama.gui.easyShipment.component.shipmenttable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.swing.interfaces.SwitchPanel;


public class ShipmentLinesPanel extends SwitchPanel implements ActionListener{

    /**
     * 
     */
    private static final long serialVersionUID = 8533620878669614099L;
    private static ShipmentLinesPanel INSTANCE;
    private JPanel shipmentHeaderPanel;
       
    private ShipmentTable internalTable;
    private JScrollPane scrollPane;
    
    public synchronized static ShipmentLinesPanel getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ShipmentLinesPanel();
        }
        return INSTANCE;
    }
    private ShipmentLinesPanel(){
        super(new MigLayout("ins 0, wrap 1", "[grow, fill]", "[grow, fill]"));
        internalTable = new ShipmentTable(this);
        scrollPane = new JScrollPane(internalTable);
        scrollPane.setBorder(null);

        this.add(scrollPane, "cell 0 0");
    }
    
    @Override
    protected void onHide() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void onShow() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
    public List<ShipmentLine> getShipmentLine() {
        List<ShipmentLine> result = new ArrayList<ShipmentLine>();
        for (int i = 0; i < internalTable.getDataModel().getRowCount(); i++) {
            result.add((ShipmentLine) internalTable.getDataModel().getObjectforRow(i));
        }
        return result;
    }

    public void setShipmentLine(List<ShipmentLine> shipmentLines) {
        internalTable.getDataModel().removeAllRows();
        
        if (shipmentLines != null){
            for (ShipmentLine shipmentLine : shipmentLines) {
                internalTable.getDataModel().addRow(shipmentLine);
            }            
        }
        internalTable.repaint(); 
    }
    public void addEmptyRow(){
        internalTable.getDataModel().addEmptyRow();
    }
    
    public void loseFocusonTable(){
        if ( internalTable.getCellEditor() != null){
            internalTable.getCellEditor().stopCellEditing();
        }
       
    }
    public JPanel getShipmentHeaderPanel() {
        return shipmentHeaderPanel;
    }
    public void setShipmentHeaderPanel(JPanel shipmentHeaderPanel) {
        this.shipmentHeaderPanel = shipmentHeaderPanel;
    }
}
