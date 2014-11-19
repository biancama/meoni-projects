package com.biancama.test.ejb.jsf2.component;

import org.primefaces.component.inputtext.InputText;

import com.biancama.test.ejb.jsf2.validator.BiancamaEmailValidator;

public class BiancamaEmailInput extends InputText{
	//private static final String DEFAULT_RENDERER = "com.biancama.test.ejb.jsf2.component.BiancamaEmailInputRenderer";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.inputtext.InputTextRenderer";
	
	public BiancamaEmailInput(){
		super();
		setRendererType(DEFAULT_RENDERER);
		BiancamaEmailValidator emailValidator = new BiancamaEmailValidator();
		addValidator(emailValidator);
	}
	
	 @Override
	  public String getFamily() {
	    return "BIANCAMA_EMAIL_FAMILY";
	  }


}
