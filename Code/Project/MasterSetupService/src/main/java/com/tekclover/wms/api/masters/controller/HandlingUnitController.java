package com.tekclover.wms.api.masters.controller;

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

import com.tekclover.wms.api.masters.model.handlingunit.AddHandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.HandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.SearchHandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.UpdateHandlingUnit;
import com.tekclover.wms.api.masters.service.HandlingUnitService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"HandlingUnit"}, value = "HandlingUnit  Operations related to HandlingUnitController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HandlingUnit ",description = "Operations related to HandlingUnit ")})
@RequestMapping("/handlingunit")
@RestController
public class HandlingUnitController {
	
	@Autowired
	HandlingUnitService handlingUnitService;
	
    @ApiOperation(response = HandlingUnit.class, value = "Get all HandlingUnit details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HandlingUnit> handlingUnitList = handlingUnitService.getHandlingUnits();
		return new ResponseEntity<>(handlingUnitList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingUnit.class, value = "Get a HandlingUnit") // label for swagger 
	@GetMapping("/{handlingUnit}")
	public ResponseEntity<?> getHandlingUnit(@PathVariable String handlingUnit) {
    	HandlingUnit dbHandlingUnit = handlingUnitService.getHandlingUnit(handlingUnit);
    	log.info("HandlingUnit : " + dbHandlingUnit);
		return new ResponseEntity<>(dbHandlingUnit, HttpStatus.OK);
	}
    
	@ApiOperation(response = HandlingUnit.class, value = "Search HandlingUnit") // label for swagger
	@PostMapping("/findHandlingUnit")
	public List<HandlingUnit> findHandlingUnit(@RequestBody SearchHandlingUnit searchHandlingUnit)
			throws Exception {
		return handlingUnitService.findHandlingUnit(searchHandlingUnit);
	}
	
    @ApiOperation(response = HandlingUnit.class, value = "Create HandlingUnit") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHandlingUnit(@Valid @RequestBody AddHandlingUnit newHandlingUnit, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnit createdHandlingUnit = handlingUnitService.createHandlingUnit(newHandlingUnit, loginUserID);
		return new ResponseEntity<>(createdHandlingUnit , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnit.class, value = "Update HandlingUnit") // label for swagger
    @PatchMapping("/{handlingUnit}")
	public ResponseEntity<?> patchHandlingUnit(@PathVariable String handlingUnit, 
			@Valid @RequestBody UpdateHandlingUnit updateHandlingUnit, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnit createdHandlingUnit = handlingUnitService.updateHandlingUnit(handlingUnit, updateHandlingUnit, loginUserID);
		return new ResponseEntity<>(createdHandlingUnit , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnit.class, value = "Delete HandlingUnit") // label for swagger
	@DeleteMapping("/{handlingUnit}")
	public ResponseEntity<?> deleteHandlingUnit(@PathVariable String handlingUnit, @RequestParam String loginUserID) {
    	handlingUnitService.deleteHandlingUnit(handlingUnit, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}