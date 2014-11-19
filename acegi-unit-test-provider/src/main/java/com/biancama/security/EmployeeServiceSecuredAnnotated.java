package com.biancama.security;

import org.springframework.security.access.annotation.Secured;

public interface EmployeeServiceSecuredAnnotated {
	@Secured({"ROLE_WORKER", "ROLE_MANAGER"})
	public void addNewEmployee();
	@Secured({"ROLE_ANONYMOUS"})
	public int getNumberOfEmployees();
	@Secured({"ROLE_MANAGER"})
	public void setNumberOfEmployees(int numberOfEmployees);
}
