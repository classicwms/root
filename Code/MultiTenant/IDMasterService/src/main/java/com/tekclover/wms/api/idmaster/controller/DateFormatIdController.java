package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.dateformatid.AddDateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.DateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.FindDateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.UpdateDateFormatId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.DateFormatIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
@Api(tags = {"DateFormatId"}, value = "DateFormatId  Operations related to DateFormatIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DateFormatId ",description = "Operations related to DateFormatId ")})
@RequestMapping("/dateformatid")
@RestController
public class DateFormatIdController {
	
	@Autowired
	DateFormatIdService dateformatidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
    @ApiOperation(response = DateFormatId.class, value = "Get all DateFormatId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<DateFormatId> dateformatidList = dateformatidService.getDateFormatIds();
		return new ResponseEntity<>(dateformatidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = DateFormatId.class, value = "Get a DateFormatId") // label for swagger 
	@GetMapping("/{dateFormatId}")
	public ResponseEntity<?> getDateFormatId(@PathVariable String dateFormatId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		DateFormatId dateformatid =
    			dateformatidService.getDateFormatId(warehouseId,dateFormatId,companyCodeId,languageId,plantId);
    	log.info("DateFormatId : " + dateformatid);
		return new ResponseEntity<>(dateformatid, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = DateFormatId.class, value = "Create DateFormatId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDateFormatId(@Valid @RequestBody AddDateFormatId newDateFormatId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newDateFormatId.getCompanyCodeId(), newDateFormatId.getPlantId(), newDateFormatId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		DateFormatId createdDateFormatId = dateformatidService.createDateFormatId(newDateFormatId, loginUserID);
		return new ResponseEntity<>(createdDateFormatId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = DateFormatId.class, value = "Update DateFormatId") // label for swagger
    @PatchMapping("/{dateFormatId}")
	public ResponseEntity<?> patchDateFormatId(@PathVariable String dateFormatId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateDateFormatId updateDateFormatId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		DateFormatId createdDateFormatId =
				dateformatidService.updateDateFormatId(warehouseId, dateFormatId,companyCodeId,languageId,plantId,loginUserID, updateDateFormatId);
		return new ResponseEntity<>(createdDateFormatId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = DateFormatId.class, value = "Delete DateFormatId") // label for swagger
	@DeleteMapping("/{dateFormatId}")
	public ResponseEntity<?> deleteDateFormatId(@PathVariable String dateFormatId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		dateformatidService.deleteDateFormatId(warehouseId, dateFormatId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = DateFormatId.class, value = "Find DateFormatId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findDateFormatId(@Valid @RequestBody FindDateFormatId findDateFormatId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findDateFormatId.getCompanyCodeId(), findDateFormatId.getPlantId(), findDateFormatId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<DateFormatId> createdDateFormatId = dateformatidService.findDateFormatId(findDateFormatId);
		return new ResponseEntity<>(createdDateFormatId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}