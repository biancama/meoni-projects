package com.biancama.test.ejb.jsf2.component;

import javax.faces.webapp.UIComponentELTag;

public class BiancamaEmailInputTag extends UIComponentELTag {

	private String value;

	@Override
	public String getComponentType() {
		return "BIANCAMA_EMAIL_INPUT";
	}

	@Override
	public String getRendererType() {
		return "BIANCAMA_EMAIL_RENDERER";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
