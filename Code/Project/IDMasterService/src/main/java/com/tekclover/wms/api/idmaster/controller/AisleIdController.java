package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.aisleid.*;
import com.tekclover.wms.api.idmaster.service.AisleIdService;
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
@Api(tags = {"AisleId"}, value = "AisleId  Operations related to AisleIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "AisleId ",description = "Operations related to AisleId ")})
@RequestMapping("/aisleid")
@RestController
public class AisleIdController {
	
	@Autowired
	AisleIdService aisleidService;
	
    @ApiOperation(response = AisleId.class, value = "Get all AisleId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<AisleId> aisleidList = aisleidService.getAisleIds();
		return new ResponseEntity<>(aisleidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = AisleId.class, value = "Get a AisleId") // label for swagger 
	@GetMapping("/{aisleId}")
	public ResponseEntity<?> getAisleId(@PathVariable String aisleId,
			@RequestParam String warehouseId, @RequestParam String floorId, @RequestParam String storageSectionId) {
    	AisleId aisleid = 
    			aisleidService.getAisleId(warehouseId, aisleId, floorId, storageSectionId);
    	log.info("AisleId : " + aisleid);
		return new ResponseEntity<>(aisleid, HttpStatus.OK);
	}
    
    @ApiOperation(response = AisleId.class, value = "Create AisleId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postAisleId(@Valid @RequestBody AddAisleId newAisleId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		AisleId createdAisleId = aisleidService.createAisleId(newAisleId, loginUserID);
		return new ResponseEntity<>(createdAisleId , HttpStatus.OK);
	}
    
    @ApiOperation(response = AisleId.class, value = "Update AisleId") // label for swagger
    @PatchMapping("/{aisleId}")
	public ResponseEntity<?> patchAisleId(@PathVariable String aisleId,
			@RequestParam String warehouseId, @RequestParam String floorId, @RequestParam String storageSectionId,
			@Valid @RequestBody UpdateAisleId updateAisleId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		AisleId createdAisleId = 
				aisleidService.updateAisleId(warehouseId, aisleId,floorId, storageSectionId, loginUserID, updateAisleId);
		return new ResponseEntity<>(createdAisleId , HttpStatus.OK);
	}
    
    @ApiOperation(response = AisleId.class, value = "Delete AisleId") // label for swagger
	@DeleteMapping("/{aisleId}")
	public ResponseEntity<?> deleteAisleId(@PathVariable String aisleId,@RequestParam String floorId, @RequestParam String storageSectionId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	aisleidService.deleteAisleId(warehouseId, aisleId, floorId, storageSectionId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}