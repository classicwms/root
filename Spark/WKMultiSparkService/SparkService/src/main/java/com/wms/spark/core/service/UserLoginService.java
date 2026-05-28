package com.wms.spark.core.service;

import com.wms.spark.core.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wms.spark.core.model.UserLogin;
import com.wms.spark.core.model.UsersHelper;

@Service
public class UserLoginService implements UserDetailsService {

    @Autowired
    UserLoginRepository userLoginRepository;

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