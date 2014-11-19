package com.biancama.security.prepost;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserRepository {

	User getUser(String username) throws UsernameNotFoundException;

}
