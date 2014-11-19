package com.biancama.gui.easyShipment.persistence.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.print.PrintException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import com.biancama.config.Configuration;
import com.biancama.gui.easyShipment.model.AttributeInstance;
import com.biancama.gui.easyShipment.model.Customer;
import com.biancama.gui.easyShipment.model.Order;
import com.biancama.gui.easyShipment.model.OrderLine;
import com.biancama.gui.easyShipment.model.Product;
import com.biancama.gui.easyShipment.model.Shipment;
import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.easyShipment.model.TestB;
import com.biancama.gui.easyShipment.model.TestLineB;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.AttributeInstanceDao;
import com.biancama.gui.easyShipment.persistence.iface.CustomerDao;
import com.biancama.gui.easyShipment.persistence.iface.MLocatorDao;
import com.biancama.gui.easyShipment.persistence.iface.OrderDao;
import com.biancama.gui.easyShipment.persistence.iface.OrderLineDao;
import com.biancama.gui.easyShipment.persistence.iface.ProductDao;
import com.biancama.gui.easyShipment.persistence.iface.SequenceDao;
import com.biancama.gui.easyShipment.persistence.iface.ShipmentDao;
import com.biancama.gui.easyShipment.persistence.iface.ShipmentLineDao;
import com.biancama.gui.easyShipment.persistence.iface.TestBDao;
import com.biancama.gui.easyShipment.persistence.iface.WarehouseDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.AttributeInstanceSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.CustomerSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.MLocatorSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.OrderLineSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.OrderSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.ProductSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.SequenceSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.ShipmentLineSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.ShipmentSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.TestBSqlMapDao;
import com.biancama.gui.easyShipment.persistence.sqlmapdao.WarehouseSqlMapDao;
import com.biancama.utils.DatabaseUtils;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.file.FileControl;
import com.biancama.utils.printer.JobPrinter;
import com.ibatis.sqlmap.client.SqlMapClient;

public class ShipmentService {
    public static final String DELIVERYRULE_Availability = "A";
    private static final String REPORT_NAME_B = "reports/shipmentB.jasper";
    private static final String REPORT_NAME = "reports/shipment.jasper";
    /** Customer Schedule after Delivery = S */
    public static final String INVOICERULE_CustomerScheduleAfterDelivery = "S";
    
    /** Pickup = P */
    public static final String DELIVERYVIARULE_Pickup = "P";
    
    private enum ShipmentLineType{NORMAL, B_TYPE, BOTH, NOTHING}
    
    // this map is used to store attribute instance news not already committed
    // otherwise if I have two same value the system perform two insert since the
    // first it's not committed
    private Map<String, Integer> tempAttributeInstance = new HashMap<String, Integer>();
    
    public boolean save(Shipment shipment, List<ShipmentLine> lines) throws SQLException{
        tempAttributeInstance.clear();
        SqlMapClient sqlMapClient = DaoConfig.getInstance().getSqlMapper();
        Customer customer = fillCustomer(shipment.getBpartnerId(), shipment.getBpartnerLocationId());
        
        try{
            sqlMapClient.startTransaction();
            boolean result = false;
            if (shipment.getId() != null){
                result =   update(customer,shipment, lines );
            }else{
                result = insert(customer,shipment, lines );
            }
            
            sqlMapClient.commitTransaction();  
            return result;
            
        }finally {
            sqlMapClient.endTransaction();
        }        
    }
    
    public Shipment getShipment(String shipmentNo, Date d) throws SQLException{
        ShipmentDao shipmentDao = new ShipmentSqlMapDao();
        return shipmentDao.getShipmentFromDocumentNo(shipmentNo, d);
        
    }

    private Customer fillCustomer(int bpPartnerId, int shipmentLocationId) throws SQLException {
        CustomerDao customerDao = new CustomerSqlMapDao();
        Customer customer = customerDao.getCustomer(bpPartnerId);
        if (customer.getSalesRep() == null){
            customer.setSalesRep(customerDao.getDefaultSalesRep());
        }
        if (customer.getDeliveryRule() == null || customer.getDeliveryRule().equals("") ){
            customer.setDeliveryRule(DELIVERYRULE_Availability);
        }
        if (customer.getInvoiceRule() == null || customer.getInvoiceRule().equals("") ){
            customer.setInvoiceRule(INVOICERULE_CustomerScheduleAfterDelivery);
        }
        if (customer.getDeliveryViaRule() == null || customer.getDeliveryViaRule().equals("") ){
            customer.setDeliveryViaRule(DELIVERYVIARULE_Pickup);
        }
        if (customer.getPaymentTermId() == null){
            OrderDao orderDao = new OrderSqlMapDao();
            customer.setPaymentTermId(orderDao.getDefaultPaymentTerm());
        }

        // set default location
        customer.setLocationId(customerDao.getShipmentAddress(bpPartnerId));
        // set Contact
        customer.setBillUserId(customerDao.getContact(bpPartnerId));
        // set bill Partner 
        customer.setBillBPartnerId(customerDao.getBillPartner(bpPartnerId));
        // set Bill Partner location
        customer.setBillLocationId(customerDao.getBillPartnerLocationId(customer.getBillBPartnerId(), shipmentLocationId));
        // set pay Partner 
        customer.setPayBPartnerId(customerDao.getPayPartner(bpPartnerId));
        // set pay Partner location
        customer.setPayLocationId(customerDao.getPayPartnerLocationId(customer.getPayBPartnerId()));
        return customer;
    }

    private boolean insert(Customer customer,  Shipment shipment, List<ShipmentLine> lines) throws SQLException {
        int defaultWarehouseId = getDefaultWarehouse();
        Order orderNew = createOrder(customer, shipment, defaultWarehouseId, shipment.getShipmentDate());
        Shipment shipmentNew = createShipment(customer, shipment, orderNew.getId(), defaultWarehouseId );
        shipment.setId(shipmentNew.getId());
        shipment.setOrderId(orderNew.getId());
        int line = 10;
        Map<BigDecimal, BigDecimal> taxRates = new HashMap<BigDecimal, BigDecimal>(); // it contains tax rates for every type of rate
        BigDecimal totalLines = new BigDecimal(0);
        for (ShipmentLine shipmentLine : lines) {
            ShipmentLineType type = checkType(shipmentLine);
            Product productFilled = null;
            OrderLine orderLineNew = null;
            if (type != ShipmentLineType.NOTHING){
                Product productForIn = shipmentLine.getProduct();
                productForIn.setPriceListId(orderNew.getPricelistId());
                productForIn.setValidFrom(shipment.getShipmentDate());
                productFilled = getProductFilled(productForIn, shipmentLine.getPrice());
                
            }
            ShipmentLine shipmentLineNew = null;
            if (type == ShipmentLineType.NORMAL || type == ShipmentLineType.BOTH ){
                orderLineNew = createOrderLine(orderNew, productFilled, shipmentLine.getQuantity(), shipmentLine.getPrice(), line);
                totalLines = totalLines.add(orderLineNew.getLinenetamt());
                BigDecimal rate = orderLineNew.getRate().divide(new BigDecimal(100));
                if (!taxRates.containsKey(rate)){
                    taxRates.put(rate, orderLineNew.getLinenetamt());
                } else {                
                    BigDecimal oldAmt = taxRates.get(rate);
                    taxRates.put(rate, oldAmt.add(orderLineNew.getLinenetamt()));
                }
                shipmentLineNew = createShipmentLine(shipmentNew, shipmentLine,productFilled, orderLineNew.getId(), line);
                shipmentLine.setId(shipmentLineNew.getId());                
                
            }
            if (type == ShipmentLineType.B_TYPE || type == ShipmentLineType.BOTH ){
                if (shipment.getIdB() == null){
                    Integer idB = createTestB(shipment.getId(), shipment.getShipmentDate(), shipment.getDocumentNo(), (new CustomerSqlMapDao()).getName(shipment.getBpartnerId()) );
                    shipment.setIdB(idB);
                }               
                insertAttributeInstanceId(shipmentLine);
                TestLineB testLineB = null;
                if (shipmentLineNew !=  null){
                    testLineB = createTestLineB(shipment, shipmentLine.getAttributeistanceId(), shipmentLine.getLot(),  shipmentLine.getQuantityB(), line, productFilled, shipmentLineNew.getId() );                                        
                } else {
                    testLineB = createTestLineB(shipment, shipmentLine.getAttributeistanceId(), shipmentLine.getLot(),  shipmentLine.getQuantityB(), line, productFilled, null );
                }
                shipmentLine.setIdB(testLineB.getId());               
            }
            if (type == ShipmentLineType.B_TYPE|| type == ShipmentLineType.BOTH || type == ShipmentLineType.NORMAL){
                line += 10;
            }
        }
        // calculation of grandTotal
        BigDecimal grandTotal = new BigDecimal(0);
        for (Map.Entry<BigDecimal, BigDecimal> entry: taxRates.entrySet()){
            grandTotal = grandTotal.add(entry.getKey().multiply(entry.getValue()));
        }
        grandTotal = grandTotal.add(totalLines);
        updateGrandTotal(orderNew, totalLines, grandTotal);
        return true;
    }
    
    private TestLineB createTestLineB(Shipment shipment, Integer attributeInstanceId, String lot, BigDecimal quantityB, int line, Product productFilled, Integer shipmentLineId) throws SQLException {
        TestLineB testLineB = new TestLineB();
        testLineB.setShipmentId(shipment.getId());
        testLineB.setTestId(shipment.getIdB());
        testLineB.setShipmentLineId(shipmentLineId);
        testLineB.setLine(line);
        
        testLineB.setAttributesetinstanceId(attributeInstanceId);
        testLineB.setLot(lot);
        testLineB.setProductId(productFilled.getId());
        testLineB.setPruduct(productFilled.getName());
        testLineB.setQuantity(quantityB);
        TestBDao testBDao = new TestBSqlMapDao();
        Integer id = testBDao.insertLine(testLineB);
        testLineB.setId(id);
        return testLineB;
    }
    
    private boolean updateTestLineB(Integer testLineBId, Integer attributeInstanceId, String lot, Product productFilled, BigDecimal quantityB, int line) throws SQLException{
        TestLineB testLineB = new TestLineB();
        testLineB.setId(testLineBId);
        testLineB.setAttributesetinstanceId(attributeInstanceId);
        testLineB.setLot(lot);
        testLineB.setProductId(productFilled.getId());
        testLineB.setPruduct(productFilled.getName());
        testLineB.setQuantity(quantityB);
        testLineB.setLine(line);
        TestBDao testBDao = new TestBSqlMapDao();
        return testBDao.updateLine(testLineB);
    }

    private boolean update(Customer customer, Shipment shipment, List<ShipmentLine> lines) throws SQLException {
        int defaultWarehouseId = getDefaultWarehouse();
        Order orderNew = updateOrder(customer, shipment, defaultWarehouseId, shipment.getOrderId(), shipment.getShipmentDate());
        Shipment shipmentNew = updateShipment(customer, shipment, shipment.getId(), defaultWarehouseId );
        int line = 10;
        Map<BigDecimal, BigDecimal> taxRates = new HashMap<BigDecimal, BigDecimal>(); // it contains tax rates for every type of rat
        BigDecimal totalLines = new BigDecimal(0);
        List<Integer> oldShipmentLinesIds = getOldShipmentLinesIds(shipment.getId());
        boolean needToUpdate = true;
        for (ShipmentLine shipmentLine : lines) {
            ShipmentLineType type = checkType(shipmentLine);
            if (type == ShipmentLineType.NOTHING){
                continue;
            }
            Product productForIn = shipmentLine.getProduct();
            productForIn.setPriceListId(orderNew.getPricelistId()); 
            productForIn.setValidFrom(shipment.getShipmentDate());
            Product productFilled = getProductFilled(productForIn, shipmentLine.getPrice());
            ShipmentLine shipmentLineNew = null;
            if (type == ShipmentLineType.NORMAL || type == ShipmentLineType.BOTH ){
                
                if (shipmentLine.getId() == null){
                    // new row
                    OrderLine orderLineNew = createOrderLine(orderNew, productFilled, shipmentLine.getQuantity(), shipmentLine.getPrice(), line);
                    totalLines = totalLines.add(orderLineNew.getLinenetamt());
                    BigDecimal rate = orderLineNew.getRate().divide(new BigDecimal(100));
                    if (!taxRates.containsKey(rate)){
                        taxRates.put(rate, orderLineNew.getLinenetamt());
                    } else {                
                        BigDecimal oldAmt = taxRates.get(rate);
                        taxRates.put(rate, oldAmt.add(orderLineNew.getLinenetamt()));
                    }
                    shipmentLineNew = createShipmentLine(shipmentNew, shipmentLine,productFilled, orderLineNew.getId(), line);
                    shipmentLine.setId(shipmentLineNew.getId());   
                    oldShipmentLinesIds.remove(shipmentLineNew.getId());                  
                }else{
                    ShipmentLine shipmentLineLoaded = loadShipmentLine(shipmentLine.getId());
                    OrderLine orderLineNew = updateOrderLine(orderNew, productFilled, shipmentLine.getQuantity(), shipmentLine.getPrice(), line, shipmentLineLoaded.getOrderLineId());
                    totalLines = totalLines.add(orderLineNew.getLinenetamt());
                    BigDecimal rate = orderLineNew.getRate().divide(new BigDecimal(100));
                    if (!taxRates.containsKey(rate)){
                        taxRates.put(rate, orderLineNew.getLinenetamt());
                    } else {                
                        BigDecimal oldAmt = taxRates.get(rate);
                        taxRates.put(rate, oldAmt.add(orderLineNew.getLinenetamt()));
                    }
                    shipmentLineNew = updateShipmentLine(shipmentLine,productFilled, line, shipmentLineLoaded.getId());
                    oldShipmentLinesIds.remove(shipmentLine.getId());                                   
                }
                
            }
            if (type == ShipmentLineType.B_TYPE || type == ShipmentLineType.BOTH ){
                String lot = null;
                Integer attributeInstanceId = null;
                if (shipmentLine.getLot() != null){
                    lot = shipmentLine.getLot().trim();
                    if (tempAttributeInstance.containsKey(lot)){
                        attributeInstanceId = tempAttributeInstance.get(lot);
                    }else{
                        attributeInstanceId = getAttributeInstanceId(lot);
                        tempAttributeInstance.put(lot, attributeInstanceId);
                    }
                }
                if (shipment.getIdB() == null){
                    Integer idB = createTestB(shipment.getId(), new Date(), shipment.getDocumentNo(), (new CustomerSqlMapDao()).getName(shipment.getBpartnerId()) );
                    shipment.setIdB(idB);
                }else if (needToUpdate ){
                    updateTestB(shipment.getIdB(), (new CustomerSqlMapDao()).getName(shipment.getBpartnerId()));
                    needToUpdate = false;
                }
                if (shipmentLine.getIdB() == null){
                    
                    
                    TestLineB testLineB = null;
                    if (shipmentLineNew != null){                         
                        testLineB = createTestLineB(shipment, attributeInstanceId, lot, shipmentLine.getQuantityB(), line, productFilled, shipmentLineNew.getId());
                        shipmentLineNew.setIdB(testLineB.getId());
                    } else {
                        testLineB = createTestLineB(shipment, attributeInstanceId, lot, shipmentLine.getQuantityB(), line, productFilled, null);
                    }
                    shipmentLine.setIdB(testLineB.getId());
                } else {
                    // update old testLine
                    
                    updateTestLineB(shipmentLine.getIdB(), attributeInstanceId, lot, productFilled, shipmentLine.getQuantityB(), line);
                }
            }
            if ( type != ShipmentLineType.NOTHING){
                line += 10;  
            }
        }
        // calculation of grandTotal
        BigDecimal grandTotal = new BigDecimal(0);
        for (Map.Entry<BigDecimal, BigDecimal> entry: taxRates.entrySet()){
           grandTotal = grandTotal.add(entry.getKey().multiply(entry.getValue()));
        }
        grandTotal = grandTotal.add(totalLines);
        updateGrandTotal(orderNew, totalLines, grandTotal);
        removeExceededLines(oldShipmentLinesIds);
        return true;
    }
    private List<Integer> getOldShipmentLinesIds(Integer id) throws SQLException {
        ShipmentLineDao shipmentLineDao = new ShipmentLineSqlMapDao();        
        return shipmentLineDao.getShipmentLinesIds(id);
    }

    private void removeExceededLines(List<Integer> shipmentLinesIds) throws SQLException {
        OrderLineDao orderLineDao = new OrderLineSqlMapDao();        
        orderLineDao.removeOrderLinesIds(shipmentLinesIds);
        
        ShipmentLineDao shipmentLineDao = new ShipmentLineSqlMapDao();        
        shipmentLineDao.removeShipmentLinesId(shipmentLinesIds);
        
    }

    private ShipmentLine loadShipmentLine(Integer id) throws SQLException {
        ShipmentLineDao shipmentLineDao = new ShipmentLineSqlMapDao();        
        return shipmentLineDao.getShipmentLine(id);
    }

    private void updateGrandTotal(Order orderNew, BigDecimal totalLines, BigDecimal grandTotal) throws SQLException {
        orderNew.setGrandTotal(grandTotal);
        orderNew.setTotalLines(totalLines);
        OrderDao orderDao = new OrderSqlMapDao();
        orderDao.updateGrandTotal(orderNew);
    }

    private int getDefaultWarehouse() {
        WarehouseDao warehouseDao = new WarehouseSqlMapDao();

        int defaultWarehouseId = warehouseDao.getDefault();
        return defaultWarehouseId;
    }

    private ShipmentLineType checkType(ShipmentLine shipmentLine) {
        if (shipmentLine.getProduct() == null ){
            return ShipmentLineType.NOTHING;
        }
        boolean qty = (shipmentLine.getQuantity() != null && shipmentLine.getQuantity().compareTo(new BigDecimal(0))  > 0);
        boolean qtyB = (shipmentLine.getQuantityB() != null && shipmentLine.getQuantityB().compareTo(new BigDecimal(0))  > 0);
        if (qty && qtyB ){
            return ShipmentLineType.BOTH;
        }else if (qty && !qtyB){
            return ShipmentLineType.NORMAL;
        }else if (!qty && qtyB){
            return ShipmentLineType.B_TYPE;
        }else if (!qty && !qtyB){
            return ShipmentLineType.NOTHING;
        }
        return null;
    }

    private ShipmentLine createShipmentLine(Shipment shipmentNew, ShipmentLine shipmentLine,Product product, Integer orderLineId, Integer line) throws SQLException {
        SequenceDao sequenceDao =  new SequenceSqlMapDao();
        int shipmentId = sequenceDao.getNextId("M_InOutLine");
        ShipmentLine shipmentLineNew = new ShipmentLine();
        shipmentLineNew.setId(shipmentId);
        
        shipmentLineNew.setLine(line);
        
        copyShipmentLineInfoInShipmentLineNew(shipmentLine, shipmentLineNew);
        shipmentLineNew.setShipmentId(shipmentNew.getId());
        
        shipmentLineNew.setOrderLineId(orderLineId);
        
        copyProductInfoInShipmenLineNew(product, shipmentLineNew);
        // Lot
        insertAttributeInstanceId(shipmentLineNew);
        
        // save the shipmentLine
        ShipmentLineDao shipmentLineDao = new ShipmentLineSqlMapDao();
        if (shipmentLineDao.insert(shipmentLineNew)){
            return shipmentLineNew;
        }else{
            return null;
        }

    }
    private ShipmentLine updateShipmentLine(ShipmentLine shipmentLine,Product product, Integer line, Integer shipmentId) throws SQLException {
        ShipmentLine shipmentLineNew = new ShipmentLine();
        shipmentLineNew.setId(shipmentId);
        
        shipmentLineNew.setLine(line);
        
        copyShipmentLineInfoInShipmentLineNew(shipmentLine, shipmentLineNew);
        
        copyProductInfoInShipmenLineNew(product, shipmentLineNew);
        // Lot
        insertAttributeInstanceId(shipmentLineNew);
        
        // save the shipmentLine
        ShipmentLineDao shipmentLineDao = new ShipmentLineSqlMapDao();
        if (shipmentLineDao.update(shipmentLineNew)){
            return shipmentLineNew;
        }else{
            return null;
        }

    }

    private void insertAttributeInstanceId(ShipmentLine shipmentLine) throws SQLException {
        if (shipmentLine.getLot() != null && !shipmentLine.getLot().trim().equals("")){
            String lotValue = shipmentLine.getLot().trim();
            if (tempAttributeInstance.get(lotValue)== null){
                Integer attributeInstanceId = getAttributeInstanceId(lotValue);
                shipmentLine.setAttributeistanceId(attributeInstanceId);
                tempAttributeInstance.put(lotValue, attributeInstanceId);
            }else{
                shipmentLine.setAttributeistanceId(tempAttributeInstance.get(lotValue));
            }
        }
    }

    private Integer getAttributeInstanceId(String lotValue) throws SQLException {
        
        AttributeInstanceDao attributeInstanceDao = new AttributeInstanceSqlMapDao();
        AttributeInstance attributeInstance = new AttributeInstance();
        
        attributeInstance.setValue(lotValue);
        Integer attributeInstanceId = attributeInstanceDao.getAttributeInstance(attributeInstance);
        if (attributeInstanceId == null){
            // create a new attribute instance
            SequenceDao sequenceDaoForLot =  new SequenceSqlMapDao();
            int newAttributeInstanceId = sequenceDaoForLot.getNextId("M_AttributeSetInstance");
            attributeInstance.setId(newAttributeInstanceId);
            Integer attributeId = attributeInstanceDao.getAttributeId(attributeInstance);
            Integer attributeSetId = attributeInstanceDao.getAttributeSetId(attributeInstance);
            attributeInstance.setAttributeId(attributeId);
            attributeInstance.setAttributeSetId(attributeSetId);
            attributeInstanceDao.insert(attributeInstance);
            attributeInstanceId = attributeInstance.getId();          
        } 
        return attributeInstanceId;
    }

    private void copyProductInfoInShipmenLineNew(Product product, ShipmentLine shipmentLineNew) {
        if (product.getLocatorId() == null){
            MLocatorDao mLocatorDao = new MLocatorSqlMapDao();
            shipmentLineNew.setLocatorId(mLocatorDao.getDefault());        
        } else {
            shipmentLineNew.setLocatorId(product.getLocatorId());            
        }
        shipmentLineNew.setProductId(product.getId());
        shipmentLineNew.setUomId(product.getUomId());
    }

    private void copyShipmentLineInfoInShipmentLineNew(ShipmentLine shipmentLine, ShipmentLine shipmentLineNew) {
        shipmentLineNew.setLot(shipmentLine.getLot());
        shipmentLineNew.setQuantity(shipmentLine.getQuantity());
    }

    private OrderLine createOrderLine(Order orderNew, Product product, BigDecimal qty, BigDecimal price, Integer line) throws SQLException {
        SequenceDao sequenceDao =  new SequenceSqlMapDao();
        int orderLineId = sequenceDao.getNextId("C_OrderLine");
        OrderLine orderLine = new OrderLine();
        orderLine.setId(orderLineId);
        
        copyOrderInfoinOrderLine(orderNew, orderLine);
        // price
        copyProductInfoInOrderLine(product, orderLine, price);
        // shipmentLine
        orderLine.setQty(qty);
        
        orderLine.setLine(line);
        // save the order Line
        OrderLineDao orderLineDao = new OrderLineSqlMapDao();
        if (orderLineDao.insert(orderLine)){
            return orderLine;
        }else{
            return null;
        }
    }
    private OrderLine updateOrderLine(Order orderNew, Product product, BigDecimal qty, BigDecimal price, Integer line, Integer orderLineId) throws SQLException {
        OrderLine orderLine = new OrderLine();
        orderLine.setId(orderLineId);
        
        copyOrderInfoinOrderLine(orderNew, orderLine);
        // price
        copyProductInfoInOrderLine(product, orderLine, price);
        // shipmentLine
        orderLine.setQty(qty);
        
        orderLine.setLine(line);
        // save the order Line
        OrderLineDao orderLineDao = new OrderLineSqlMapDao();
        if (orderLineDao.update(orderLine)){
            return orderLine;
        }else{
            return null;
        }
    }

    private void copyProductInfoInOrderLine(Product product, OrderLine orderLine, BigDecimal price) throws SQLException {
        if (price != null && price.compareTo(new BigDecimal(0)) > 0){
            orderLine.setPrice(price);
        } else {            
            orderLine.setPrice(product.getPrice());
        }
        orderLine.setProductId(product.getId());
        orderLine.setTaxId(product.getTaxId());
        orderLine.setRate(product.getRate());
        orderLine.setUomId(product.getUomId());
    }

    private Product getProductFilled(Product productForIn, BigDecimal price) throws SQLException {
        ProductDao productDao = new ProductSqlMapDao();
        Product product = productDao.fillWithPrice(productForIn);
        product.setPrice(price);
        return product;
    }

    private void copyOrderInfoinOrderLine(Order orderNew, OrderLine orderLine) {
        orderLine.setBpartnerId(orderNew.getBpartnerId());
        orderLine.setBpartnerLocationId(orderNew.getBpartnerLocationId());
        orderLine.setCurrencyId(orderNew.getCurrencyId());
        orderLine.setOrderId(orderNew.getId());
       
        orderLine.setWarehouseId(getDefaultWarehouse());
    }

    private Shipment createShipment(Customer customer, Shipment shipmentFromPanel, int orderId, int warehouseId) throws SQLException {
        SequenceDao sequenceDao =  new SequenceSqlMapDao();
        int shipmentId = sequenceDao.getNextId("M_InOut");
        String documentNoInsert = shipmentFromPanel.getDocumentNo();
        
        Shipment shipment = new Shipment();
        shipment.setId(shipmentId);
        
        if ( documentNoInsert != null && !documentNoInsert.equals("")){
            shipment.setDocumentNo(documentNoInsert);
            sequenceDao.updateDocNumber(documentNoInsert, "MM Shipment", shipmentFromPanel.getShipmentDate());
        } else {            
            int documentNo = sequenceDao.getNextDocNumber("MM Shipment", shipmentFromPanel.getShipmentDate());
            shipment.setDocumentNo(String.valueOf(documentNo));
        }
        
        copyCustomerInfoToShipment(customer, warehouseId, shipment);
        
        copyShipmentFromPanelInfoToShipment(shipmentFromPanel, shipment);
        
        shipment.setOrderId(orderId);
        // save the shipment
        ShipmentDao shipmentDao = new ShipmentSqlMapDao();
        if (shipmentDao.insert(shipment)){
            return shipment;
        }else{
            return null;
        }
    }

    private void copyShipmentFromPanelInfoToShipment(Shipment shipmentFromPanel, Shipment shipment) {
        shipment.setDescription(shipmentFromPanel.getDescription());
        shipment.setShipperId(shipmentFromPanel.getShipperId());
        shipment.setItemNo(shipmentFromPanel.getItemNo());
        shipment.setShipmentDate(shipmentFromPanel.getShipmentDate());
        shipment.setBpartnerLocationId(shipmentFromPanel.getBpartnerLocationId());
    }

    private Shipment updateShipment(Customer customer, Shipment shipmentFromPanel, Integer shipmentId, int warehouseId) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setId(shipmentId);
        copyCustomerInfoToShipment(customer, warehouseId, shipment);
        copyShipmentFromPanelInfoToShipment(shipmentFromPanel, shipment);

        // save the shipment
        ShipmentDao shipmentDao = new ShipmentSqlMapDao();
        if (shipmentDao.update(shipment)){
            return shipment;
        }else{
            return null;
        }
    }

    private void copyCustomerInfoToShipment(Customer customer, int warehouseId, Shipment shipment) {
        shipment.setBpartnerId(customer.getId());
//        shipment.setBpartnerLocationId(customer.getLocationId());
        shipment.setDeliveryRule(customer.getDeliveryRule());
        shipment.setDeliveryViaRule(customer.getDeliveryViaRule());
        shipment.setSalesRep(customer.getSalesRep());
        shipment.setWarehouseId(warehouseId);
    }

    private Order createOrder(Customer customer,Shipment shipmentFromPanel, int warehouseId, Date date) throws SQLException {
        SequenceDao sequenceDao =  new SequenceSqlMapDao();
        int orderId = sequenceDao.getNextId("C_Order");
        int documentNo = sequenceDao.getNextDocNumber("Standard Order", date);
        Order order = new Order();
        order.setId(orderId);
        order.setDocumentNo(String.valueOf(documentNo));
        order.setDateordered(date);
        copyCustomerInfoInOrder(customer, order, shipmentFromPanel);
        // warehouse
        order.setWarehouseId(warehouseId);
        // insert Order
        OrderDao orderDao = new OrderSqlMapDao();
        if ( orderDao.insert(order)){
            return order;
        }else{
            return null;
        }
    }

    private Order updateOrder(Customer customer, Shipment shipmentFromPanel, int warehouseId, Integer orderId, Date date) throws SQLException {
        OrderDao orderDao = new OrderSqlMapDao();

        Order order = orderDao.getOrder(orderId);
        order.setDateordered(date);
        copyCustomerInfoInOrder(customer, order, shipmentFromPanel);
        // warehouse
        order.setWarehouseId(warehouseId);
        // update Order
        if ( orderDao.update(order)){
            return order;
        }else{
            return null;
        }
    }

    private void copyCustomerInfoInOrder(Customer customer, Order order, Shipment shipmentFromPanel) {
        order.setBillBPartnerId(customer.getBillBPartnerId());
        order.setBillLocationId(customer.getBillLocationId());
        order.setBillUserId(customer.getBillUserId());
        order.setBpartnerId(customer.getId());      
        order.setBpartnerLocationId(shipmentFromPanel.getBpartnerLocationId());
        order.setCurrencyId(customer.getCurrencyId());
        order.setDeliveryRule(customer.getDeliveryRule());
        order.setDeliveryViaRule(customer.getDeliveryViaRule());
        order.setInvoiceRule(customer.getInvoiceRule());
        order.setPayBPartnerId(customer.getPayBPartnerId());
        order.setPayLocationId(customer.getPayLocationId());
        order.setPaymentTermId(customer.getPaymentTermId());
        order.setPricelistId(customer.getPricelistId());
        order.setSalesRep(customer.getSalesRep());
    }

    public List<ShipmentLine> getShipmentLines(Integer id) throws SQLException {
        ShipmentLineDao shipmentLineDao = new ShipmentLineSqlMapDao();
        List<ShipmentLine> shipmentLines = shipmentLineDao.getShipmentLines(id);
        // add product and Lot
        ProductDao productDao = new ProductSqlMapDao();
        
        for (ShipmentLine shipmentLine : shipmentLines) {
            Product product = productDao.findById(shipmentLine.getProductId());
            shipmentLine.setProduct(product);
            if (shipmentLine.getAttributeistanceId() != null){
                shipmentLine.setLot(productDao.getLot(shipmentLine.getAttributeistanceId()));
            }
            
        }
        return shipmentLines;
    }
    
 
    private Integer createTestB(Integer shipmentId, Date dateOrdered, String documentNo, String customer) throws SQLException {
        TestBDao testBDao = new TestBSqlMapDao();
        TestB testB = new TestB();
        testB.setShipmentId(shipmentId);
        testB.setDateOrdered(dateOrdered);
        testB.setDocumentNo(documentNo);
        testB.setCustomer(customer);
        return testBDao.insert(testB);
    }
    
    private boolean updateTestB(Integer testBId,String customer) throws SQLException {
        TestB testBDummy = new TestB();
        TestBDao testBDao = new TestBSqlMapDao();
        testBDummy.setId(testBId);
        testBDummy.setCustomer(customer);
        return testBDao.update(testBDummy);
    }

 
    public void printOut(Integer id, String name, Date date) throws SQLException, JRException, PDFException, PDFSecurityException, IOException, PrintException {
  
        //check if needs B type
        Object obj = DatabaseUtils.getDatabaseConnector().getData(Configuration.NAME);
        if (obj == null){
            return;
        }
        Configuration configuration = (Configuration) obj;
        
        String folderToSaveShipment = configuration.getStringProperty(Configuration.Param.PARAM_PDF_DIRECTORY.toString());
        String folderToSaveShipmentB = folderToSaveShipment + "/B";
        
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy");
        String calendarYear = simpleDateFormatter.format(date);
        SimpleDateFormat simpleDateFormatterMonth = new SimpleDateFormat("MM");
        String calendarMonth = simpleDateFormatterMonth.format(date);
        
        TestBDao testBDao = new TestBSqlMapDao();
        Integer testB = testBDao.getShipmentLineB(id);
        if (testB > 0){
            // print B
            printOutReport(id, name,REPORT_NAME_B, folderToSaveShipmentB, calendarYear, calendarMonth);
            
        }
        printOutReport(id, name, REPORT_NAME, folderToSaveShipment, calendarYear, calendarMonth);
        // complete shipment and sales order
        ShipmentDao shipmentDao= new ShipmentSqlMapDao();
        Shipment shipment = shipmentDao.getShipmentFromId(id);
        shipmentDao.complete(id);
        OrderDao orderDao = new OrderSqlMapDao();
        orderDao.complete(shipment.getOrderId());
    }
    @SuppressWarnings("unchecked")
    private void printOutReport(Integer id, String name, String reportName, String folderToSaveShipment, String calendarYear, String calendarMonth) throws JRException, SQLException, PDFException, PDFSecurityException, IOException, PrintException {
        URL url = ClassLoader.getSystemResource(reportName);        
//            JasperDesign jasperDesign = JRXmlLoader.load( url.getFile());
//            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        
        Map parameters = new HashMap();
        parameters.put("shipmentId", id);
        //parameters.put("SUBREPORT_DIR", "" + File.separatorChar);
        parameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);
        
        //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, DaoConfig.getInstance().getConnection());
        //JRProperties.setProperty("net.sf.jasperreports.default.pdf.encoding", "iso-8859");
        JasperPrint jasperPrint = JasperFillManager.fillReport(url.getPath(), parameters, DaoConfig.getInstance().getDataSource().getConnection());
        //JasperViewer.viewReport(jasperPrint);
        // pdf
        if (!FileSystemUtils.isFolderExists(folderToSaveShipment + File.separatorChar + calendarYear)){
            FileSystemUtils.createFolder(folderToSaveShipment + File.separatorChar + calendarYear);
            FileSystemUtils.setRWXpermission(folderToSaveShipment + File.separatorChar + calendarYear);
        }
        if (!FileSystemUtils.isFolderExists(folderToSaveShipment + File.separatorChar + calendarYear + File.separatorChar+ calendarMonth)){
            FileSystemUtils.createFolder(folderToSaveShipment + File.separatorChar + calendarYear + File.separatorChar+ calendarMonth);
            FileSystemUtils.setRWXpermission(folderToSaveShipment + File.separatorChar + calendarYear + File.separatorChar+ calendarMonth);
        }

        String fileNameB =  folderToSaveShipment + File.separatorChar + calendarYear + File.separatorChar + calendarMonth +  File.separatorChar +FileControl.renameFileName(name) + ".pdf";
        JasperExportManager.exportReportToPdfFile(jasperPrint, fileNameB);
        FileSystemUtils.setRWpermission(fileNameB);
        viewPdf(fileNameB);
        JobPrinter.printPdfDocument(fileNameB, 2);
    }
    private void viewPdf(String filePath) {
        // build a controller
        SwingController controller = new SwingController();
        // Build a SwingViewFactory configured with the controller
        SwingViewBuilder factory = new SwingViewBuilder( controller);
        // Use the factory to build a JPanel that is pre-configured
        // with a complete, active Viewer UI.
        JPanel viewerComponentPanel = factory.buildViewerPanel();
        // Create a JFrame to display the panel in
        JFrame window = new JFrame( "Using the Viewer Component");
        window.getContentPane().add( viewerComponentPanel);
        window.pack();
        window.setVisible( true);
        // Open a PDF document to view
        controller.openDocument( filePath );
        
    }

}
