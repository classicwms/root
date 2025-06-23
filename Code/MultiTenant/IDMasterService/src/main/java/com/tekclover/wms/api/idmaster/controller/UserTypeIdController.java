package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.usertypeid.FindUserTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
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

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = UserTypeId.class, value = "Get all UserTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<UserTypeId> usertypeidList = usertypeidService.getUserTypeIds();
		return new ResponseEntity<>(usertypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = UserTypeId.class, value = "Get a UserTypeId") // label for swagger 
	@GetMapping("/{userTypeId}")
	public ResponseEntity<?> getUserTypeId(@RequestParam String warehouseId,@PathVariable Long userTypeId,@RequestParam String companyCodeId,
										   @RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
    	UserTypeId usertypeid =
    			usertypeidService.getUserTypeId(warehouseId,userTypeId,companyCodeId,languageId,plantId);
    	log.info("UserTypeId : " + usertypeid);
		return new ResponseEntity<>(usertypeid, HttpStatus.OK);
	}
    finally {

			DataBaseContextHolder.clear();
		}
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
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newUserTypeId.getCompanyCodeId(), newUserTypeId.getPlantId(), newUserTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		UserTypeId createdUserTypeId = usertypeidService.createUserTypeId(newUserTypeId, loginUserID);
		return new ResponseEntity<>(createdUserTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = UserTypeId.class, value = "Update UserTypeId") // label for swagger
    @PatchMapping("/{userTypeId}")
	public ResponseEntity<?> patchUserTypeId(@RequestParam String warehouseId, @PathVariable Long userTypeId,
											 @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateUserTypeId updateUserTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		UserTypeId createdUserTypeId =
				usertypeidService.updateUserTypeId(warehouseId, userTypeId,companyCodeId,languageId,plantId,loginUserID, updateUserTypeId);
		return new ResponseEntity<>(createdUserTypeId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}

    @ApiOperation(response = UserTypeId.class, value = "Delete UserTypeId") // label for swagger
	@DeleteMapping("/{userTypeId}")
	public ResponseEntity<?> deleteUserTypeId(@RequestParam String warehouseId,@PathVariable Long userTypeId, @RequestParam String companyCodeId,
											  @RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		usertypeidService.deleteUserTypeId(warehouseId, userTypeId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = UserTypeId.class, value = "Find UserTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findUserTypeId(@Valid @RequestBody FindUserTypeId findUserTypeId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findUserTypeId.getCompanyCodeId(), findUserTypeId.getPlantId(), findUserTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<UserTypeId> createdUserTypeId = usertypeidService.findUserTypeId(findUserTypeId);
		return new ResponseEntity<>(createdUserTypeId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}