package gui;

/*
 * 
  Code reference: http://forum.java.sun.com/thread.jspa?threadID=384665&messageID=3310903  
Pratap Singh
Email:  pratap.singh at siemens.com
        at is @
 * 
 * */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.EventObject;
import java.util.GregorianCalendar;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SimpleTableTestTabComboBox extends JFrame {

  protected JTable table;

  public SimpleTableTestTabComboBox() {
    Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    TableValues tv = new TableValues();
    table = new JTable(tv);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(TableValues.GENDER);
//    tc.setCellRenderer(new GenderRenderer());

    tc.setCellEditor(new GenderEditor());

    JScrollPane jsp = new JScrollPane(table);
    pane.add(jsp, BorderLayout.CENTER);
    pane.add(new JTextField(), BorderLayout.SOUTH);
  }

  public static void main(String[] args) {
    SimpleTableTestTabComboBox stt = new SimpleTableTestTabComboBox();
    stt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    stt.setSize(400, 300);
    stt.setVisible(true);
  }
}

class TableValues extends AbstractTableModel {

  public final static int FIRST_NAME = 0;

  public final static int LAST_NAME = 1;

  public final static int DATE_OF_BIRTH = 2;

  public final static int ACCOUNT_BALANCE = 3;

  public final static int GENDER = 4;

  public final static boolean GENDER_MALE = true;

  public final static boolean GENDER_FEMALE = false;

  public final static String[] columnNames = { "First Name", "Last Name", "Date of Birth",
      "Account Balance", "Gender" };

  public Object[][] values = {
      { "Clay", "Ashworth", new GregorianCalendar(1962, Calendar.FEBRUARY, 20).getTime(),
          new Float(12345.67), "three" },
      { "Jacob", "Ashworth", new GregorianCalendar(1987, Calendar.JANUARY, 6).getTime(),
          new Float(23456.78), "three1" },
      { "Jordan", "Ashworth", new GregorianCalendar(1989, Calendar.AUGUST, 31).getTime(),
          new Float(34567.89), "three2" },
      { "Evelyn", "Kirk", new GregorianCalendar(1945, Calendar.JANUARY, 16).getTime(),
          new Float(-456.70), "One" },
      { "Belle", "Spyres", new GregorianCalendar(1907, Calendar.AUGUST, 2).getTime(),
          new Float(567.00), "two" } };

  public int getRowCount() {
    return values.length;
  }

  public int getColumnCount() {
    return values[0].length;
  }

  public Object getValueAt(int row, int column) {
    return values[row][column];
  }

  public void setValueAt(Object aValue, int r, int c) {
    values[r][c] = aValue;
  }

  public String getColumnName(int column) {
    return columnNames[column];
  }

  public boolean isCellEditable(int r, int c) {
    return c == GENDER;
  }
}

class MyComboUI extends BasicComboBoxUI {

  public JList getList() {
    return listBox;
  }
}

class GenderRenderer extends JComboBox implements TableCellRenderer {

  public GenderRenderer() {
    super();
    addItem("three");
    addItem("three1");
    addItem("three2");
    addItem("One");
    addItem("two");
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {

    if (hasFocus) {
      table.editCellAt(row, column);
      JComboBox combo = (JComboBox) table.getColumnModel().getColumn(TableValues.GENDER)
          .getCellEditor();
      ComboBoxEditor editor = (ComboBoxEditor) combo.getEditor();
      JTextField tf = (JTextField) editor.getEditorComponent();
      tf.requestFocusInWindow();
      tf.selectAll();
    }
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }

    setSelectedItem(value);
    return this;
  }

}

class GenderEditor extends JComboBox implements TableCellEditor, CaretListener {

  protected EventListenerList listenerList = new EventListenerList();

  protected ChangeEvent changeEvent = new ChangeEvent(this);

  private MyComboUI comboUi = new MyComboUI();

  private JTextField tf;

  Object newValue;

  public GenderEditor() {
    super();
    setUI(comboUi);
    // comboUi.get
    addItem("three");
    addItem("three1");
    addItem("three2");
    addItem("One");
    addItem("two");
    setEditable(true);
    tf = (JTextField) getEditor().getEditorComponent();
    tf.addCaretListener(this);

    tf.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          Object value = comboUi.getList().getSelectedValue();
          tf.transferFocus();
          hidePopup();
          System.out.println("enter pressed, selected value, when enter pressed: " + value);
          fireEditingStopped();
        }
      }
    });

    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        // fireEditingStopped();
      }
    });
  }

  public void addCellEditorListener(CellEditorListener listener) {
    listenerList.add(CellEditorListener.class, listener);
  }

  public void removeCellEditorListener(CellEditorListener listener) {
    listenerList.remove(CellEditorListener.class, listener);
  }

  protected void fireEditingStopped() {
    System.out.println("fireEditingStopped called ");

    CellEditorListener listener;
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i++) {
      if (listeners[i] == CellEditorListener.class) {
        listener = (CellEditorListener) listeners[i + 1];
        listener.editingStopped(changeEvent);
      }
    }
  }

  protected void fireEditingCanceled() {
    CellEditorListener listener;
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i++) {
      //if (listeners == CellEditorListener.class) {
      if (listeners[i] == CellEditorListener.class) {
        listener = (CellEditorListener) listeners[i + 1];
        listener.editingCanceled(changeEvent);
      }
    }
  }

  public void cancelCellEditing() {
    System.out.println("cancelCellEditing called ");
    fireEditingCanceled();
  }

  public boolean stopCellEditing() {
    System.out.println("stopCellEditing called ");
    fireEditingStopped();
    return true;
  }

  public boolean isCellEditable(EventObject event) {
    return true;
  }

  public boolean shouldSelectCell(EventObject event) {
    return true;
  }

  public Object getCellEditorValue() {
    System.out.println("getCellEditorValue called returning vlaue: " + newValue);
    tf.setText(newValue.toString());
    return newValue;
    // return super.getSelectedItem();
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {

    return this;
  }

  public void caretUpdate(CaretEvent e) {
    if (!isPopupVisible() && tf.isShowing() && tf.hasFocus())
      showPopup();

    JTextField tf = (JTextField) e.getSource();
    String text = tf.getText().toLowerCase();

    int index = -1;
    for (int i = 0; i < super.getItemCount(); i++) {
      String item = ((String) getItemAt(i)).toLowerCase();
      if (item.startsWith(text)) {
        index = i;
        break;
      }
    }
    if (index != -1)
      comboUi.getList().setSelectedIndex(index);
    else
      comboUi.getList().clearSelection();

    newValue = comboUi.getList().getSelectedValue();
    System.out.println("new value set to: " + newValue);
  }

}           
             