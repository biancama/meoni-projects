package com.biancama.test.ejb.jsf2.component;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

public class BiancamaHelloWorldComponentTag extends UIComponentELTag {
	
	private ValueExpression message = null;
	
	@Override
	public String getComponentType() {
		return "HELLO_WORLD";
	}

	@Override
	public String getRendererType() {
		  return null;
	}

	@Override
	protected void setProperties(UIComponent component) {
		super.setProperties(component);
		if (!(component instanceof BiancamaHelloWorldComponent)){
			throw new IllegalStateException("Component " + component.toString() +
                    " is of wrong type!!!");
		}
		BiancamaHelloWorldComponent biancamaHelloWorldComponent = (BiancamaHelloWorldComponent) component;
		
		if (message != null){
			biancamaHelloWorldComponent.setValueExpression("message", message);
		}
	}
	
	public ValueExpression getMessage() {
		return message;
	}

	public void setMessage(ValueExpression message) {
		this.message = message;
	}

}
