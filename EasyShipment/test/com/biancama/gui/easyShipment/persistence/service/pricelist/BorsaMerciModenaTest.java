package com.biancama.gui.easyShipment.persistence.service.pricelist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.xml.sax.SAXException;


public class BorsaMerciModenaTest {
    @Test
    public void getSuiniLink() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException
    {
       BorsaMerciModena.getSalumiLink("http://www.borsamercimodena.it/listinosettimanale.asp");
    }
    @Test
    public void getAll() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException{
        String suiniLink = BorsaMerciModena.getSalumiLink("http://www.borsamercimodena.it/listinosettimanale.asp");
        Map<String, BigDecimal> map = BorsaMerciModena.getPriceList(suiniLink);
        assertTrue(map.size() > 0);
    }
    
    
}
