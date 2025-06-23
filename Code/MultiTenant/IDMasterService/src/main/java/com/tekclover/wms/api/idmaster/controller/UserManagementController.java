package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import org.checkerframework.checker.units.qual.A;
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

import com.tekclover.wms.api.idmaster.model.user.AddUserManagement;
import com.tekclover.wms.api.idmaster.model.user.FindUserManagement;
import com.tekclover.wms.api.idmaster.model.user.UpdateUserManagement;
import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import com.tekclover.wms.api.idmaster.service.UserManagementService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"UserManagement"}, value = "UserManagement  Operations related to UserManagementController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "UserManagement ",description = "Operations related to UserManagement ")})
@RequestMapping("/usermanagement")
@RestController
public class UserManagementController {
	
	@Autowired
	UserManagementService userManagementService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
	@ApiOperation(response = UserManagement.class, value = "Get all UserManagement details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<UserManagement> userManagementList = userManagementService.getUserManagements();
		return new ResponseEntity<>(userManagementList, HttpStatus.OK);
	}
	
	@ApiOperation(response = UserManagement.class, value = "Get a UserManagement") // label for swagger 
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserManagement(@PathVariable String userId, 
			@RequestParam String warehouseId, @RequestParam String languageId,
											   @RequestParam String companyCode,
											   @RequestParam String plantId,
											   @RequestParam Long userRoleId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			UserManagement userManagement = userManagementService.getUserManagement(languageId, companyCode, plantId, warehouseId, userId, userRoleId);
			log.info("UserManagement : " + userManagement);
			return new ResponseEntity<>(userManagement, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = UserManagement.class, value = "Create UserManagement") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postUserManagement(@Valid @RequestBody AddUserManagement newUserManagement, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			try {
				DataBaseContextHolder.setCurrentDb("IMF");
				String routingDb = dbConfigRepository.getDbName(newUserManagement.getCompanyCode(), newUserManagement.getPlantId(), newUserManagement.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
				UserManagement createdUserManagement = userManagementService.createUserManagement(newUserManagement, loginUserID);
				return new ResponseEntity<>(createdUserManagement, HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
			}

			}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = UserManagement.class, value = "Update UserManagement") // label for swagger
    @PatchMapping("/{userId}")
	public ResponseEntity<?> patchUserManagement(@PathVariable String userId, @RequestParam String warehouseId,@RequestParam String companyCode,@RequestParam String plantId,@RequestParam String languageId,@RequestParam Long userRoleId,
			@RequestBody UpdateUserManagement updateUserManagement, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			UserManagement updatedUserManagement =
					userManagementService.updateUserManagement(userId, warehouseId, companyCode, languageId, plantId, userRoleId, updateUserManagement, loginUserID);
			return new ResponseEntity<>(updatedUserManagement, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = UserManagement.class, value = "Delete UserManagement") // label for swagger
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUserManagement(@PathVariable String userId, @RequestParam String warehouseId,
												  @RequestParam String languageId,
												  @RequestParam String companyCode,
												  @RequestParam String plantId,
												  @RequestParam Long userRoleId,
												  @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			userManagementService.deleteUserManagement(userId, warehouseId, languageId, companyCode, plantId, userRoleId, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

	//Search
	@ApiOperation(response = UserManagement.class, value = "Find UserManagement") // label for swagger
	@PostMapping("/findUserManagement")
	public ResponseEntity<?> findUserManagement(@Valid @RequestBody FindUserManagement findUserManagement) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbNameList(findUserManagement.getCompanyCode(),findUserManagement.getPlantId(),findUserManagement.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<UserManagement> createdUserManagement = userManagementService.findUserManagement(findUserManagement);
			return new ResponseEntity<>(createdUserManagement, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

}