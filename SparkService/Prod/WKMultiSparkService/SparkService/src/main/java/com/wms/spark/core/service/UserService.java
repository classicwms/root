package com.wms.spark.core.service;

import com.wms.spark.core.model.User;
import com.wms.spark.core.repository.UserRepository;
import com.wms.spark.core.util.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> getUsers () {
        return userRepository.findAll();
    }

    public User getUser (long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser (User newUser) {
        return userRepository.save(newUser);
    }

    public User patchUser (User modifiedUser) {
        return userRepository.save(modifiedUser);
    }

    public void deleteUser (long userId) {
        User user = getUser (userId);
        userRepository.delete(user);
    }

}