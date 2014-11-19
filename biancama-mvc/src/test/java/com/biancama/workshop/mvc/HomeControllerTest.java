package com.biancama.workshop.mvc;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.biancama.workshop.dom.User;
import com.biancama.workshop.dom.Workshop;
import com.biancama.workshop.service.WorkshopService;

public class HomeControllerTest {
	@Test
	public void shouldDisplayTodayWorkshops(){
		
		// Mockito mock
		List<Workshop> expectedWorkshops = asList(new Workshop("title 1"), new Workshop("title 2"), new Workshop("title 3"));
	
		WorkshopService workshopService = mock(WorkshopService.class);
		User user = new User();
		user.setUsername("biancama");
		when(workshopService.getWorkshopsOfToday(user)).thenReturn(expectedWorkshops);
		
		HomeController controller = new HomeController(workshopService);
		Model model = new ExtendedModelMap();
		
		String viewNameHome = controller.showHomePage(model);
		assertEquals("home", viewNameHome);

		String viewNameWorkshop = controller.getWorkshopsOfToday("biancama", model);
		assertEquals("workshop", viewNameWorkshop);
		
		
		assertSame(expectedWorkshops, model.asMap().get("workshops"));
		verify(workshopService).getWorkshopsOfToday(user);
	}

}
