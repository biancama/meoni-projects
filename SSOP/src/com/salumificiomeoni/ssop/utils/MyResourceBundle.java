package com.salumificiomeoni.ssop.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MyResourceBundle {
	private MyResourceBundle(){};
	
	public static boolean isHoliday(String day, int year){
		ResourceBundle rs = ResourceBundle.getBundle(String.valueOf(year));
		boolean isHoliday = true;
		
		try{
			rs.getString(day);
		}catch(MissingResourceException ex){
			isHoliday = false;
		}
		
		return isHoliday;
	}
}
