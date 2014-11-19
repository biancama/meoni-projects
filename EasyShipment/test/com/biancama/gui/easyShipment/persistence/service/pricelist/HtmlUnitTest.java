package com.biancama.gui.easyShipment.persistence.service.pricelist;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class HtmlUnitTest {
    @Test
    public void homePage() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
        assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

        final String pageAsXml = page.asXml();
        assertTrue(pageAsXml.contains("<body class=\"composite\">"));

        final String pageAsText = page.asText();
        assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));

        webClient.closeAllWindows();
    }
    
    @Test
    public void homePage_Firefox() throws Exception {
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_2);
        final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
        assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

        webClient.closeAllWindows();
    }
    
    @Test
    public void getElements() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://www.borsamercimodena.it/Listino.asp?dat=17/01/2011&tip=1&nomegr=SALUMI+E+GRASSINE&idgr=28&anno=2011&set=2");
        final List<HtmlElement> table = page.getElementsByTagName("table");
        
        assertTrue(table.size() > 0);

        webClient.closeAllWindows();
    }
    
    @Test
    public void xpath() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://www.borsamercimodena.it/Listino.asp?dat=17/01/2011&tip=1&nomegr=SALUMI+E+GRASSINE&idgr=28&anno=2011&set=2");

        //get list of all divs
        final List<?> divs = page.getByXPath("//div");

        //get div which has a 'name' attribute of 'John'
//        final HtmlDivision div = (HtmlDivision) page.getByXPath("//div[@name='John']").get(0);
        final HtmlTable table = (HtmlTable) page.getByXPath("/html/body/table[3]/tbody/tr/td[2]/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/table").get(0);
        webClient.closeAllWindows();
    }
    
    @Test
    public void submittingForm() throws Exception {
        final WebClient webClient = new WebClient();

        // Get the first page
        final HtmlPage page1 = webClient.getPage("http://some_url");

        // Get the form that we are dealing with and within that form, 
        // find the submit button and the field that we want to change.
        final HtmlForm form = page1.getFormByName("myform");

        final HtmlSubmitInput button = form.getInputByName("submitbutton");
        final HtmlTextInput textField = form.getInputByName("userid");

        // Change the value of the text field
        textField.setValueAttribute("root");

        // Now submit the form by clicking the button and get back the second page.
        final HtmlPage page2 = button.click();

        webClient.closeAllWindows();
    }
}
