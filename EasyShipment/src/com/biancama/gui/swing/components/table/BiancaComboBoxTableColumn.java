package com.biancama.gui.swing.components.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.jdesktop.swingx.renderer.JRendererCheckBox;
import org.jdesktop.swingx.renderer.JRendererLabel;

public abstract class BiancaComboBoxTableColumn extends BiancaTableColumn implements ActionListener {

    private static final long serialVersionUID = 5128718970314177880L;

    private JRendererLabel comborend;
    protected JComboBox combobox;

    public BiancaComboBoxTableColumn(String name, BiancaTableModel table, JComboBox combobox) {
        super(name, table);

        comborend = new JRendererLabel();
       
        this.combobox = combobox;
       
    }

    protected abstract String getDisplayedValue(Object value);

    @Override
    protected int getMaxWidth() {
        return 100;
    }

    @Override
    public final Object getCellEditorValue() {
        return combobox.getSelectedIndex();
    }

    @Override
    public final Component myTableCellEditorComponent(BiancaTableModel table, Object value, boolean isSelected, int row, int column) {
        combobox.removeActionListener(this);
        combobox.setSelectedItem(getDisplayedValue(value));
        combobox.addActionListener(this);
        return combobox;
    }

    @Override
    public final Component myTableCellRendererComponent(BiancaTableModel table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        comborend.setText(getDisplayedValue(value));
        return comborend;
    }

    
    
    public void actionPerformed(ActionEvent e) {
        combobox.removeActionListener(this);
        this.fireEditingStopped();
    }


}
