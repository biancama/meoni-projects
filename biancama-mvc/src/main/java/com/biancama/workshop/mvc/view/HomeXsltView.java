package com.biancama.workshop.mvc.view;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.springframework.web.servlet.view.xslt.XsltView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.biancama.workshop.dom.Workshop;

public class HomeXsltView extends XsltView {
	@Override
	protected Source locateSource(Map<String, Object> model) throws Exception {
		Document doc =
				DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		// root element
		Element rootEl = doc.createElement("company");
		doc.appendChild(rootEl);
		@SuppressWarnings("unchecked")
		List<Workshop> workshops = (List<Workshop>) model.get("workshops");
		for (Workshop workshop : workshops) {
			appendWorkshop(doc, rootEl, workshop);
		}
		return new DOMSource(rootEl);
	}
	
	
	private void appendWorkshop(Document doc, Element rootEl, Workshop workshop) {
		Element workshopEl = doc.createElement("workshop");
		// title
		Element nameEl = doc.createElement("title");
		nameEl.appendChild(doc.createTextNode(workshop.getTitle()));
		workshopEl.appendChild(nameEl);
		// title
		Element teacherEl = doc.createElement("teacher");
		teacherEl.appendChild(doc.createTextNode(workshop.getTeacher().getUsername()));
		workshopEl.appendChild(teacherEl);

		rootEl.appendChild(workshopEl);
		
	}

}
