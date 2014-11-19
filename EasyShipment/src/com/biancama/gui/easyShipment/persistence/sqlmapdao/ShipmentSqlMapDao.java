package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.biancama.gui.easyShipment.model.Shipment;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.ShipmentDao;
import com.biancama.log.BiancaLogger;

public class ShipmentSqlMapDao implements ShipmentDao {

    @Override
    public String getDocumentNo(Date shipmentDate) {
        String result = null;
       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String calendarYear = sdf.format(shipmentDate);
        Shipment shipment = new Shipment();
        shipment.setCalendarYear(calendarYear);
        try {
            result = (String) DaoConfig.getInstance().getSqlMapper().queryForObject("getDocumentNo", shipment);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query document No: " + e);
        }
        return result;
    }

    @Override
    public boolean insert(Shipment shipment) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("insertShipment", shipment);
        return true;
    }

    @Override
    public boolean update(Shipment shipment) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().update("updateShipment", shipment);
        return true;
    }

    @Override
    public Shipment getShipmentFromDocumentNo(String docNumber, Date d) throws SQLException {
        
        if (d == null){
            d = new Date();
        }       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String calendarYear = sdf.format(d);
        Shipment shipment = new Shipment();
        shipment.setCalendarYear(calendarYear);
        shipment.setDocumentNo(docNumber);
        return (Shipment) DaoConfig.getInstance().getSqlMapper().queryForObject("getShipmentFromDocNo", shipment);
    }

    @Override
    public boolean complete(Integer id) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().update("completeShipment", id);
        return true;
    }

    @Override
    public Shipment getShipmentFromId(Integer id) throws SQLException {
        return (Shipment) DaoConfig.getInstance().getSqlMapper().queryForObject("getShipmentFromId", id);
    }

   
}
