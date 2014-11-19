package com.biancama.workshop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.biancama.workshop.dom.User;
import com.biancama.workshop.dom.Workshop;
@Service("workshopService")
public class WorkshopServiceImpl implements WorkshopService {

	@Override
	public List<Workshop> getWorkshopsOfToday(User user) {
		List<Workshop> workshops = new ArrayList<Workshop>();
		Workshop workshop = new Workshop("workshop 1: " + user.getUsername());
		workshop.setTeacher(user);
		workshops.add(workshop);
		workshop = new Workshop("workshop 2: " + user.getUsername());
		workshop.setTeacher(user);
		workshops.add(workshop);
		workshop = new Workshop("workshop 3: " + user.getUsername());
		workshop.setTeacher(user);
		workshops.add(workshop);
		return workshops;
	}

}
