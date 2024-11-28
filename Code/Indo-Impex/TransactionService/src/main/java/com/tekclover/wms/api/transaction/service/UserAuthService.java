package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.auth.UserLogin;
import com.tekclover.wms.api.transaction.model.auth.UsersHelper;
import com.tekclover.wms.api.transaction.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


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