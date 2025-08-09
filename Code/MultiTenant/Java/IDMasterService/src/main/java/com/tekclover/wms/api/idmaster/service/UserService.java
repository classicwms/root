package com.tekclover.wms.api.idmaster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.user.User;
import com.tekclover.wms.api.idmaster.repository.UserRepository;
import com.tekclover.wms.api.idmaster.util.PasswordEncoder;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder = new PasswordEncoder();
	
	public User validateUser (String username, String loginPassword) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null) {
    		throw new BadRequestException("Invalid Username : " + username);
    	}
		
		boolean isSuccess = passwordEncoder.matches(loginPassword, user.getPassword());
		if (!isSuccess) {
			throw new BadRequestException("Password is wrong. Please enter correct password.");
		}
		
		return user;
	}
}
