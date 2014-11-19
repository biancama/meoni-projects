package com.biancama.gui.easyShipment.persistence.service.pricelist;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import javax.xml.xpath.*;
import javax.xml.parsers.*;
import java.util.logging.*;

import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ParseAnchorsTest {
    private static final String TEST_URI = "http://www.borsamercimodena.it/Listino.asp?dat=10/01/2011&tip=1&nomegr=SALUMI+E+GRASSINE&idgr=28&anno=2011&set=1";
    
    public static void main(String[] args) throws Exception {
        // Disable most Cobra logging.
        Logger.getLogger("org.lobobrowser").setLevel(Level.WARNING);
        UserAgentContext uacontext = new SimpleUserAgentContext();
        // In this case we will use a standard XML document
        // as opposed to Cobra's HTML DOM implementation.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        URL url = new URL(TEST_URI);
        InputStream in = url.openConnection().getInputStream();
        try {
            Reader reader = new InputStreamReader(in, "ISO-8859-1");
            Document document = builder.newDocument();
            // Here is where we use Cobra's HTML parser.            
            HtmlParser parser = new HtmlParser(uacontext, document);
            parser.parse(reader);
            // Now we use XPath to locate "a" elements that are
            // descendents of any "html" element.
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate("html//a", document, XPathConstants.NODESET);
            int length = nodeList.getLength();
            for(int i = 0; i < length; i++) {
                Element element = (Element) nodeList.item(i);
                System.out.println("## Anchor # " + i + ": " + element.getAttribute("href"));
            }
        } finally {
            in.close();
        }
    }
}
