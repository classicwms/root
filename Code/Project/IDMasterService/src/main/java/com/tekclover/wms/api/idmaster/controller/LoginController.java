package com.tekclover.wms.api.idmaster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import com.tekclover.wms.api.idmaster.service.UserManagementService;
import com.tekclover.wms.api.idmaster.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

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
	UserManagementService userManagementService;
	
    @ApiOperation(response = UserManagement.class, value = "Validate Login User") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> validateUserID(@RequestParam String userID, @RequestParam String password) {
    	log.info("UserID:" + userID + " - " + "Password: " + password);
    	UserManagement validatedUser = userManagementService.validateUser(userID, password);
    	log.info("Login : " + validatedUser);
		return new ResponseEntity<>(validatedUser, HttpStatus.OK);
	}
    
    //------------------Current-Used-Method-------------------------------------------------------------
//    @ApiOperation(response = Optional.class, value = "Validate Login User") // label for swagger
//	@GetMapping("")
//	public ResponseEntity<?> validateUser(@RequestParam String name, @RequestParam String password) {
//    	log.info("Name: " + name + " - " + "Password: " + password);
//    	User validatedUser = userService.validateUser(name, password);
//    	log.info("Login : " + validatedUser);
//    	
//		return new ResponseEntity<>(validatedUser, HttpStatus.OK);
//	}
}