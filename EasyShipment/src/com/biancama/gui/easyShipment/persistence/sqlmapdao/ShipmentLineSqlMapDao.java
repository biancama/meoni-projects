package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.ShipmentLine;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.ShipmentLineDao;

public class ShipmentLineSqlMapDao implements ShipmentLineDao {

    @Override
    public boolean insert(ShipmentLine shipmentLine) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("insertShipmentLine", shipmentLine);
        return true;
    }

    @Override
    public boolean update(ShipmentLine shipmentLine) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().update("updateShipmentLine", shipmentLine);
        return true;
        
    }

    @Override
    public ShipmentLine getShipmentLine(Integer id) throws SQLException {
        return (ShipmentLine) DaoConfig.getInstance().getSqlMapper().queryForObject("getShipmentLine", id);        
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentLine> getShipmentLines(Integer shipmentId) throws SQLException {
        ShipmentLine shipmentLineDummy = new ShipmentLine();
        shipmentLineDummy.setShipmentId(shipmentId);
        return DaoConfig.getInstance().getSqlMapper().queryForList("getShipmentLines", shipmentLineDummy);        
    }

    @Override
    public boolean remove(Integer id) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().delete("removeShipmentLine", id);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Integer> getShipmentLinesIds(Integer shipmentId) throws SQLException {
        return DaoConfig.getInstance().getSqlMapper().queryForList("getShipmentLinesIds", shipmentId);       
    }

    @Override
    public void removeShipmentLinesId(List<Integer> shipmentLinesIds) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().startBatch();
        for (Integer id : shipmentLinesIds) {
            this.remove(id);
        }
        DaoConfig.getInstance().getSqlMapper().executeBatch();
    }

}
