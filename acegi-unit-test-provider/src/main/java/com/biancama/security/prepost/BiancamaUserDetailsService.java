package com.biancama.security.prepost;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class BiancamaUserDetailsService implements UserDetailsService {
	
	private UserRepository userRepository;
	
	
	public BiancamaUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return userRepository.getUser(username);
	}

}
