package com.biancama.workshop.mvc;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.biancama.workshop.dom.User;
import com.biancama.workshop.service.WorkshopService;

@Controller
public class HomeXlstControllerWithView {
	@Autowired
	private WorkshopService workshopService;
	
	public HomeXlstControllerWithView(){
		
	}
	public HomeXlstControllerWithView(WorkshopService workshopService) {
		super();
		this.workshopService = workshopService;
	}
	
	
	@RequestMapping({"/","/home"})
	public String showHomePage(Model model) throws ParserConfigurationException {
		User user = new User();
		user.setUsername("biancama");
		model.addAttribute("workshops", workshopService.getWorkshopsOfToday(user));
		return "home";
	}

	
	
	
}
