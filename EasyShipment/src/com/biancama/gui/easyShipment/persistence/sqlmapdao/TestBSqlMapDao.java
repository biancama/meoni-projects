package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.Date;

import com.biancama.gui.easyShipment.model.TestB;
import com.biancama.gui.easyShipment.model.TestLineB;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.TestBDao;

public class TestBSqlMapDao implements TestBDao {

    @Override
    public TestB getTestB(String docNumber) throws SQLException {
        TestB testBDummy = new TestB();
        testBDummy.setDocumentNo(docNumber);
        testBDummy.setDateOrdered(new Date());
        return  (TestB) DaoConfig.getInstance().getSqlMapper().queryForObject("getTestB", testBDummy);
    }

    @Override
    public Integer insert(TestB testB) throws SQLException {
       return (Integer) DaoConfig.getInstance().getSqlMapper().insert("insertTestB", testB);
    }


    @Override
    public Integer insertLine(TestLineB testLineB) throws SQLException {
        return (Integer) DaoConfig.getInstance().getSqlMapper().insert("insertTestLineB", testLineB);    
    }

    @Override
    public boolean updateLine(TestLineB testLineB) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("updateTestLineB", testLineB);
        return true;
    }

    @Override
    public boolean update(TestB testB) throws SQLException {
        DaoConfig.getInstance().getSqlMapper().insert("updateTestB", testB);
        return true;
    }
    
    @Override
    public Integer getShipmentLineB(Integer shipmentId) throws SQLException {
        
        return (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getTestlineBCount", shipmentId);
    }


}
