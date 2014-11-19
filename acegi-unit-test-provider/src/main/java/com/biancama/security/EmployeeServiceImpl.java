package com.biancama.security;

public class EmployeeServiceImpl implements EmployeeService {

	private int numberOfEmployees = 0;
	@Override
	public void addNewEmployee() {
		this.numberOfEmployees++;

	}

	@Override
	public int getNumberOfEmployees() {
		return numberOfEmployees;
	}

	@Override
	public void setNumberOfEmployees(int numberOfEmployees) {
		this.numberOfEmployees = numberOfEmployees;

	}

}
