package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.adhocmoduleid.*;
import com.tekclover.wms.api.idmaster.service.AdhocModuleIdService;
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
@Api(tags = {"AdhocModuleId"}, value = "AdhocModuleId  Operations related to AdhocModuleIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "AdhocModuleId ",description = "Operations related to AdhocModuleId ")})
@RequestMapping("/adhocmoduleid")
@RestController
public class AdhocModuleIdController {
	
	@Autowired
	AdhocModuleIdService adhocmoduleidService;
	
    @ApiOperation(response = AdhocModuleId.class, value = "Get all AdhocModuleId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<AdhocModuleId> adhocmoduleidList = adhocmoduleidService.getAdhocModuleIds();
		return new ResponseEntity<>(adhocmoduleidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = AdhocModuleId.class, value = "Get a AdhocModuleId") // label for swagger 
	@GetMapping("/{adhocModuleId}")
	public ResponseEntity<?> getAdhocModuleId(@RequestParam String moduleId,@PathVariable String adhocModuleId,
			@RequestParam String warehouseId) {
    	AdhocModuleId adhocmoduleid = 
    			adhocmoduleidService.getAdhocModuleId(warehouseId, moduleId, adhocModuleId);
    	log.info("AdhocModuleId : " + adhocmoduleid);
		return new ResponseEntity<>(adhocmoduleid, HttpStatus.OK);
	}
    
    @ApiOperation(response = AdhocModuleId.class, value = "Create AdhocModuleId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postAdhocModuleId(@Valid @RequestBody AddAdhocModuleId newAdhocModuleId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		AdhocModuleId createdAdhocModuleId = adhocmoduleidService.createAdhocModuleId(newAdhocModuleId, loginUserID);
		return new ResponseEntity<>(createdAdhocModuleId , HttpStatus.OK);
	}
    
    @ApiOperation(response = AdhocModuleId.class, value = "Update AdhocModuleId") // label for swagger
    @PatchMapping("/{adhocModuleId}")
	public ResponseEntity<?> patchAdhocModuleId( @RequestParam String warehouseId, @PathVariable String adhocModuleId, @RequestParam String moduleId,
			@Valid @RequestBody UpdateAdhocModuleId updateAdhocModuleId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		AdhocModuleId createdAdhocModuleId = 
				adhocmoduleidService.updateAdhocModuleId(warehouseId, moduleId, adhocModuleId, loginUserID, updateAdhocModuleId);
		return new ResponseEntity<>(createdAdhocModuleId , HttpStatus.OK);
	}
    
    @ApiOperation(response = AdhocModuleId.class, value = "Delete AdhocModuleId") // label for swagger
	@DeleteMapping("/{adhocModuleId}")
	public ResponseEntity<?> deleteAdhocModuleId(@PathVariable String adhocModuleId,
			@RequestParam String warehouseId, @RequestParam String moduleId, @RequestParam String loginUserID) {
    	adhocmoduleidService.deleteAdhocModuleId(warehouseId, moduleId, adhocModuleId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}