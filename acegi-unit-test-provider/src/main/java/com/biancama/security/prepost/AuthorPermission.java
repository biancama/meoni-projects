package com.biancama.security.prepost;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

public class AuthorPermission implements BiancamaPermission {

	@Override
	public boolean isAllowed(Authentication authentication,
			Object targetDomainObject) {
		if (! (targetDomainObject instanceof Book && authentication.getPrincipal() instanceof User)){
			return false;
		}
		Book book = (Book) targetDomainObject;
		User user = (User) authentication.getPrincipal();
		if (book.getAuthor().getName().equals(user.getUsername())){
			return true;
		} else {
			return false;
		}
	}

}
