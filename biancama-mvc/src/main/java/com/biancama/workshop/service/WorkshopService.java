package com.biancama.workshop.service;

import java.util.List;

import com.biancama.workshop.dom.User;
import com.biancama.workshop.dom.Workshop;

public interface WorkshopService {
	List<Workshop> getWorkshopsOfToday(User user);
}
