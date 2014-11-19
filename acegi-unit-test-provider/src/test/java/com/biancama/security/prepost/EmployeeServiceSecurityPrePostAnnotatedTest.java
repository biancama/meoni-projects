package com.biancama.security.prepost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-secured-prepost.xml", "classpath:/spring-secured-prepost-test.xml" })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class EmployeeServiceSecurityPrePostAnnotatedTest extends  
AbstractJUnit4SpringContextTests { 
	@Resource
	private BookService bookService;
	
	private BookRepository bookrepository;
	
	@Autowired
	private
	AuthenticationProvider provider;
	private List <Book> expectedBooksOfManager = new ArrayList<Book>();
	private List <Book> expectedBooksOfManager1 = new ArrayList<Book>();
			
	private List <Book> expectedBooks = new ArrayList<Book>();

	private void createSecureContext(final String username, final String password) {
	        Authentication auth = provider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	        SecurityContextHolder.getContext().setAuthentication(auth);
	}
	@Before
	public void createMockService(){
		expectedBooksOfManager.add(new Book(1, new Author("manager"), "Java"));
		expectedBooksOfManager.add(new Book(2, new Author("manager"), "Python"));
		expectedBooksOfManager1.add(new Book(3, new Author("manager"), "C++"));
		
		Book book = new Book(1, new Author("manager"), "Java");
		book.setPrice(new BigDecimal(12.99));
		expectedBooks.add(book);
		
		book = new Book(2, new Author("manager"), "Python");
		book.setPrice(new BigDecimal(19.99));
		expectedBooks.add(book);
		expectedBooks.add(new Book(3, new Author("manager1"), "C++"));
		
		bookrepository = mock(BookRepository.class);
		// getBookById
		when(bookrepository.getBookById(1)).thenReturn(new Book(1, new Author("manager"), "Java"));
		//getAllBooksWithRoleOfAuthor
		when(bookrepository.getAllBooksWithRoleOfAuthor("ROLE_MANAGER")).thenReturn(expectedBooks);
		bookService.setBookRepository(bookrepository);
	}
	
	
	@After 
	public void disposeSecurityHolder() {
		SecurityContextHolder.setContext(new SecurityContextImpl());
	} 
	
	// PreAuthorize
	@Test
	public void testChangeBookTitleFromAuthor() {
		createSecureContext("manager", "manager");
		Author author = new Author("manager");
		Book book = new Book("Bible", author);
		bookService.changeTitle(book, "new Testament");
		assertEquals(book.getTitle(), "new Testament");
	}
	@Test(expected = AccessDeniedException.class)
	public void testChangeBookTitleFromAnotherAuthor() {
		createSecureContext("manager1", "manager1");
		Author author = new Author("manager");
		Book book = new Book("Bible", author);
		bookService.changeTitle(book, "new Testament");
	}
	// PostAuthorize
	@Test
	public void testBookWithCorrectAuthor() {
		createSecureContext("manager", "manager");
		Book book = bookService.getBookById(1);
		assertNotNull(book);
	}	
	
	@Test(expected = AccessDeniedException.class)
	public void testBookWithNonCorrectAuthor() {
		createSecureContext("manager1", "manager1");
		bookService.getBookById(1); // this method is called 
	}	
	// PostFilter
	@Test
	public void testBookFromManager() {
		createSecureContext("manager", "manager");
		List<Book> books = bookService.gelAllBooksFromManagers();
		assertEquals(expectedBooksOfManager, books);
	}	

	@Test
	public void testBookFromManager1() {
		createSecureContext("manager1", "manager1");
		List<Book> books = bookService.gelAllBooksFromManagers();
		assertEquals(expectedBooksOfManager1, books);
	}	
	//PreFilter
	@Test
	public void testBookPricedFromManager() {
		createSecureContext("manager", "manager");
		assertTrue(bookService.areAllPriced(expectedBooks));
	}	
	//PreFilter
	@Test
	public void testBookPricedFromManager1() {
		createSecureContext("manager1", "manager1");
		assertFalse(bookService.areAllPriced(expectedBooks));
	}	
}
