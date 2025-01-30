package com.tekclover.wms.api.enterprise.transaction.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
	
	static BCryptPasswordEncoder passwordEncoder;
	
	public PasswordEncoder () {
		passwordEncoder = new BCryptPasswordEncoder();
	}
	
	public BCryptPasswordEncoder getEncoder () {
		return passwordEncoder;
	}
	
	public static String encodePassword (String password) {
		String hashedPassword = passwordEncoder.encode(password);
		return hashedPassword;
	}
	
	public boolean matches (String loginPassword, String dbPassword) {
		return passwordEncoder.matches(loginPassword, dbPassword);
	}
	
	public static void main(String[] args) {
		String password = "!wYemVeePee@123";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
		
		System.out.println(hashedPassword);
	}
}