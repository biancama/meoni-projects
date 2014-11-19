package com.biancama.test.ejb.jsf2.web;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "mailBean")
@RequestScoped
public class EmailBean {
	private String email = "massimo.biancalani@gmail.com";

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
