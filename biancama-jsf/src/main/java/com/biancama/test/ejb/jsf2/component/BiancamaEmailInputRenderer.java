package com.biancama.test.ejb.jsf2.component;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.primefaces.renderkit.CoreRenderer;

public class BiancamaEmailInputRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		decodeBehaviors(context, component);
		
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		super.encodeEnd(context, component);
	    if (context == null) {
	        throw new NullPointerException("NULL CONTEXT NOT ALLOWED!");
	      } else if (component == null) {
	          throw new NullPointerException("NULL COMPONENT NOT ALLOWED!");
	      }

	      ResponseWriter responseWriter = context.getResponseWriter();

	      responseWriter.startElement("input", component);
	      responseWriter.writeAttribute("type", "text", "text");
	      String id = (String)component.getClientId(context);
	      responseWriter.writeAttribute("id", id, "id");
	      responseWriter.writeAttribute("name", id, "id");

	      Object obj = getValue(component);
	      responseWriter.writeAttribute("value", formattingValue(obj), "value");
	      responseWriter.endElement("input");

	}

	private Object formattingValue(Object obj) {
		return obj.toString();
	}

	private Object getValue(UIComponent component) {
	    Object obj = null;
	    if (component instanceof UIInput) {
	      obj = ((UIInput) component).getSubmittedValue();
	    }

	    if ((null == obj) && (component instanceof ValueHolder)) {
	      obj = ((ValueHolder) component).getValue();
	    }

	    return obj;
	}
}
