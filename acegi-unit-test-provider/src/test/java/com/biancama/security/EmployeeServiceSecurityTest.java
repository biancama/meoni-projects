package com.biancama.security;



import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml"})
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class EmployeeServiceSecurityTest extends  
AbstractJUnit4SpringContextTests { 

	@Autowired
	private EmployeeService employeeService;
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
		createSecureContext("anonymous", "anonymous");
		employeeService.addNewEmployee();

	}
}
