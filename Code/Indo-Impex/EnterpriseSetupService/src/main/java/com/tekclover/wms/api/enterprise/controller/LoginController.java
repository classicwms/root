package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Slf4j
@Validated
@Api(tags = {"Login"}, value = "Login Operations related to User Login") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Login", description = "Operations related to Login")})
@RequestMapping("/login")
//@RestController
public class LoginController {

    @Autowired
    UserService userService;

    @ApiOperation(response = Optional.class, value = "Validate Login User") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> validateUser(@RequestParam String name, @RequestParam String password) {
        log.info("Name: " + name + " - " + "Password: " + password);
        boolean isValidUser = userService.validateUser(name, password);
        log.info("Login : " + isValidUser);
        if (!isValidUser) {
            throw new BadRequestException("Password is wrong. Enter correct password.");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}