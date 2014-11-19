package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;

import com.biancama.gui.easyShipment.model.Order;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.OrderDao;

public class OrderSqlMapDao implements OrderDao {

    @Override
    public boolean insert(Order order) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("insertOrder", order);
            return true;
        
    }

    @Override
    public Integer getDefaultPaymentTerm() throws SQLException {
        Order orderDummy = new Order();
        
        return (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getDefaultPaymentTerm", orderDummy);
    }

    @Override
    public boolean updateGrandTotal(Order order) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().update("updateGrandTotal", order);
        return true;
    }

    @Override
    public boolean update(Order order) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().update("updateOrder", order);
        return true;
    }

    @Override
    public Order getOrder(Integer id) throws SQLException {
        return (Order) DaoConfig.getInstance().getSqlMapper().queryForObject("getOrder", id);
    }

    @Override
    public boolean complete(Integer id) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().update("completeOrder", id);
        return true;
    }

}
