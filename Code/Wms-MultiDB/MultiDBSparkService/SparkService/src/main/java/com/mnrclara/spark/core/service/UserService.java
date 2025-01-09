//package com.mnrclara.spark.core.service;
//
//import java.util.List;
//import java.util.Optional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.mnrclara.spark.core.model.User;
//import com.mnrclara.spark.core.repository.UserRepository;
//import com.mnrclara.spark.core.util.Client;
//import com.mnrclara.spark.core.util.SparkTest;
//
//@Service
//public class UserService {
//
//    @Autowired
//    UserRepository userRepository;
//
//    public List<User> getUsers () {
//        return userRepository.findAll();
//    }
//
//    public User getUser (long id) {
//        return userRepository.findById(id).orElse(null);
//    }
//
//    public User createUser (User newUser) {
//        return userRepository.save(newUser);
//    }
//
//    public User patchUser (User modifiedUser) {
//        return userRepository.save(modifiedUser);
//    }
//
//    public void deleteUser (long userId) {
//        User user = getUser (userId);
//        userRepository.delete(user);
//    }
//
//    public List<Client> sparkProcess () throws Exception {
//        SparkTest test = new SparkTest();
//        return test.dbProcess();
//    }
//}