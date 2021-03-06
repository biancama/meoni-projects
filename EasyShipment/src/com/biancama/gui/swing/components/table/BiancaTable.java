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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;

import com.biancama.config.SubConfiguration;
import com.biancama.gui.swing.components.JExtCheckBoxMenuItem;
import com.biancama.gui.swing.interfaces.BiancaMouseAdapter;
import com.biancama.utils.JavaUtils;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

class SortMenuItem extends JMenuItem implements ActionListener {

    private static final long serialVersionUID = 6328630034846759725L;
    private Object obj = null;
    private BiancaTableColumn column = null;
    private static String defaultString = BiancaL.L("gui.table.contextmenu.sort", " Sort");

    public SortMenuItem() {
        super(defaultString);
        this.setIcon(BiancaTheme.II("gui.images.sort", 16, 16));
        this.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (column == null) { return; }
        if (column.isSortable(obj)) {
            column.doSort(obj);
        }
    }

    public void set(BiancaTableColumn column, Object obj, String desc) {
        if (desc == null) {
            desc = defaultString;
        }
        this.column = column;
        this.obj = obj;
        this.setText(desc);
    }
}

public class BiancaTable extends JXTable {

    private static final long serialVersionUID = -6631229711568284941L;
    private static final String JDL_PREFIX = "jd.gui.swing.components.table.JDTable.";
    private final BiancaTableModel model;
    private final SubConfiguration tableconfig;
    private final SortMenuItem defaultSortMenuItem;
    public static final int ROWHEIGHT = 19;

    public BiancaTable(BiancaTableModel model) {
        super(model);
        this.model = model;
        model.setBiancaTable(this);
        tableconfig = model.getConfig();
        createColumns();
        setSortable(false);
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        UIManager.put("Table.focusCellHighlightBorder", null);
        defaultSortMenuItem = new SortMenuItem();
        getTableHeader().addMouseListener(new BiancaMouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int col = realColumnAtPoint(e.getPoint());
                    if (getBiancaTableModel().getBiancaTableColumn(col).isSortable(null)) {
                        getBiancaTableModel().getBiancaTableColumn(col).doSort(null);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    columControlMenu().show(getTableHeader(), e.getX(), e.getY());
                }
            }

        });
        getTableHeader().setReorderingAllowed(true);
        getTableHeader().setResizingAllowed(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setAutoscrolls(true);
        this.setRowHeight(ROWHEIGHT);
        installColumnControlButton();
        getTableHeader().setPreferredSize(new Dimension(getColumnModel().getTotalColumnWidth(), 19));
        // This method is 1.6 only
        if (JavaUtils.getJavaVersion() >= 1.6) {
            this.setFillsViewportHeight(true);
        }

        getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
            }

            public void columnMarginChanged(ChangeEvent e) {
            }

            public void columnMoved(TableColumnModelEvent e) {
                if (e == null) { return; }
                if (e.getFromIndex() == e.getToIndex()) { return; }
                TableColumnModel tcm = getColumnModel();
                for (int i = 0; i < tcm.getColumnCount(); i++) {
                    tableconfig.setProperty("POS_COL_" + i, getBiancaTableModel().getBiancaTableColumn(tcm.getColumn(i).getModelIndex()).getID());
                }
                tableconfig.save();
            }

            public void columnRemoved(TableColumnModelEvent e) {
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
            }

        });
    }

    private void installColumnControlButton() {
        JButton button = new JButton(((JButton) getColumnControl()).getIcon());
        button.setToolTipText(BiancaL.L(JDL_PREFIX + "columnControl", "Change Columns"));
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JButton source = (JButton) event.getSource();
                int x = source.getLocation().x;
                int y = source.getLocation().y;
                columControlMenu().show(getTableHeader(), x, y);
            }

        });
        this.setColumnControl(button);
        this.setColumnControlVisible(true);
    }

    public JPopupMenu columControlMenu() {
        JPopupMenu popup = new JPopupMenu();
        JCheckBoxMenuItem[] mis = new JCheckBoxMenuItem[getBiancaTableModel().getColumnCount()];

        for (int i = 0; i < getBiancaTableModel().getColumnCount(); ++i) {
            final int j = i;
            final JExtCheckBoxMenuItem mi = new JExtCheckBoxMenuItem(getBiancaTableModel().getColumnName(i));
            mi.setHideOnClick(false);
            mis[i] = mi;
            if (i == 0) {
                mi.setEnabled(false);
            }
            mi.setSelected(getBiancaTableModel().isVisible(i));
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    getBiancaTableModel().setVisible(j, mi.isSelected());
                    createColumns();
                    revalidate();
                    repaint();
                }

            });
            popup.add(mi);
        }
        return popup;
    }

    public ArrayList<BiancaRowHighlighter> getBiancaRowHighlighter() {
        return model.getBiancaRowHighlighter();
    }

    public void addBiancaRowHighlighter(BiancaRowHighlighter high) {
        model.addBiancaRowHighlighter(high);
    }

    public BiancaTableModel getBiancaTableModel() {
        return model;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int col) {
        return model.getBiancaTableColumn(convertColumnIndexToModel(col));
    }

    @Override
    public TableCellEditor getCellEditor(int row, int col) {
        return model.getBiancaTableColumn(convertColumnIndexToModel(col));
    }

    private void createColumns() {
        setAutoCreateColumnsFromModel(false);
        TableColumnModel tcm = getColumnModel();
        while (tcm.getColumnCount() > 0) {
            tcm.removeColumn(tcm.getColumn(0));
        }
        LinkedHashMap<String, TableColumn> columns = new LinkedHashMap<String, TableColumn>();
        for (int i = 0; i < getModel().getColumnCount(); ++i) {
            final int j = i;

            TableColumn tableColumn = new TableColumn(i);
            tableColumn.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("width")) {
                        tableconfig.setProperty("WIDTH_COL_" + model.getBiancaTableColumn(j).getID(), evt.getNewValue());
                        tableconfig.save();
                    }
                }
            });
            tableColumn.setPreferredWidth(tableconfig.getIntegerProperty("WIDTH_COL_" + model.getBiancaTableColumn(j).getID(), tableColumn.getWidth()));
            if (!model.isVisible(i)) {
                continue;
            }
            columns.put(model.getBiancaTableColumn(j).getID(), tableColumn);
        }
        int index = 0;
        while (true) {
            if (columns.isEmpty()) {
                break;
            }
            if (index < getModel().getColumnCount()) {
                String id = tableconfig.getStringProperty("POS_COL_" + index, null);
                index++;
                if (id != null) {
                    TableColumn item = columns.remove(id);
                    if (item != null) {
                        addColumn(item);
                    }
                }
            } else {
                for (TableColumn ritem : columns.values()) {
                    addColumn(ritem);
                }
                break;
            }
        }
    }

    public int realColumnAtPoint(Point point) {
        int x = columnAtPoint(point);
        return convertColumnIndexToModel(x);
    }

    public void addSortItem(JPopupMenu menu, int colindex, Object obj, String desc) {
        if (menu == null) { return; }
        BiancaTableColumn col = model.getBiancaTableColumn(colindex);
        if (col.isSortable(obj)) {
            defaultSortMenuItem.set(col, obj, desc);
            menu.add(defaultSortMenuItem);
        }
    }

    public Point getPointinCell(Point x) {
        int row = rowAtPoint(x);
        if (row == -1) { return null; }
        Rectangle cellPosition = getCellRect(row, columnAtPoint(x), true);
        Point p = new Point();
        p.setLocation(x.getX() - cellPosition.getX(), x.getY() - cellPosition.getY());
        return p;
    }



}
