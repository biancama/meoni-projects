package com.biancama.test.ejb.jsf2.web;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name = "accountBean")
@RequestScoped
//@SessionScoped
public class Account {
	private final static Logger logger = LoggerFactory.getLogger(Account.class);
	//private final static String ACTION = "pages/listener?facesredirect=true";
	private final static String ACTION = "pages/listenerView.xhtml";
	private String name;
	
	private HtmlInputText inputText;
	private HtmlOutputText outputText;
	
	public String actionSub(){
		logger.info("action done: " + name);
//		return ACTION;
		return null;
	}
	
	// Setter -------------------------------------------------------------
	public void setName(String name) {
		logger.info("Account SET " + name);
		this.name = name;
	}

	public String getName() {
		logger.info("Account GET " + name);
		return name;
	}

	public void setOutputText(HtmlOutputText outputText) {
		logger.info("OutputText SET " + outputText);
		this.outputText = outputText;
	}

	public HtmlOutputText getOutputText() {
		logger.info("OutputText GET " + outputText);
		return outputText;
	}

	public void setInputText(HtmlInputText inputText) {
		logger.info("InputText SET " + inputText);
		this.inputText = inputText;
	}

	public HtmlInputText getInputText() {
		logger.info("InputText GET " + inputText);
		return inputText;
	}
	
}

