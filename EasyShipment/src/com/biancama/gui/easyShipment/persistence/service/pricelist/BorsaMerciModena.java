package com.biancama.gui.easyShipment.persistence.service.pricelist;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.biancama.utils.HTMLUtils;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class BorsaMerciModena {

    private static final String BORSA_MODENA_LINK="http://www.borsamercimodena.it";
    private static final String SALUMI_LINK="SALUMI E GRASSINE";
    private static final String XPATH_PRICE_TABLE = "/html/body/table[3]/tbody/tr/td[2]/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/table";
    public static String getSalumiLink(String uri) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException{
        Map<String, String> mapLinks = HTMLUtils.extractLinks(uri);
        for (String link : mapLinks.keySet()) {
            if (link.equals(SALUMI_LINK)){
                return BORSA_MODENA_LINK + "/" + mapLinks.get(link);
            }
        }
        return null;
    }
    
    public static Map<String, BigDecimal> getPriceList(String uri) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException{
        final HtmlTable table  = HTMLUtils.getHtmlTable(uri, XPATH_PRICE_TABLE);
        final Map<String, BigDecimal> mapResult = new HashMap<String, BigDecimal>(); 
        /*
        for (final HtmlTableRow row : table.getRows()) {
            System.out.println("Found row");
            for (final HtmlTableCell cell : row.getCells()) {
                System.out.println("   Found cell: " + cell.asText());
            }
        }  
        */
       
        int counter = 4;
        while (true){
            String key = table.getCellAt(counter,0).asText().trim();
            if (key.equals("")){
                break;
            }
            BigDecimal price = null;
            try{
                price = new BigDecimal(table.getCellAt(counter,2).asText().trim());
            }catch(NumberFormatException ex){
                price = new BigDecimal(0);                
            }catch(IndexOutOfBoundsException ex){
                break;
            }
            mapResult.put(key, price);
            counter ++;           
        }     
      
        return mapResult;
    }


}
