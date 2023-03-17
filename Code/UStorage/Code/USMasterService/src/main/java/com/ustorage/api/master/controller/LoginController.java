package com.ustorage.api.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ustorage.api.master.model.user.*;
import com.ustorage.api.master.service.UserService;
import com.ustorage.api.master.service.CommonService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@Api(tags = {"Login"}, value = "Login Operations related to User Login") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Login",description = "Operations related to Login")})
@RequestMapping("/login")
@RestController
public class LoginController {
	
	@Autowired
	UserService userService;

	@Autowired
	CommonService commonService;

	@ApiOperation(response = Optional.class, value = "Get all Users") // label for swagger
	@GetMapping("/users")
	public ResponseEntity<?> getAll() {
		List<User> userList = userService.getUsers();
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}

	@ApiOperation(response = Optional.class, value = "Get a User") // label for swagger
	@GetMapping("/user/{id}")
	public ResponseEntity<?> getUser(@PathVariable Long id) {
		User user = userService.getUser(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@ApiOperation(response = Optional.class, value = "Create User") // label for swagger
	@PostMapping("/user")
	public ResponseEntity<?> postUser(@Valid @RequestBody AddUser newUser,@RequestParam String loginUserID) {
		User createdUser = userService.createUser(newUser,loginUserID);
		return new ResponseEntity<>(createdUser , HttpStatus.OK);
	}

	@ApiOperation(response = Optional.class, value = "Patch User") // label for swagger
	@PatchMapping("/user/{id}")
	public ResponseEntity<?> patchUser(@PathVariable Long id, @Valid @RequestBody User modifiedUser,@RequestParam String loginUserID) {
		User updatedUser = userService.patchUser(id, modifiedUser,loginUserID);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@ApiOperation(response = Optional.class, value = "Delete User") // label for swagger
	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestParam String loginUserID) {
		userService.deleteUser(id, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(response = Optional.class, value = "Validate Login User") // label for swagger
	@GetMapping("/validate")
	public ResponseEntity<?> validateUserID(@RequestParam String userID, @RequestParam String password) {
		log.info("UserID:" + userID + " - " + "Password: " + password);
		User validatedUser = userService.validateUser(userID, password);

		log.info("Login : " + validatedUser);
		return new ResponseEntity<>(validatedUser, HttpStatus.OK);
	}
}