package com.biancama.gui.easyShipment.component.shipmenttable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.CellEditor;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.biancama.gui.easyShipment.views.shipment.ShipmentPanel;
import com.biancama.gui.swing.components.table.BiancaTable;
import com.biancama.log.BiancaLogger;



public class ShipmentTable extends BiancaTable implements MouseListener, KeyListener {

    /**
     * 
     */
    private static final long serialVersionUID = 5543284012381613595L;
    private final ShipmentLinesPanel shipmentLinesPanel;
    private ShipmentDataModel dataModel;
    
    public ShipmentTable(ShipmentLinesPanel shipmentLinesPanel) {
        super(new ShipmentDataModel("shipmentLines"));
        if (dataModel == null){
            dataModel = (ShipmentDataModel) this.getBiancaTableModel();
        }
       dataModel.addTableModelListener(new InteractiveTableModelListener());
        this.setSurrendersFocusOnKeystroke(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true);
        
        this.shipmentLinesPanel = shipmentLinesPanel;
        this.addMouseListener(this);  
        // risolve il problema del ctrl + s che non salvava l'ultima modifica
        // se il text field era in editing in editing
        this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
       overrideTabBehavior();
    }
    
    @Override
    public void changeSelection(
            int row, int column, boolean toggle, boolean extend)
        {
           
            
        super.changeSelection(row, column, toggle, extend);
        // Put cell selected in editing mode by default
        if (column == 0){
            return;
        }
            if (editCellAt(row, column))
            {
                Component editor = getEditorComponent();
                editor.requestFocusInWindow();
//              ((JTextComponent)editor).selectAll();
            }
        }

    @Override
    public void mouseClicked(MouseEvent e) {
     
        if (dataModel.getRowCount() == 0){
            dataModel.addEmptyRow();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        BiancaLogger.getLogger().fine("Mouse Entered");
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        BiancaLogger.getLogger().fine("Mouse Exited");
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        BiancaLogger.getLogger().fine("Mouse Pressed");
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        BiancaLogger.getLogger().fine("Mouse Released");
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
      
        KeyStroke ctrlS = KeyStroke.getKeyStroke("ctrl s");
        if (e.getKeyChar() == ctrlS.getKeyChar()){
            BiancaLogger.getLogger().fine("Ctrl s typed");
            ShipmentTable.this.getCellEditor().stopCellEditing();
        }
        
        
    }
    
    public class InteractiveTableModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent evt) {
            if (evt.getType() == TableModelEvent.UPDATE) {
                ShipmentPanel.getInstance().onUpdating();
                int column = evt.getColumn();
                        
                int row = evt.getFirstRow();
                if (row < 0 && column < 0 ){
                    return;
                }
                if (column != 0){ // the event it's already handled by tab
                    return;
                }
                //requestFocusInWindow(); // JDK 1.4 onwards
                BiancaLogger.getLogger().fine("row: " + row + " column: " + column);
                if (column < dataModel.getColumnCount() - 1 ){
                    ShipmentTable.this.setColumnSelectionInterval(column + 1, column + 1);
                    ShipmentTable.this.setRowSelectionInterval(row, row);     
                    
                }else{
                   if (ShipmentTable.this.dataModel.getRowCount() <= row + 1 ){
                       ShipmentTable.this.dataModel.addEmptyRow();
                   }
                   
                    ShipmentTable.this.setColumnSelectionInterval(0, 0);
                    ShipmentTable.this.setRowSelectionInterval(row + 1, row + 1);  
                    
                    
                }
            }
        }
    }
    
  //Override the default tab behaviour.
    //Tab to the next editable cell. When no editable cells goto next cell*/
    private void overrideTabBehavior(){
        InputMap im = this.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        KeyStroke shiftTab = KeyStroke.getKeyStroke("shift TAB");
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        final Action oldTabAction = this.getActionMap().get(im.get(tab));
        final Action enterAction = this.getActionMap().get(im.get(enter));
        Action tabAction = new AbstractAction() {
            

            /**
             * 
             */
            private static final long serialVersionUID = 8954118240297101999L;

            @Override       
            public void actionPerformed(ActionEvent e) {
                int rows = ShipmentTable.this.getRowCount();
                int cols = ShipmentTable.this.getColumnCount();
                int selectedRow = ShipmentTable.this.getEditingRow();
                int selectedColumn = ShipmentTable.this.getEditingColumn();
                moveNext(selectedRow, rows, selectedColumn, cols);               
              
            }

           
        };
        
        Action shiftTabAction = new AbstractAction() {
            

            /**
             * 
             */
            private static final long serialVersionUID = 8954118240297101999L;

            @Override       
            public void actionPerformed(ActionEvent e) {
                int rows = ShipmentTable.this.getRowCount();
                int cols = ShipmentTable.this.getColumnCount();
                int selectedRow = ShipmentTable.this.getEditingRow();
                int selectedColumn = ShipmentTable.this.getEditingColumn();
                movePrev(selectedRow, rows, selectedColumn, cols);               
              
            }

           
        };
        this.getActionMap().put(im.get(tab), tabAction);              
        this.getActionMap().put(im.get(shiftTab), shiftTabAction);              

    }

    public ShipmentDataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(ShipmentDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public void removeAllRows(){
        this.dataModel.removeAllRows();
    }
    
    private void moveNext(int selectedRow, int rows, int selectedColumn, int cols) {
        
        selectedRow = selectedRow <0 ? 0 : selectedRow;
        
//        selectedColumn = selectedColumn <0 ? 0 : selectedColumn;
        if((rows == (selectedRow+1)) && (cols == (selectedColumn+1))){
            ShipmentTable.this.dataModel.addEmptyRow();
            
        }
        if (selectedColumn < cols - 1){
          ShipmentTable.this.setColumnSelectionInterval(selectedColumn+ 1, selectedColumn+ 1);
          ShipmentTable.this.setRowSelectionInterval(selectedRow, selectedRow);
                
        } else {
          ShipmentTable.this.setColumnSelectionInterval(0, 0);
          ShipmentTable.this.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
          
      }
        if (selectedColumn < cols - 1){
            ShipmentTable.this.editCellAt(selectedRow, selectedColumn+ 1);
        } else {
            ShipmentTable.this.editCellAt(selectedRow + 1 , 0);
        }
    }
    private void movePrev(int selectedRow, int rows, int selectedColumn, int cols) {
        
        selectedRow = selectedRow <0 ? 0 : selectedRow;
        selectedColumn = selectedColumn <0 ? 1 : selectedColumn;
        
        if ((selectedRow == 0) && (selectedColumn == 0)){
            return;
        }
               
        if (selectedColumn == 0){
          ShipmentTable.this.setColumnSelectionInterval(cols - 1 , cols - 1);
          ShipmentTable.this.setRowSelectionInterval(selectedRow - 1, selectedRow -  1);
          ShipmentTable.this.editCellAt(selectedRow - 1, cols - 1);
      } else {
          ShipmentTable.this.setColumnSelectionInterval(selectedColumn - 1 , selectedColumn - 1);
          ShipmentTable.this.setRowSelectionInterval(selectedRow, selectedRow);
         ShipmentTable.this.editCellAt(selectedRow , selectedColumn - 1);
      }
        
    }

    public ShipmentLinesPanel getShipmentLinesPanel() {
        return shipmentLinesPanel;
    }


      
        

}
