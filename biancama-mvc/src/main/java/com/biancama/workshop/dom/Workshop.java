package com.biancama.workshop.dom;

import java.io.Serializable;

public class Workshop implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7091861872468862424L;
	
	
	private String title;
	private User teacher;
	
	public Workshop(String title) {
		super();
		this.title = title;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public User getTeacher() {
		return teacher;
	}


	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}
}
