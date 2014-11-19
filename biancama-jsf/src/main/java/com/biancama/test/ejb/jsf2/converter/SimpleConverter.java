package com.biancama.test.ejb.jsf2.converter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleConverter implements Converter {
	
	private final static Logger logger = LoggerFactory.getLogger(SimpleConverter.class);
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent component, String newValue) {
		logger.info("Component: " + component + " value: " +newValue);		
		return newValue;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		logger.info("Component: " + component + " value: " +value.toString() );
		return value.toString();
	}

}

