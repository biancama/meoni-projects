package com.biancama.security.prepost;

import java.util.List;

import org.springframework.stereotype.Service;

@Service("bookService")
public class BookServiceImpl implements BookService{
	BookRepository bookrepository;
	
	public void changeTitle(Book book, String newTitle){
		book.setTitle(newTitle);
	}
	
	public Book getBookById(long id){
		System.out.println("getBookById: Method Called"); // This statement is reached in @PostAuthorize
		return bookrepository.getBookById(id);
	}

	@Override
	public void setBookRepository(BookRepository bookrepository) {
		this.bookrepository = bookrepository;
		
	}

	@Override
	public List<Book> gelAllBooksFromManagers() {
		return this.bookrepository.getAllBooksWithRoleOfAuthor("ROLE_MANAGER");
	}
	

	@Override
	public boolean areAllPriced(List<Book> books) {
		for (Book book : books) {
			if (book.getPrice() == null || book.getPrice().equals(0)){
				return false;
			}
		}
		return true;
	}
}
