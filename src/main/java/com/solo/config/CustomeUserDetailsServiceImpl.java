package com.solo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.solo.entity.User;
import com.solo.repo.IUserRepo;

public class CustomeUserDetailsServiceImpl implements UserDetailsService {


	@Autowired
	private IUserRepo userRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		//fetching user form data
		
		User user = userRepository.findByEmail(email);
		
		if(user == null) {
			throw new UsernameNotFoundException("Could not found user  !!");
		}
		else {
			return new CustomeUserDetails(user);
		}
		
		
	}

}
