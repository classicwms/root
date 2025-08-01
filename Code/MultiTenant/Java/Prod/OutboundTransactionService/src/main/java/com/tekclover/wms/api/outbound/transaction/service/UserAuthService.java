package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.model.auth.UsersHelper;
import com.tekclover.wms.api.outbound.transaction.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.outbound.transaction.model.auth.UserLogin;


@Service
public class UserAuthService implements UserDetailsService {
	
	@Autowired
    UserAuthRepository userLoginRepository;

	@Override
	public UsersHelper loadUserByUsername(String username) throws UsernameNotFoundException {
		UserLogin userLogin = null;
		try {
			userLogin = userLoginRepository.getUserLoginDetails(username);
			UsersHelper usersHelper = new UsersHelper(userLogin);
			return usersHelper;
		} catch (Exception e) {
			e.printStackTrace();
			throw new UsernameNotFoundException(username + " not found..");
		}
	}
}
