package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.user.AddUser;
import com.tekclover.wms.api.enterprise.model.user.ModifyUser;
import com.tekclover.wms.api.enterprise.model.user.User;
import com.tekclover.wms.api.enterprise.repository.UserRepository;
import com.tekclover.wms.api.enterprise.util.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new PasswordEncoder();

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean validateUser(String username, String loginPassword) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                throw new BadRequestException("Invalid Username : " + username);
            }

            return passwordEncoder.matches(loginPassword, user.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    public User createUser(AddUser newUser) {
        try {
            User dbUser = new User();
            dbUser.setUsername(newUser.getUsername());
            dbUser.setRole(User.Role.USER);
            dbUser.setEmail(newUser.getEmail());
            dbUser.setCity(newUser.getCity());
            dbUser.setState(newUser.getState());
            dbUser.setCountry(newUser.getCountry());

            dbUser.setPassword(PasswordEncoder.encodePassword(newUser.getPassword()));
            return userRepository.save(dbUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    public User patchUser(Long id, ModifyUser modifiedUser) {
        try {
            User dbUser = getUser(id);
            dbUser.setEmail(modifiedUser.getEmail());
            dbUser.setCity(modifiedUser.getCity());
            dbUser.setState(modifiedUser.getState());
            dbUser.setCountry(modifiedUser.getCountry());
            return userRepository.save(dbUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    public void deleteUser(long userId) {
        try {
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
            } else {
                throw new EntityNotFoundException(String.valueOf(userId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    @SuppressWarnings("static-access")
    public void changePassword(String email, String oldPassword, String newPassword)
            throws ConstraintViolationException {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("email"));
            if (oldPassword == null || oldPassword.isEmpty()
                    || passwordEncoder.getEncoder().matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encodePassword(newPassword));
                userRepository.save(user);
            } else {
                throw new ConstraintViolationException("old password doesn't match", new HashSet<>());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}