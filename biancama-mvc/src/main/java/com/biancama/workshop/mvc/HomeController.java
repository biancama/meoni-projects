package com.biancama.workshop.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.biancama.workshop.dom.User;
import com.biancama.workshop.service.WorkshopService;

//@Controller
public class HomeController {
	@Autowired
	private WorkshopService workshopService;
	
	public HomeController(){
		
	}
	public HomeController(WorkshopService workshopService) {
		super();
		this.workshopService = workshopService;
	}
	
	
	@RequestMapping({"/","/home"})
	public String showHomePage(Model model) {
		return "home";
	}

	@RequestMapping(value={"/workshop"}, method = RequestMethod.POST)
	public String getWorkshopsOfToday(@RequestParam(value="username", required=true) String username,   Model model ) {
		User user = new User();
		user.setUsername(username);
		
		model.addAttribute("workshops", workshopService.getWorkshopsOfToday(user));
		return "workshop";
	}
	
	
	
}
