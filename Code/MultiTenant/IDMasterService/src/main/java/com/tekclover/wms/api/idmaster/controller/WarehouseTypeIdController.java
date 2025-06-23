package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.FindWarehouseTypeId;
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

import com.tekclover.wms.api.idmaster.model.warehousetypeid.AddWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.UpdateWarehouseTypeId;
import com.tekclover.wms.api.idmaster.service.WarehouseTypeIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"WarehouseTypeId"}, value = "WarehouseTypeId  Operations related to WarehouseTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "WarehouseTypeId ",description = "Operations related to WarehouseTypeId ")})
@RequestMapping("/warehousetypeid")
@RestController
public class WarehouseTypeIdController {
	
	@Autowired
	WarehouseTypeIdService warehousetypeidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = WarehouseTypeId.class, value = "Get all WarehouseTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<WarehouseTypeId> warehousetypeidList = warehousetypeidService.getWarehouseTypeIds();
		return new ResponseEntity<>(warehousetypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = WarehouseTypeId.class, value = "Get a WarehouseTypeId") // label for swagger 
	@GetMapping("/{warehouseTypeId}")
	public ResponseEntity<?> getWarehouseTypeId(@RequestParam String warehouseId,@PathVariable Long warehouseTypeId,
												@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		WarehouseTypeId warehousetypeid =
    			warehousetypeidService.getWarehouseTypeId(warehouseId,warehouseTypeId,companyCodeId,languageId,plantId);
    	log.info("WarehouseTypeId : " + warehousetypeid);
		return new ResponseEntity<>(warehousetypeid, HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}

//	@ApiOperation(response = WarehouseTypeId.class, value = "Search WarehouseTypeId") // label for swagger
//	@PostMapping("/findWarehouseTypeId")
//	public List<WarehouseTypeId> findWarehouseTypeId(@RequestBody SearchWarehouseTypeId searchWarehouseTypeId)
//			throws Exception {
//		return warehousetypeidService.findWarehouseTypeId(searchWarehouseTypeId);
//	}
    
    @ApiOperation(response = WarehouseTypeId.class, value = "Create WarehouseTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWarehouseTypeId(@Valid @RequestBody AddWarehouseTypeId newWarehouseTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newWarehouseTypeId.getCompanyCodeId(), newWarehouseTypeId.getPlantId(), newWarehouseTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		WarehouseTypeId createdWarehouseTypeId = warehousetypeidService.createWarehouseTypeId(newWarehouseTypeId, loginUserID);
		return new ResponseEntity<>(createdWarehouseTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = WarehouseTypeId.class, value = "Update WarehouseTypeId") // label for swagger
    @PatchMapping("/{warehouseTypeId}")
	public ResponseEntity<?> patchWarehouseTypeId(@RequestParam String warehouseId,@PathVariable Long warehouseTypeId,
												  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateWarehouseTypeId updateWarehouseTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		WarehouseTypeId createdWarehouseTypeId =
				warehousetypeidService.updateWarehouseTypeId(warehouseId, warehouseTypeId,companyCodeId,languageId,plantId,loginUserID, updateWarehouseTypeId);
		return new ResponseEntity<>(createdWarehouseTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = WarehouseTypeId.class, value = "Delete WarehouseTypeId") // label for swagger
	@DeleteMapping("/{warehouseTypeId}")
	public ResponseEntity<?> deleteWarehouseTypeId(@RequestParam String warehouseId,@PathVariable Long warehouseTypeId,
												   @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			 @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
    	warehousetypeidService.deleteWarehouseTypeId(warehouseId,warehouseTypeId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = WarehouseTypeId.class, value = "Find WarehouseTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findWarehouseTypeId(@Valid @RequestBody FindWarehouseTypeId findWarehouseTypeId) throws Exception {

		List<WarehouseTypeId> createdWarehouseTypeId = warehousetypeidService.findWarehouseTypeId(findWarehouseTypeId);
		return new ResponseEntity<>(createdWarehouseTypeId, HttpStatus.OK);
	}
}