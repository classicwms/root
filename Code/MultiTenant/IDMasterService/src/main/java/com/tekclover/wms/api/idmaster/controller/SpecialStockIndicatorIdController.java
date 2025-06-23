package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.AddSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.FindSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.SpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.UpdateSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.SpecialStockIndicatorIdService;
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
@Api(tags = {"SpecialStockIndicatorId"}, value = "SpecialStockIndicatorId  Operations related to SpecialStockIndicatorIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SpecialStockIndicatorId ",description = "Operations related to SpecialStockIndicatorId ")})
@RequestMapping("/specialstockindicatorid")
@RestController
public class SpecialStockIndicatorIdController {
	
	@Autowired
	SpecialStockIndicatorIdService specialstockindicatoridService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = SpecialStockIndicatorId.class, value = "Get all SpecialStockIndicatorId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<SpecialStockIndicatorId> specialstockindicatoridList = specialstockindicatoridService.getSpecialStockIndicatorIds();
		return new ResponseEntity<>(specialstockindicatoridList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = SpecialStockIndicatorId.class, value = "Get a SpecialStockIndicatorId") // label for swagger 
	@GetMapping("/{specialStockIndicatorId}")
	public ResponseEntity<?> getSpecialStockIndicatorId(@PathVariable String specialStockIndicatorId, @RequestParam String stockTypeId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		SpecialStockIndicatorId specialstockindicatorid =
    			specialstockindicatoridService.getSpecialStockIndicatorId(warehouseId,stockTypeId, specialStockIndicatorId,companyCodeId,languageId,plantId);
    	log.info("SpecialStockIndicatorId : " + specialstockindicatorid);
		return new ResponseEntity<>(specialstockindicatorid, HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}

    @ApiOperation(response = SpecialStockIndicatorId.class, value = "Create SpecialStockIndicatorId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postSpecialStockIndicatorId(@Valid @RequestBody AddSpecialStockIndicatorId newSpecialStockIndicatorId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newSpecialStockIndicatorId.getCompanyCodeId(), newSpecialStockIndicatorId.getPlantId(), newSpecialStockIndicatorId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		SpecialStockIndicatorId createdSpecialStockIndicatorId = specialstockindicatoridService.createSpecialStockIndicatorId(newSpecialStockIndicatorId, loginUserID);
		return new ResponseEntity<>(createdSpecialStockIndicatorId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = SpecialStockIndicatorId.class, value = "Update SpecialStockIndicatorId") // label for swagger
    @PatchMapping("/{specialStockIndicatorId}")
	public ResponseEntity<?> patchSpecialStockIndicatorId(@PathVariable String specialStockIndicatorId,@RequestParam String stockTypeId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID,
			@Valid @RequestBody UpdateSpecialStockIndicatorId updateSpecialStockIndicatorId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		SpecialStockIndicatorId createdSpecialStockIndicatorId =
				specialstockindicatoridService.updateSpecialStockIndicatorId(warehouseId,stockTypeId,specialStockIndicatorId,companyCodeId,languageId,plantId,loginUserID,updateSpecialStockIndicatorId);
		return new ResponseEntity<>(createdSpecialStockIndicatorId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = SpecialStockIndicatorId.class, value = "Delete SpecialStockIndicatorId") // label for swagger
	@DeleteMapping("/{specialStockIndicatorId}")
	public ResponseEntity<?> deleteSpecialStockIndicatorId(@PathVariable String specialStockIndicatorId,@RequestParam String stockTypeId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		specialstockindicatoridService.deleteSpecialStockIndicatorId(warehouseId,stockTypeId, specialStockIndicatorId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = SpecialStockIndicatorId.class, value = "Find SpecialStockIndicatorId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findSpecialStockIndicatorId(@Valid @RequestBody FindSpecialStockIndicatorId findSpecialStockIndicatorId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findSpecialStockIndicatorId.getCompanyCodeId(), findSpecialStockIndicatorId.getPlantId(), findSpecialStockIndicatorId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<SpecialStockIndicatorId> createdSpecialStockIndicatorId = specialstockindicatoridService.findSpecialStockIndicatorId(findSpecialStockIndicatorId);
		return new ResponseEntity<>(createdSpecialStockIndicatorId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}