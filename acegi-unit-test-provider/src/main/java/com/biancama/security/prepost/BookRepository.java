package com.biancama.security.prepost;

import java.util.List;

public interface BookRepository {

	public Book getBookById(long id);

	public List<Book> getAllBooksWithRoleOfAuthor(String string);

}
