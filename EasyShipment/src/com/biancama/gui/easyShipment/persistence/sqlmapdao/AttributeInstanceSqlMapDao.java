package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.AttributeInstance;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.AttributeInstanceDao;

public class AttributeInstanceSqlMapDao implements AttributeInstanceDao {

    @Override
    public Integer getAttributeId(AttributeInstance attributeInstance) throws SQLException {
        return (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getAttributeId", attributeInstance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getAttributeInstance(AttributeInstance attributeInstance) throws SQLException {
        List<Integer> allvalues = DaoConfig.getInstance().getSqlMapper().queryForList("getAttributeInstance", attributeInstance);
        if (allvalues == null || allvalues.size() == 0){
            return null;
        }else {
            return allvalues.get(0);
        }
    }

    @Override
    public boolean insert(AttributeInstance attributeInstance) throws SQLException{
        DaoConfig.getInstance().getSqlMapper().insert("insertAttributeSetInstance", attributeInstance);
        DaoConfig.getInstance().getSqlMapper().insert("insertAttributeInstance", attributeInstance);
        return true;
    }

    @Override
    public Integer getAttributeSetId(AttributeInstance attributeInstance) throws SQLException {
        return (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getAttributeSetId", attributeInstance);    
    }

}
