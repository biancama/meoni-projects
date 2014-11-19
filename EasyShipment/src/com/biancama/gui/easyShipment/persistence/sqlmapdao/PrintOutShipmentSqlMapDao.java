package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.PrintOutShipment;
import com.biancama.gui.easyShipment.model.PrintOutShipmentLine;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.PrintOutShipmentDao;

public class PrintOutShipmentSqlMapDao implements PrintOutShipmentDao {

    @Override
    public PrintOutShipment getShipment(Integer id) throws SQLException {
        return (PrintOutShipment) DaoConfig.getInstance().getSqlMapper().queryForObject("getPrintOutShipment", id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PrintOutShipmentLine> getShipmentLine(Integer shipmentId) throws SQLException {
        return DaoConfig.getInstance().getSqlMapper().queryForList("getPrintOutShipmentLine", shipmentId);
    }

}
