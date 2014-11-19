package com.biancama.test.ejb.jsf2.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "biancmaEmailValidator")
public class BiancamaEmailValidator implements Validator {

	private static final String IP_REGEX = ".+@.+\\.[a-z]+";
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
        String emailAddress = (String) value;
        Pattern mask = null;

        mask = Pattern.compile(IP_REGEX);
        Matcher matcher = mask.matcher(emailAddress);

        if (!matcher.matches()) {

            FacesMessage message = new FacesMessage();
            message.setDetail("E-mail not valid");
            message.setSummary("E-mail not valid");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }		
	}

}
