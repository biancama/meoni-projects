package com.biancama.utils;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.biancama.log.BiancaLogger;

public class ParserUtils {
    private ParserUtils() {
    }

    public static Document parseXmlString(String xmlString, boolean validating) {
        if (xmlString == null) { return null; }
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);

            InputSource inSource = new InputSource(new StringReader(xmlString));

            // Create the builder and parse the file
            Document doc = factory.newDocumentBuilder().parse(inSource);

            return doc;
        } catch (Exception e) {
            BiancaLogger.getLogger().severe(xmlString);
            BiancaLogger.exception(e);
        }
        return null;
    }

}
