package com.biancama.workshop.mvc;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.biancama.workshop.dom.User;
import com.biancama.workshop.dom.Workshop;
import com.biancama.workshop.service.WorkshopService;

//@Controller
public class HomeXlstController {
	@Autowired
	private WorkshopService workshopService;
	
	public HomeXlstController(){
		
	}
	public HomeXlstController(WorkshopService workshopService) {
		super();
		this.workshopService = workshopService;
	}
	
	
	@RequestMapping({"/","/home"})
	public String showHomePage(Model model) throws ParserConfigurationException {
		User user = new User();
		user.setUsername("biancama");
		Document doc =
				DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		// root element
		Element rootEl = doc.createElement("company");
		doc.appendChild(rootEl);
		for (Workshop workshop : workshopService.getWorkshopsOfToday(user)) {
			appendWorkshop(doc, rootEl, workshop);
		}

		model.addAttribute("workshops", new DOMSource(doc));

		return "home";
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
	@RequestMapping(value={"/workshop"}, method = RequestMethod.POST)
	public String getWorkshopsOfToday(@RequestParam(value="username", required=true) String username,   Model model ) {
		User user = new User();
		user.setUsername(username);
		
		model.addAttribute("workshops", workshopService.getWorkshopsOfToday(user));
		return "workshop";
	}
	
	
	
}
