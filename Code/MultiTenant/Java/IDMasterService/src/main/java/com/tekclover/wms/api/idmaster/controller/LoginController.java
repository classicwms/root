package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.user.Login;
import com.tekclover.wms.api.idmaster.model.user.UserLoginInput;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import com.tekclover.wms.api.idmaster.service.UserManagementService;
import com.tekclover.wms.api.idmaster.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;

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

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = UserManagement.class, value = "Validate Login User") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> validateUserID(@RequestParam String userID, @RequestParam String password, @RequestParam String version) {
    	log.info("UserID:" + userID + " - " + "Password: " + password + " - " + "Version: " + version);
    	UserManagement validatedUser = userManagementService.validateUser(userID, password, version);
    	log.info("Login : " + validatedUser);
		return new ResponseEntity<>(validatedUser, HttpStatus.OK);
	}

	@ApiOperation(response = Login.class, value = "Validate Login User and return along with UserRole and ModuleId") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> validateUserId(@RequestBody UserLoginInput userLoginInput) throws ParseException {
		Login validatedUser = userManagementService.validateUserV8(userLoginInput);
		log.info("Login : " + validatedUser.getUsers());
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