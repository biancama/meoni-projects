package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.util.List;

import com.biancama.gui.easyShipment.model.Customer;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.CustomerDao;
import com.biancama.log.BiancaLogger;

public class CustomerSqlMapDao implements CustomerDao  {

    
    @SuppressWarnings("unchecked")
    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> result = null;
        Customer dummyCustomer = new Customer();
        try {
            result = DaoConfig.getInstance().getSqlMapper().queryForList("getCustomerList", dummyCustomer);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query all customer: " + e);
        }
        return result;
    }

    @Override
    public String getName(int id) {
       String result = null;
       try {
           result = (String) DaoConfig.getInstance().getSqlMapper().queryForObject("getCustomerName", id);
       } catch (SQLException e) {
          BiancaLogger.getLogger().severe("Error query get Customer Name: " + e);
       }
      return result;
    }

    public Integer getId(String value) {
       Integer id = null;
       try {
           id = (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getCustomerId", value);
       } catch (SQLException e) {
          BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
       }
           return id;           
    }

    @Override
    public Customer getCustomer(int bpartnerId) {
        Customer result = null;
        try {
            result = (Customer) DaoConfig.getInstance().getSqlMapper().queryForObject("getCustomer", bpartnerId);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Name: " + e);
        }
       return result;
       
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getBillPartner(int bpartnerId) {
        List<Integer> ids = null;
        try {
            ids =  DaoConfig.getInstance().getSqlMapper().queryForList("getBillPartner", bpartnerId);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
        }
        if (ids == null || ids.isEmpty()){
            return null;
        }else{
            return ids.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getBillPartnerLocationId(int billBPartnerId, int shipmentLocationId) {
        List<Integer> ids = null;
        try {
            ids =  DaoConfig.getInstance().getSqlMapper().queryForList("getBillPartnerLocationId", billBPartnerId);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
        }
        if (ids == null || ids.isEmpty()){
            return null;
        }else{
            // if shipment Location is bill location then return shipment location chosen,
            // otherwise the first one
            for (Integer id : ids) {
                if (id.equals(shipmentLocationId)){
                    return id;
                }
            }
            return ids.get(0);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getContact(int bpartnerId) {
        List<Integer> ids = null;
        try {
            ids =  DaoConfig.getInstance().getSqlMapper().queryForList("getContact", bpartnerId);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
        }
        if (ids == null || ids.isEmpty()){
            return null;
        }else{
            return ids.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getDefaultSalesRep() {
        List<Integer> ids = null;
        Customer dummyCustomer = new Customer();
        try {
            ids =  DaoConfig.getInstance().getSqlMapper().queryForList("getDefaultSalesRep", dummyCustomer);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
        }
        if (ids == null || ids.isEmpty()){
            return null;
        }else{
            return ids.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getPayPartner(int bpartnerId) {
        List<Integer> ids = null;
        try {
            ids =  DaoConfig.getInstance().getSqlMapper().queryForList("getPayPartner", bpartnerId);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
        }
        if (ids == null || ids.isEmpty()){
            return null;
        }else{
            return ids.get(0);
        }

    }
    @SuppressWarnings("unchecked")
    @Override
    public Integer getPayPartnerLocationId(int payBPartnerId) {
        List<Integer> ids = null;
        try {
            ids =  DaoConfig.getInstance().getSqlMapper().queryForList("getPayPartnerLocationId", payBPartnerId);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
        }
        if (ids == null || ids.isEmpty()){
            return null;
        }else{
            return ids.get(0);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getShipmentAddress(int bpartnerId) {
        List<Integer> ids = null;
        try {
            ids =  DaoConfig.getInstance().getSqlMapper().queryForList("getShipmentAddress", bpartnerId);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query get Customer Id: " + e);
        }
        if (ids == null || ids.isEmpty()){
            return null;
        }else{
            return ids.get(0);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Customer> getAllEmployees() {
        List<Customer> result = null;
        Customer dummyCustomer = new Customer();
        try {
            result = DaoConfig.getInstance().getSqlMapper().queryForList("getEmployeesList", dummyCustomer);
        } catch (SQLException e) {
           BiancaLogger.getLogger().severe("Error query all employees: " + e);
        }
        return result;

    }


}
