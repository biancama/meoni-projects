package com.biancama.gui.easyShipment.persistence.iface;

import java.util.List;

import com.biancama.gui.easyShipment.model.Customer;

public interface CustomerDao {
    List<Customer> getAllCustomers();
    List<Customer> getAllEmployees();
    
    String getName(int id);
    
    Integer getId(String value);

    Customer getCustomer(int bpartnerId);

    Integer getDefaultSalesRep();

    Integer getShipmentAddress(int bpartnerId);

    Integer getContact(int bpartnerId);

    Integer getBillPartner(int bpartnerId);

    Integer getBillPartnerLocationId(int billBPartnerId, int shipmentLocationId);

    Integer getPayPartner(int bpartnerId);

    Integer getPayPartnerLocationId(int payBPartnerId);
}
