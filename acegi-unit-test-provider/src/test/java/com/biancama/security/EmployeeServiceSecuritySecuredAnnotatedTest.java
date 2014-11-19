package com.biancama.security;






import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.biancama.security.EmployeeServiceSecuredAnnotated;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-secured-annotated.xml"})
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class EmployeeServiceSecuritySecuredAnnotatedTest extends  
AbstractJUnit4SpringContextTests { 

	@Autowired
	private EmployeeServiceSecuredAnnotated employeeService;
	
	@Autowired
	private
	AuthenticationProvider provider;
	
	
	private void createSecureContext(final String username, final String password) {
	        Authentication auth = provider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	        SecurityContextHolder.getContext().setAuthentication(auth);
	}
	@After 
	public void disposeSecurityHolder() {
		SecurityContextHolder.setContext(new SecurityContextImpl());
	} 
	@Test
	public void testAddNewEmployeeForAManager() {
		createSecureContext("manager", "manager");
		employeeService.addNewEmployee();

	}
	@Test(expected = AccessDeniedException.class)
	public void testAddNewEmployeeForAnonymous() {
		createSecureContext("anonymous", "");
		employeeService.addNewEmployee();

	}
}
