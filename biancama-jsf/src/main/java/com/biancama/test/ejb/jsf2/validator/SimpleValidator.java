package com.biancama.test.ejb.jsf2.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleValidator implements Validator {
	private final static Logger logger = LoggerFactory.getLogger(SimpleValidator.class);
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		logger.info("Component: " + component + " value: " +value );

	}

}

