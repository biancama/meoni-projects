//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.gui.swing.components.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.biancama.config.SubConfiguration;

public abstract class BiancaTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -7560879256832858265L;

    protected SubConfiguration config;

    protected ArrayList<Object> list = new ArrayList<Object>();
    protected ArrayList<BiancaTableColumn> columns = new ArrayList<BiancaTableColumn>();
    private final ArrayList<BiancaRowHighlighter> highlighter = new ArrayList<BiancaRowHighlighter>();
    private BiancaTable table = null;

    public BiancaTableModel(String configname) {
        super();
        config = SubConfiguration.getConfig(configname);
        initColumns();
    }
    // Added by M. Biancalani
    public void removeAllRows(){
        list.clear();
    }
    /**
     * Should be overwritten to initialize the columns of the JDTable
     */
    protected abstract void initColumns();

    public void setBiancaTable(BiancaTable table) {
        this.table = table;
    }

    /**
     * attention: may return null if not set
     */
    public BiancaTable getBiancaTable() {
        return table;
    }

    public void addColumn(BiancaTableColumn e) {
        columns.add(e);
    }

    public void addColumn(BiancaTableColumn e, int index) {
        columns.add(index, e);
    }

    public SubConfiguration getConfig() {
        return config;
    }

    abstract public void refreshModel();

    public int getRowCount() {
        return list.size();
    }

    public ArrayList<BiancaRowHighlighter> getBiancaRowHighlighter() {
        return highlighter;
    }

    public void addBiancaRowHighlighter(BiancaRowHighlighter high) {
        highlighter.add(high);
    }

    public int getRowforObject(Object o) {
        synchronized (list) {
            return list.indexOf(o);
        }
    }

    public Object getObjectforRow(int rowIndex) {
        synchronized (list) {
            if (rowIndex < list.size()) { return list.get(rowIndex); }
            return null;
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            return list.get(rowIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getColumnCount() {
        return columns.size();
    }

    public boolean isVisible(int column) {
        BiancaTableColumn col = getBiancaTableColumn(column);
        return config.getBooleanProperty("VISABLE_COL_" + col.getName(), col.defaultEnabled());
    }

    public void setVisible(int column, boolean visible) {
        BiancaTableColumn col = getBiancaTableColumn(column);
        config.setProperty("VISABLE_COL_" + col.getName(), visible);
        config.save();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        columns.get(columnIndex).setValueAt(value, rowIndex, columnIndex);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public BiancaTableColumn getBiancaTableColumn(int columnIndex) {
        return columns.get(columnIndex);
    }

}
