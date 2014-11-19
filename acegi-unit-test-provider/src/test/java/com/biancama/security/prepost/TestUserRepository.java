package com.biancama.security.prepost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TestUserRepository implements UserRepository {

	static private Map<String, User> userNames = new HashMap<String, User>();
	
	static{
		// Manager sha256 and salt{username}
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MANAGER");
		//manager{manager}
		User user = new User("manager", "597dd6df16d8e7444e6ffb8fde3ddd06aafb495c4bcf4b24812835c4cf51d4e0", new ArrayList<GrantedAuthority> (Arrays.asList(authority)));
		userNames.put("manager", user);
		
		authority = new SimpleGrantedAuthority("ROLE_MANAGER");
		user = new User("manager1", "25bcf1679b66ce2ff76154bb18a7a489b1799c466ddc19ffa96f734900deee7d", new ArrayList<GrantedAuthority> (Arrays.asList(authority)));
		userNames.put("manager1", user);
		
		authority = new SimpleGrantedAuthority("ROLE_WORKER");
		user = new User("worker", "94f2bd021547bdd88bc0c5b0e12e68563cd934a86f4a6e2f804b4bd95190d15c", new ArrayList<GrantedAuthority> (Arrays.asList(authority)));
		userNames.put("worker", user);
	}

	@Override
	public User getUser(String username) throws UsernameNotFoundException {
		if (!(userNames.containsKey(username))){
			throw new UsernameNotFoundException("User with login " + username + "  has not been found.");
		}
		return userNames.get(username);
	}

}
