package com.biancama.gui.easyShipment.views.shipment;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.print.PrintException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRException;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;

import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.component.shipmenttable.CustomerComponent;
import com.biancama.gui.easyShipment.component.shipmenttable.CustomerLocationComponent;
import com.biancama.gui.easyShipment.component.shipmenttable.ItemNoComponent;
import com.biancama.gui.easyShipment.component.shipmenttable.ShipmentComponent;
import com.biancama.gui.easyShipment.component.shipmenttable.ShipmentDateComponent;
import com.biancama.gui.easyShipment.component.shipmenttable.ShipmentLinesPanel;
import com.biancama.gui.easyShipment.component.shipmenttable.ShipmentNoteComponent;
import com.biancama.gui.easyShipment.component.shipmenttable.ShipmentToolbar;
import com.biancama.gui.easyShipment.component.shipmenttable.ShipperComponent;
import com.biancama.gui.easyShipment.menus.actions.shipment.SaveShipmentAction;
import com.biancama.gui.easyShipment.model.FormAction;
import com.biancama.gui.easyShipment.model.Shipment;
import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.easyShipment.persistence.service.ShipmentService;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.CustomerLocationSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.CustomerSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.ShipmentSqlMapDao;
import com.biancama.gui.swing.actions.ActionController;
import com.biancama.gui.swing.actions.ToolBarAction;
import com.biancama.gui.swing.interfaces.SwitchPanel;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.gui.ValidatorUtils;
import com.biancama.utils.locale.BiancaL;

public class ShipmentPanel extends SwitchPanel implements ActionListener, FormAction{

    /**
     * 
     */
    private static final long serialVersionUID = 339466617214047651L;
    public static final String LOCALE_PREFIX = "com.biancama.gui.easyShipment.views.shipment";
    private static final String DOC_STATUS_COMPLETE = "CO";
    private static ShipmentPanel INSTANCE;
    private         Shipment shipment;

    // GUI
    private JPanel shipmentHeaderPanel;
    private ShipmentLinesPanel shipmentLinesPanel;
    private ShipmentToolbar toolBar;
    
    private ShipmentComponent shipmentComponent;
    private ShipmentDateComponent shipmentDate;
    private CustomerComponent customerComponent;
    private CustomerLocationComponent customerLocationComponent;
    private ShipmentNoteComponent shipmentNoteComponent;
    private ShipperComponent shipperComponent;
    private ItemNoComponent itemNoComponent;
    static final Color LABEL_COLOR = new Color(0, 70, 213);
    
    private boolean saved = false;
    private String saveonlyifnotcompleted;
    public static synchronized ShipmentPanel getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ShipmentPanel();
        }
        return INSTANCE;
    }

    private JPanel createTabPanel(LayoutManager lm)
    {
        JPanel panel = new JPanel(lm);
        panel.setOpaque(false);
        return panel;
    }
    
    private void addSeparator(JPanel panel, String text)
    {
        JLabel l = createLabel(text);
        l.setForeground(LABEL_COLOR);

        panel.add(l, "gapbottom 1, span, split 2, aligny center");
        panel.add(new JSeparator(), "gapleft rel, growx");
    }
    private JLabel createLabel(String text)
    {
        return createLabel(text, SwingConstants.LEADING);
    }

    private JLabel createLabel(String text, int align)
    {
        final JLabel b = new JLabel(text, align);    
        return b;
    }

    private ShipmentPanel(){
        
        
        super(new MigLayout("", "[para]0[][100lp, fill][60lp][95lp, fill]", ""));
        Properties props = new Properties();
        saveonlyifnotcompleted = "0";
        try {
            props.load(ClassLoader.getSystemResourceAsStream("setup.properties"));
            saveonlyifnotcompleted = props.getProperty("saveonlyifnotcompleted");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        shipment = new Shipment();
        initActions();
        
        shipmentHeaderPanel = new JPanel(new MigLayout("ins 0, wrap 2", "[fill,grow]", "[][fill,grow]"));
        createHeaderPanel();
        shipmentLinesPanel = ShipmentLinesPanel.getInstance();
        // add header panel to the lines
        shipmentLinesPanel.setShipmentHeaderPanel(shipmentHeaderPanel);
        
        addSeparator(this, BiancaL.L(LOCALE_PREFIX + ".header.shipment.label", "Header"));
        this.add(shipmentHeaderPanel, "wrap para");
        
        addSeparator(this, BiancaL.L(LOCALE_PREFIX + ".lines.shipment.label", "Lines"));
        this.add(shipmentLinesPanel, "span 8, gapleft rel, growx, wrap para");
        toolBar = new ShipmentToolbar();
        this.add(toolBar, "gapleft 3, gaptop 3");
        
    }


    private void createHeaderPanel() {
        shipmentHeaderPanel.add(new JLabel(BiancaL.L(LOCALE_PREFIX + ".header.shipment.number", "Shipment Number")));
        shipmentComponent = new ShipmentComponent("ShipmentComponent");
        shipmentHeaderPanel.add(shipmentComponent.getComponent());
        shipmentHeaderPanel.add(new JLabel(BiancaL.L(LOCALE_PREFIX + ".header.shipment.date", "Date")));
        shipmentDate = new ShipmentDateComponent("ShipmentDate");
        shipmentHeaderPanel.add(shipmentDate.getComponent());
        shipmentHeaderPanel.add(new JLabel(BiancaL.L(LOCALE_PREFIX + ".header.shipment.customer", "Customer")));
        customerComponent = new CustomerComponent("CustomerComponent");
        shipmentHeaderPanel.add(customerComponent.getComponent());
        shipmentHeaderPanel.add(new JLabel(BiancaL.L(LOCALE_PREFIX + ".header.shipment.customerLocation", "Location")));        
        customerLocationComponent = new CustomerLocationComponent("CustomerLocationComponent");
        shipmentHeaderPanel.add(customerLocationComponent.getComponent());
        
        ((JComboBox)customerComponent.getComponent()).addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String newSelection = (String)cb.getSelectedItem();
                customerLocationComponent.reload(newSelection);
            }
        });
        
        shipmentNoteComponent = new ShipmentNoteComponent("ShipmentNoteComponent");
        shipmentHeaderPanel.add(new JLabel(BiancaL.L(LOCALE_PREFIX + ".header.shipment.note", "Note")));
        shipmentHeaderPanel.add(shipmentNoteComponent.getComponent());
        // shipper
        shipperComponent = new ShipperComponent("ShipperComponent");
        shipmentHeaderPanel.add(new JLabel(BiancaL.L(LOCALE_PREFIX + ".header.shipment.shipper", "Shipper")));
        shipmentHeaderPanel.add(shipperComponent.getComponent());
        // Number of Item
        itemNoComponent = new ItemNoComponent("ItemNoComponent");
        shipmentHeaderPanel.add(new JLabel(BiancaL.L(LOCALE_PREFIX + ".header.shipment.ItemNo", "Item No")));
        shipmentHeaderPanel.add(itemNoComponent.getComponent());
        
    }
    @SuppressWarnings("serial")
    private void initActions() {
        SaveShipmentAction.getInstance();
        new ToolBarAction("action.shipment.print", "gui.images.print"){

            @Override
            public void init() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void initDefaults() {
                // TODO Auto-generated method stub
                
            }
            @Override
            public void onAction(ActionEvent e) {
                Shipment currentShipment = ShipmentPanel.this.getShipment();
                boolean isSaved = false;
                if (! saved){
                    isSaved = saveShipment(currentShipment);
                    if (!isSaved){
                        UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.notsaved", "error: Shipment not saved !"));
                        return;
                    } else{
                        ShipmentPanel.this.onSaving(); 
                    }
                }
                ShipmentService shipmentService = new ShipmentService();
                try {
                    shipmentService.printOut(currentShipment.getId(), shipment.getDocumentNo(), shipment.getShipmentDate());
                } catch (SQLException e1) {
                    BiancaLogger.getLogger().severe("SQL Error on printing " + e1);
                } catch (JRException e1) {
                    BiancaLogger.getLogger().severe("Jasper Error on printing " + e1);
                } catch (PDFException e1) {
                    BiancaLogger.getLogger().severe("PDF Error on printing " + e1);
                } catch (PDFSecurityException e1) {
                    BiancaLogger.getLogger().severe("PDF Security Error on printing " + e1);
                } catch (IOException e1) {
                    BiancaLogger.getLogger().severe("IO Error on printing " + e1);
                } catch (PrintException e1) {
                    BiancaLogger.getLogger().severe("Printer Error on printing " + e1);
                }
            }
            
        };
 
    
    }
    
    public boolean saveShipment(Shipment currentShipment) {
        
//        if (currentShipment.getDocStatus() != null && currentShipment.getDocStatus().equals(DOC_STATUS_COMPLETE) &&  saveonlyifnotcompleted.equals("1")){
//            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.error.title", "Error"), BiancaL.L(LOCALE_PREFIX + ".header.shipment.docStatus.complete", "error: Document is an Complete Status. It can be modified!"));
//            return false;
//        }
        
        if (currentShipment.getDocumentNo() == null || currentShipment.getDocumentNo().equals("")){
            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.error.title", "Error"), BiancaL.L(LOCALE_PREFIX + ".header.shipment.docNo.mandatory", "error: Document Number is mandatory!"));
            return false;
        }
        if (currentShipment.getBpartnerId() == null ){
            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.error.title", "Error"), BiancaL.L(LOCALE_PREFIX + ".header.shipment.customer.mandatory", "error: Customer is mandatory!"));
            return false;
        }
        if (currentShipment.getShipmentDate() == null ){
            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.error.title", "Error"), BiancaL.L(LOCALE_PREFIX + ".header.shipment.date.mandatory", "error: Date is mandatory!"));
            return false;
        }
        ShipmentService shipmentService = new ShipmentService();
        boolean isSaved = false;
        try {
            isSaved = shipmentService.save(currentShipment, ShipmentPanel.this.getShipmentLine());
        } catch (SQLException e1) {
            BiancaLogger.getLogger().severe("Error in save form ShipmentPanel: " + e1);
        }
        return isSaved;
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

 
    
    public void setShipment(Shipment shipment){
        this.shipment = shipment;
       
        if (shipment == null){
            return;
        }
        this.shipmentComponent.setValue(shipment.getDocumentNo());
        if (shipment.getBpartnerId() != null){
            this.customerComponent.setValue((new CustomerSqlMapDao()).getName(shipment.getBpartnerId()));            
        } else {
            this.customerComponent.setValue(null);
        }
  
        if (shipment.getBpartnerLocationId() != null){
            this.customerLocationComponent.setValue((new CustomerLocationSqlMapDao()).getCustomerShipmentLocation(shipment.getBpartnerLocationId()));            
        } else {
            this.customerLocationComponent.setValue(null);
        }

        
        
        this.shipmentNoteComponent.setValue(shipment.getDescription());
        if (shipment.getShipperId() != null){
            this.shipperComponent.setValue((new CustomerSqlMapDao()).getName(shipment.getShipperId()));                     
        } else {
            this.shipperComponent.setValue("Meoni Dante");
        }
        if (shipment.getItemNo() != null){
            this.itemNoComponent.setValue(shipment.getItemNo().toString());
        } else {
            this.itemNoComponent.setValue(null);
        }
        // date of invoice
        if (shipment.getShipmentDate() != null){
            this.shipmentDate.setValue(shipment.getShipmentDate());            
        }
    }
    
    public Shipment getShipment(){
        shipment.setDocumentNo(this.shipmentComponent.getValue());
        shipment.setBpartnerId((new CustomerSqlMapDao()).getId(this.customerComponent.getValue()));
        shipment.setDescription(this.shipmentNoteComponent.getValue());
        
        shipment.setShipperId((new CustomerSqlMapDao()).getId(this.shipperComponent.getValue()));
        try{
            shipment.setBpartnerLocationId(Integer.valueOf(this.customerLocationComponent.getKey()));
        }catch (Exception e) {
            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.error.title", "Error"), BiancaL.L(LOCALE_PREFIX + ".header.shipment.date.formatError", "error: Wrong Format Date dd//mm//yyyy !"));
        }
        Date shipmentDate = null;
        try{
            shipmentDate = (Date) this.shipmentDate.getValue();
        }catch (Exception e) {
            UserIO.getInstance().requestMessageDialog(BiancaL.L(LOCALE_PREFIX + ".header.shipment.error.title", "Error"), BiancaL.L(LOCALE_PREFIX + ".header.shipment.customer.formatError", "error: Wrong Format for the customer !"));
        }
        shipment.setShipmentDate(shipmentDate);
        if (this.itemNoComponent.getValue() != null && !this.itemNoComponent.getValue().equals("") ){
            Integer itemNoComponentInt = ValidatorUtils.checkInteger(this.itemNoComponent.getValue());
            shipment.setItemNo(itemNoComponentInt);
        } else {
            shipment.setItemNo(null);
        }
        return shipment;
    }
    
    public List<ShipmentLine> getShipmentLine(){
        return shipmentLinesPanel.getShipmentLine();
    }
    
    public void setShipmentLine(List<ShipmentLine> shipmentLines){
        shipmentLinesPanel.setShipmentLine(shipmentLines);
    }

    @Override
    public void onNewing() {
        Shipment shipment = new Shipment();
//        shipmentLinesPanel.loseFocusonTable();
        if (this.shipmentDate != null){
            shipment.setDocumentNo((new ShipmentSqlMapDao()).getDocumentNo((Date) this.shipmentDate.getValue()));            
        }else{
            shipment.setDocumentNo((new ShipmentSqlMapDao()).getDocumentNo(new Date()));
        }
        setShipment(shipment );  
        setShipmentLine(null);
        shipmentComponent.getComponent().setEnabled(true);
        shipmentComponent.getComponent().requestFocus();
        
        shipmentLinesPanel.addEmptyRow();
       
        ActionController.getToolBarAction("action.shipment.save").setEnabled(true);
        saved = false;
        
    }

    @Override
    public void onSaving() {
        shipmentLinesPanel.loseFocusonTable();
        ActionController.getToolBarAction("action.shipment.save").setEnabled(false);
        shipmentComponent.getComponent().setEnabled(false);
        saved = true;
    }

    @Override
    public void onUpdating() {
        ActionController.getToolBarAction("action.shipment.save").setEnabled(true);
        saved = false;
    }
}
