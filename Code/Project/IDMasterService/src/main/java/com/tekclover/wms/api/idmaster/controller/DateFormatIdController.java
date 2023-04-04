package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.dateformatid.AddDateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.DateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.UpdateDateFormatId;
import com.tekclover.wms.api.idmaster.service.DateFormatIdService;
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
	
    @ApiOperation(response = DateFormatId.class, value = "Get all DateFormatId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<DateFormatId> dateformatidList = dateformatidService.getDateFormatIds();
		return new ResponseEntity<>(dateformatidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = DateFormatId.class, value = "Get a DateFormatId") // label for swagger 
	@GetMapping("/{dateFormatId}")
	public ResponseEntity<?> getDateFormatId(@PathVariable String dateFormatId,
			@RequestParam String warehouseId) {
    	DateFormatId dateformatid = 
    			dateformatidService.getDateFormatId(warehouseId, dateFormatId);
    	log.info("DateFormatId : " + dateformatid);
		return new ResponseEntity<>(dateformatid, HttpStatus.OK);
	}
    
    @ApiOperation(response = DateFormatId.class, value = "Create DateFormatId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDateFormatId(@Valid @RequestBody AddDateFormatId newDateFormatId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		DateFormatId createdDateFormatId = dateformatidService.createDateFormatId(newDateFormatId, loginUserID);
		return new ResponseEntity<>(createdDateFormatId , HttpStatus.OK);
	}
    
    @ApiOperation(response = DateFormatId.class, value = "Update DateFormatId") // label for swagger
    @PatchMapping("/{dateFormatId}")
	public ResponseEntity<?> patchDateFormatId(@PathVariable String dateFormatId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateDateFormatId updateDateFormatId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		DateFormatId createdDateFormatId = 
				dateformatidService.updateDateFormatId(warehouseId, dateFormatId, loginUserID, updateDateFormatId);
		return new ResponseEntity<>(createdDateFormatId , HttpStatus.OK);
	}
    
    @ApiOperation(response = DateFormatId.class, value = "Delete DateFormatId") // label for swagger
	@DeleteMapping("/{dateFormatId}")
	public ResponseEntity<?> deleteDateFormatId(@PathVariable String dateFormatId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	dateformatidService.deleteDateFormatId(warehouseId, dateFormatId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}