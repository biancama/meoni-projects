package com.biancama.gui.easyShipment.model;

public abstract class Organization {
    protected Integer clientId;
    protected Integer orgId;
 
    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    
    protected Organization(){
        clientId = 1000000;         
        orgId = 1000000;
    }
    
}
