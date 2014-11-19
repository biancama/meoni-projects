package com.biancama.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.domimpl.HTMLDocumentImpl;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLCollection;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class HTMLUtils {
    private HTMLUtils() {}

    public static Map<String, String> extractLinks(String uri) throws IOException, SAXException {
      final Map<String, String> map = new HashMap<String, String>();
      UserAgentContext uacontext = new SimpleUserAgentContext();
      DocumentBuilderImpl builder = new DocumentBuilderImpl(uacontext);
      URL url = new URL(uri);
      InputStream in = url.openConnection().getInputStream();
      try {
          Reader reader = new InputStreamReader(in, "ISO-8859-1");
          InputSourceImpl inputSource = new InputSourceImpl(reader, uri);
          Document d = builder.parse(inputSource);
          HTMLDocumentImpl document = (HTMLDocumentImpl) d;
        
          HTMLCollection anchors = document.getAnchors();
          int length = anchors.getLength();
          for(int i = 0; i < length; i++) {           
              map.put(anchors.item(i).getTextContent(), anchors.item(i).toString());
          }
      } finally {
          in.close();
      }
      return map;
    }
    public static HtmlTable getHtmlTable(String uri, String xPath) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage(uri);

        final HtmlTable table = (HtmlTable) page.getByXPath(xPath).get(0);
        webClient.closeAllWindows();
        return table;
    }
   
}
