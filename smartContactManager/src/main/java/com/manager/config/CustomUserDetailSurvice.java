package com.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.manager.dao.UserRepository;
import com.manager.entities.User;

public class CustomUserDetailSurvice implements UserDetailsService{
	@Autowired
	private UserRepository userrep;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//fetching data through database
		User user = userrep.getUserByUserName(username);
		if (user==null) {
			throw new UsernameNotFoundException("could not found user!!");
			
		}
		
		CustomUserDetail cDetail=new CustomUserDetail(user);
		return cDetail;
	}

}
