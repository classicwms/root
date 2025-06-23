package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.returntypeid.AddReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.FindReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.ReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.UpdateReturnTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.ReturnTypeIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ReturnTypeId"}, value = "ReturnTypeId  Operations related to ReturnTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ReturnTypeId ",description = "Operations related to ReturnTypeId ")})
@RequestMapping("/returntypeid")
@RestController
public class ReturnTypeIdController {
	
	@Autowired
	ReturnTypeIdService returntypeidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = ReturnTypeId.class, value = "Get all ReturnTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ReturnTypeId> returntypeidList = returntypeidService.getReturnTypeIds();
		return new ResponseEntity<>(returntypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ReturnTypeId.class, value = "Get a ReturnTypeId") // label for swagger 
	@GetMapping("/{returnTypeId}")
	public ResponseEntity<?> getReturnTypeId(@PathVariable String returnTypeId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
    	ReturnTypeId returntypeid =
    			returntypeidService.getReturnTypeId(warehouseId,returnTypeId,companyCodeId,languageId,plantId);
    	log.info("ReturnTypeId : " + returntypeid);
		return new ResponseEntity<>(returntypeid, HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ReturnTypeId.class, value = "Create ReturnTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postReturnTypeId(@Valid @RequestBody AddReturnTypeId newReturnTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newReturnTypeId.getCompanyCodeId(), newReturnTypeId.getPlantId(), newReturnTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ReturnTypeId createdReturnTypeId = returntypeidService.createReturnTypeId(newReturnTypeId, loginUserID);
		return new ResponseEntity<>(createdReturnTypeId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ReturnTypeId.class, value = "Update ReturnTypeId") // label for swagger
    @PatchMapping("/{returnTypeId}")
	public ResponseEntity<?> patchReturnTypeId(@PathVariable String returnTypeId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateReturnTypeId updateReturnTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ReturnTypeId createdReturnTypeId =
				returntypeidService.updateReturnTypeId(warehouseId,returnTypeId,companyCodeId,languageId,plantId,loginUserID,updateReturnTypeId);
		return new ResponseEntity<>(createdReturnTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ReturnTypeId.class, value = "Delete ReturnTypeId") // label for swagger
	@DeleteMapping("/{returnTypeId}")
	public ResponseEntity<?> deleteReturnTypeId(@PathVariable String returnTypeId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		returntypeidService.deleteReturnTypeId(warehouseId, returnTypeId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = ReturnTypeId.class, value = "Find ReturnTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findReturnTypeId(@Valid @RequestBody FindReturnTypeId findReturnTypeId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findReturnTypeId.getCompanyCodeId(), findReturnTypeId.getPlantId(), findReturnTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ReturnTypeId> createdReturnTypeId = returntypeidService.findReturnTypeId(findReturnTypeId);
		return new ResponseEntity<>(createdReturnTypeId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}