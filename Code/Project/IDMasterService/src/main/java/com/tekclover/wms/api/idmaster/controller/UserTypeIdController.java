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

import com.tekclover.wms.api.idmaster.model.usertypeid.AddUserTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UserTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UpdateUserTypeId;
import com.tekclover.wms.api.idmaster.service.UserTypeIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"UserTypeId"}, value = "UserTypeId  Operations related to UserTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "UserTypeId ",description = "Operations related to UserTypeId ")})
@RequestMapping("/usertypeid")
@RestController
public class UserTypeIdController {
	
	@Autowired
	UserTypeIdService usertypeidService;
	
    @ApiOperation(response = UserTypeId.class, value = "Get all UserTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<UserTypeId> usertypeidList = usertypeidService.getUserTypeIds();
		return new ResponseEntity<>(usertypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = UserTypeId.class, value = "Get a UserTypeId") // label for swagger 
	@GetMapping("/{userTypeId}")
	public ResponseEntity<?> getUserTypeId(@PathVariable Long userTypeId, 
			@RequestParam String warehouseId) {
    	UserTypeId usertypeid = 
    			usertypeidService.getUserTypeId(warehouseId, userTypeId);
    	log.info("UserTypeId : " + usertypeid);
		return new ResponseEntity<>(usertypeid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = UserTypeId.class, value = "Search UserTypeId") // label for swagger
//	@PostMapping("/findUserTypeId")
//	public List<UserTypeId> findUserTypeId(@RequestBody SearchUserTypeId searchUserTypeId)
//			throws Exception {
//		return usertypeidService.findUserTypeId(searchUserTypeId);
//	}
    
    @ApiOperation(response = UserTypeId.class, value = "Create UserTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postUserTypeId(@Valid @RequestBody AddUserTypeId newUserTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		UserTypeId createdUserTypeId = usertypeidService.createUserTypeId(newUserTypeId, loginUserID);
		return new ResponseEntity<>(createdUserTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = UserTypeId.class, value = "Update UserTypeId") // label for swagger
    @PatchMapping("/{userTypeId}")
	public ResponseEntity<?> patchUserTypeId(@PathVariable Long userTypeId, 
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateUserTypeId updateUserTypeId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		UserTypeId createdUserTypeId = 
				usertypeidService.updateUserTypeId(warehouseId, userTypeId, loginUserID, updateUserTypeId);
		return new ResponseEntity<>(createdUserTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = UserTypeId.class, value = "Delete UserTypeId") // label for swagger
	@DeleteMapping("/{userTypeId}")
	public ResponseEntity<?> deleteUserTypeId(@PathVariable Long userTypeId, 
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	usertypeidService.deleteUserTypeId(warehouseId, userTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}