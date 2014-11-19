package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.OrderLine;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.OrderLineDao;

public class OrderLineSqlMapDao implements OrderLineDao {

    @Override
    public boolean insert(OrderLine orderLine) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("insertOrderLine", orderLine);
        return true;
    }

    @Override
    public boolean update(OrderLine orderLine) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().update("updateOrderLine", orderLine);
        return true;
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().delete("deleteOrderLine", id);
        return true;
    }

    @Override
    public void removeOrderLinesIds(List<Integer> shipmentLinesIds) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().startBatch();
        for (Integer shipmentLineId : shipmentLinesIds) {
            this.remove(shipmentLineId);
        }
        DaoConfig.getInstance().getSqlMapper().executeBatch();
    }

    @Override
    public boolean remove(Integer id) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().delete("deleteOrderLineFromShipmentLineId", id);
        return true;
    }

}
