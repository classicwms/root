package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.idmaster.model.hhtuser.AddHhtUser;
import com.tekclover.wms.api.idmaster.model.hhtuser.HhtUser;
import com.tekclover.wms.api.idmaster.model.hhtuser.UpdateHhtUser;
import com.tekclover.wms.api.idmaster.service.HhtUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"HhtUser"}, value = "HhtUser  Operations related to HhtUserController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HhtUser ",description = "Operations related to HhtUser ")})
@RequestMapping("/hhtuser")
@RestController
public class HhtUserController {
	
	@Autowired
	HhtUserService hhtuserService;
	
    @ApiOperation(response = HhtUser.class, value = "Get all HhtUser details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HhtUser> hhtuserList = hhtuserService.getHhtUsers();
		return new ResponseEntity<>(hhtuserList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HhtUser.class, value = "Get a HhtUser") // label for swagger 
	@GetMapping("/{userId}")
	public ResponseEntity<?> getHhtUser(@PathVariable String userId, @RequestParam String warehouseId) {
    	HhtUser hhtuser = hhtuserService.getHhtUser(userId, warehouseId);
    	log.info("HhtUser : " + hhtuser);
		return new ResponseEntity<>(hhtuser, HttpStatus.OK);
	}
    
    @ApiOperation(response = HhtUser.class, value = "Get HhtUsers") // label for swagger 
   	@GetMapping("/{warehouseId}/hhtUser")
   	public ResponseEntity<?> getHhtUser(@PathVariable String warehouseId) {
       	List<HhtUser> hhtuser = hhtuserService.getHhtUser(warehouseId);
       	log.info("HhtUser : " + hhtuser);
   		return new ResponseEntity<>(hhtuser, HttpStatus.OK);
   	}
    
    @ApiOperation(response = HhtUser.class, value = "Create HhtUser") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHhtUser(@Valid @RequestBody AddHhtUser newHhtUser, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HhtUser createdHhtUser = hhtuserService.createHhtUser(newHhtUser, loginUserID);
		return new ResponseEntity<>(createdHhtUser , HttpStatus.OK);
	}
    
    @ApiOperation(response = HhtUser.class, value = "Update HhtUser") // label for swagger
    @PatchMapping("/{userId}")
	public ResponseEntity<?> patchHhtUser(@PathVariable String userId, @RequestParam String warehouseId, 
			@Valid @RequestBody UpdateHhtUser updateHhtUser, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HhtUser createdHhtUser = 
				hhtuserService.updateHhtUser(warehouseId, userId, updateHhtUser, loginUserID);
		return new ResponseEntity<>(createdHhtUser , HttpStatus.OK);
	}
    
    @ApiOperation(response = HhtUser.class, value = "Delete HhtUser") // label for swagger
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteHhtUser(@PathVariable String userId, @RequestParam String warehouseId, 
			@RequestParam String loginUserID) {
    	hhtuserService.deleteHhtUser(warehouseId, userId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}