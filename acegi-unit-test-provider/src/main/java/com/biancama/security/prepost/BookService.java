package com.biancama.security.prepost;

import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.access.prepost.PreAuthorize;

public interface BookService {
	void setBookRepository(BookRepository bookrepository);
	
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_MANAGER') and hasPermission(#book, 'isAuthor')")
	public void changeTitle(Book book, String newTitle);
	
	@PostAuthorize("returnObject.author.name == principal.username")
	public Book getBookById(long id);
	
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_MANAGER')")
	@PostFilter("filterObject.author.name == principal.username")
	public List<Book> gelAllBooksFromManagers();
	

	@PreAuthorize("isAuthenticated()")
	@PreFilter("filterObject.author.name == principal.username")
	public boolean areAllPriced(List<Book> books);
	
}
