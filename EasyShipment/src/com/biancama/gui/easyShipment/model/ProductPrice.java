package com.biancama.gui.easyShipment.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductPrice extends Organization implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 2859739840062003244L;
    private Integer priceListVersionId;
    private Integer productId;
    private BigDecimal pricelist;
    private BigDecimal pricestd;
    private BigDecimal pricelimit;
    
    public Integer getPriceListVersionId() {
        return priceListVersionId;
    }
    public void setPriceListVersionId(Integer priceListVersionId) {
        this.priceListVersionId = priceListVersionId;
    }
    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    public BigDecimal getPricelist() {
        return pricelist;
    }
    public void setPricelist(BigDecimal pricelist) {
        this.pricelist = pricelist;
    }
    public BigDecimal getPricestd() {
        return pricestd;
    }
    public void setPricestd(BigDecimal pricestd) {
        this.pricestd = pricestd;
    }
    public BigDecimal getPricelimit() {
        return pricelimit;
    }
    public void setPricelimit(BigDecimal pricelimit) {
        this.pricelimit = pricelimit;
    }
    
    
}
