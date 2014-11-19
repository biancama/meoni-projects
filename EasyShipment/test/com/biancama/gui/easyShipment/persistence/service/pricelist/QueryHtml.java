package com.biancama.gui.easyShipment.persistence.service.pricelist;

/**
 * Demo of screenscraping using TagSoup and XPATH as described at 
 * http://blog.oroup.com/2006/11/05/the-joys-of-screenscraping/
 * 
 * This example class downloads the content of a page from Google
 * Finance and parses it for the Google stock price. It completely
 * omits all error handling for brevity. Also a lot of objects
 * should be cached and re-used if you were really going to call
 * this multiple times.
 * 
 * @author Oliver Roup <oroup@oroup.com>
 */

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

// The Tagsoup library.
import org.ccil.cowan.tagsoup.Parser;

public class QueryHtml {

  public static void main(String[] args) throws Exception {

    // Get the page and coerce it to an XML DOM. This loads the whole
    // thing into memory so massive pages should be cut down first
    // using SAX or something similar.
    Node node = getHtmlUrlNode("http://finance.google.com/finance?q=GOOG");

    // Create a mutable namespace context. This should really be provided
    // by the JDK, but the default implementation does not allow new entries
    // to be added.
    MutableNamespaceContext nc = new MutableNamespaceContext();

    // Set the prefix "html" to correspond to the xhtml namespace.
    // This can be called multiple times with different prefixes.
    nc.setNamespace("html", "http://www.w3.org/1999/xhtml");

    // This is the query we run against the DOM coereced from the web page.
    // If the HTML changes in a relevant way, it will break this query. 
    final String QUERY = "/html:html/html:body/html:table[2]/html:tr" + 
      "/html:td/html:table/html:tr[2]/html:td[2]/html:div/html:table" + 
      "/html:tr/html:td/html:span/html:span/text()";

    // Run the xpath query against the DOM.
    NodeList result = xPathQuery(node, QUERY, nc);
    
    // Print out the result.
    System.out.println(dumpNode(result.item(0), true));
  }

  /**
   * @param urlString The URL of the page to retrieve
   * @return A Node with a well formed XML doc coerced from the page.
   * @throws Exception if something goes wrong. No error handling at all
   * for brevity.
   */
  public static Node getHtmlUrlNode(String urlString) throws Exception {

    SAXTransformerFactory stf = 
      (SAXTransformerFactory) TransformerFactory.newInstance();
    TransformerHandler th = stf.newTransformerHandler();

    // This dom result will contain the results of the transformation
    DOMResult dr = new DOMResult();
    th.setResult(dr);

    Parser parser = new Parser();
    parser.setContentHandler(th);

    URL url = new URL(urlString);
      URLConnection urlConn = url.openConnection();
      InputStream stream = urlConn.getInputStream();
    
    // This is where the magic happens to convert HTML to XML
    parser.parse(new InputSource(stream));
    return dr.getNode();
  }
  
  /**
   * @param node An XML DOM Tree for query
   * @param query An XPATH query to run against the DOM Tree
   * @param nc The namespaceContext that maps prefixes to XML namespace
   * @return A list of nodes that result from running the query against
   * the node.
   * @throws Exception If anything goes wrong. No error handling for brevity
   */
  public static NodeList xPathQuery(Node node, String query, 
      NamespaceContext nc) throws Exception {
    XPathFactory xpf = XPathFactory.newInstance();
    XPath xpath = xpf.newXPath();
    xpath.setNamespaceContext(nc);
    return (NodeList) xpath.evaluate(query, node, XPathConstants.NODESET);
  }

  /**
   * @param node A node to be dumped to a string
   * @param omitDeclaration A boolean whether to omit the XML declaration
   * @return A string representation of the node.
   * @throws Exception If anything goes wrong. Error handling omitted.
   */
  public static String dumpNode(Node node, boolean omitDeclaration) 
    throws Exception {
    Transformer xformer = 
      TransformerFactory.newInstance().newTransformer();
    if (omitDeclaration) {
      xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }
    StringWriter sw = new StringWriter();
    Result result = new StreamResult(sw);
    Source source = new DOMSource(node);
    xformer.transform(source, result);
    return sw.toString();
  }
  
}

/** There is a bug in the JDK which omits the setNamespace declaration
 * from implementations of NamespaceContext. We have to create our
 * own implementation to work around it. Documented here:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5101859
 * @author Oliver Roup <oroup@oroup.com>
 */
class MutableNamespaceContext implements NamespaceContext {

  private Map<String, String> map;

  public MutableNamespaceContext() {
    map = new HashMap<String, String>();
  }

  public void setNamespace(String prefix, String namespaceURI) {
    map.put(prefix, namespaceURI);
  }

  public String getNamespaceURI(String prefix) {
    return map.get(prefix);
  }

  public String getPrefix(String namespaceURI) {
    for (String prefix : map.keySet()) {
      if (map.get(prefix).equals(namespaceURI)) {
        return prefix;
      }
    }
    return null;
  }

  public Iterator getPrefixes(String namespaceURI) {
    List prefixes = new ArrayList();
    for (String prefix : map.keySet()) {
      if (map.get(prefix).equals(namespaceURI)) {
        prefixes.add(prefix);
      }
    }
    return prefixes.iterator();
  }
}  