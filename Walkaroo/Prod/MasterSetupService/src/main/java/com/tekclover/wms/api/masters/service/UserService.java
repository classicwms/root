package com.tekclover.wms.api.masters.service;

import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.user.AddUser;
import com.tekclover.wms.api.masters.model.user.ModifyUser;
import com.tekclover.wms.api.masters.model.user.User;
import com.tekclover.wms.api.masters.repository.UserRepository;
import com.tekclover.wms.api.masters.util.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder = new PasswordEncoder();
	
	/**
	 * 
	 * @return
	 */
	public List<User> getUsers () {
		return userRepository.findAll();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public User getUser (long id) {
		return userRepository.findById(id).orElse(null);
	}
	
	/**
	 * 
	 * @param username
	 * @param loginPassword
	 * @return
	 */
	public User validateUser (String username, String loginPassword) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null) {
    		throw new BadRequestException("Invalid Username : " + username);
    	}
		
		boolean isValidUser = passwordEncoder.matches(loginPassword, user.getPassword());
		if (isValidUser) {
			return user;
		} 
		throw new BadRequestException("Password wrong");
	}
	
	/**
	 * 
	 * @param newUser
	 * @return
	 */
	public User createUser (AddUser newUser) {
		User dbUser = new User();
		dbUser.setUsername(newUser.getUsername());
		dbUser.setRole(User.Role.USER);
		dbUser.setEmail(newUser.getEmail());
		dbUser.setCity(newUser.getCity());
		dbUser.setState(newUser.getState());
		dbUser.setCountry(newUser.getCountry());
		
		dbUser.setPassword(PasswordEncoder.encodePassword(newUser.getPassword()));
		return userRepository.save(dbUser);
	}
	
	/**
	 * 
	 * @param id
	 * @param modifiedUser
	 * @return
	 */
	public User patchUser (Long id, ModifyUser modifiedUser) {
		User dbUser = getUser(id);
		dbUser.setEmail(modifiedUser.getEmail());
		dbUser.setCity(modifiedUser.getCity());
		dbUser.setState(modifiedUser.getState());
		dbUser.setCountry(modifiedUser.getCountry());
		return userRepository.save(dbUser);
	}
	
	/**
	 * 
	 * @param userId
	 */
	public void deleteUser (long userId) {
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
		} else {
			throw new EntityNotFoundException(String.valueOf(userId));
		}
	}
	
	@SuppressWarnings("static-access")
	public void changePassword(String email, String oldPassword, String newPassword)
	throws ConstraintViolationException{
		User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("email"));
	    if (oldPassword == null || oldPassword.isEmpty() 
	    		  || passwordEncoder.getEncoder().matches(oldPassword, user.getPassword())) {
	    	user.setPassword(passwordEncoder.encodePassword(newPassword));
	        userRepository.save(user);
	    } else {
	        throw new ConstraintViolationException("old password doesn't match", new HashSet<>());
	    }
	}
}
