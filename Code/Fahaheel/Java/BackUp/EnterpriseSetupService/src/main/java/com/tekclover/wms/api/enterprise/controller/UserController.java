package com.tekclover.wms.api.enterprise.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.enterprise.model.user.AddUser;
import com.tekclover.wms.api.enterprise.model.user.ModifyUser;
import com.tekclover.wms.api.enterprise.model.user.User;
import com.tekclover.wms.api.enterprise.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"User"}, value = "User Operations related to UserController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User",description = "Operations related to User")})
@RequestMapping("/user")
@RestController
public class UserController {
	
	@Autowired
	UserService userService;
	
    @ApiOperation(response = Optional.class, value = "Get all Users") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<User> userList = userService.getUsers();
		return new ResponseEntity<>(userList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = Optional.class, value = "Get a User") // label for swagger
	@GetMapping("/{id}")
	public ResponseEntity<?> getUser(@PathVariable Long id) {
    	User user = userService.getUser(id);
    	log.info("User : " + user);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create User") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addUser(@Valid @RequestBody AddUser newUser) {
		User createdUser = userService.createUser(newUser);
		return new ResponseEntity<>(createdUser , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Patch User") // label for swagger
	@PatchMapping("/{id}")
	public ResponseEntity<?> patchUser(@PathVariable Long id, @Valid @RequestBody ModifyUser modifiedUser) {
    	log.info("User name: " + modifiedUser.getEmail());
    	User updatedUser = userService.patchUser(id, modifiedUser);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Delete User") // label for swagger
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    	userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    @ApiOperation(response = Optional.class, value = "Change Password") // label for swagger
    @PutMapping("/{email}/changePassword")
    public ResponseEntity<?> changePassword(@PathVariable String email, 
    		@RequestParam(required = false) String oldPassword, 
    		@RequestParam @Size(min = 5) String newPassword ) {
		userService.changePassword(email, oldPassword, newPassword);
		return new ResponseEntity<>("Password updated successfully.", HttpStatus.OK);
    }
}