package com.biancama.test.ejb.jsf2.component;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


public class BiancamaHelloWorldComponent extends UIComponentBase {

	@Override
	public String getFamily() {
		return "BIANCAMA_FAMILY";
	}
	

	@Override
	public void encodeBegin(FacesContext ctx) throws IOException {
		super.encodeBegin(ctx);
		ResponseWriter responseWriter = ctx.getResponseWriter();
		String message = (String) getAttributes().get("message");
		// encode the request to the component
		responseWriter.startElement("b", this);
		responseWriter.writeText("Hello World: " + message, "message");
		responseWriter.endElement("b");
		
	}

}
