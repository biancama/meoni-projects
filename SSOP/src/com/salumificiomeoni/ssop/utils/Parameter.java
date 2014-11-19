package com.salumificiomeoni.ssop.utils;

import java.io.File;
import java.io.IOException;

import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.ZippedDocumentTemplate;

public class Parameter {
	private String templateName;
	private int hour;
	private String templateFolder;
	private DocumentTemplate template;
	
	private void setTemplate(){
		try {
			this.template = new ZippedDocumentTemplate(new File(templateFolder + templateName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public Parameter(String templateFolder, String templateName, int hour) {
		super();
		this.templateFolder = templateFolder;
		this.templateName = templateName;
		this.hour = hour;
		setTemplate();
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}

	public DocumentTemplate getTemplate() {
		return template;
	}
	
}
