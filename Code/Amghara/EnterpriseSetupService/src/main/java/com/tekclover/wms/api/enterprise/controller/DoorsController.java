package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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

import com.tekclover.wms.api.enterprise.model.doors.AddDoors;
import com.tekclover.wms.api.enterprise.model.doors.Doors;
import com.tekclover.wms.api.enterprise.model.doors.UpdateDoors;
import com.tekclover.wms.api.enterprise.service.DoorsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Doors "}, value = "Doors  Operations related to DoorsController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Doors ",description = "Operations related to Doors ")})
@RequestMapping("/doors")
@RestController
public class DoorsController {
	
	@Autowired
	DoorsService doorsService;
	
    @ApiOperation(response = Doors.class, value = "Get all Doors details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Doors> doorsList = doorsService.getDoorss();
		return new ResponseEntity<>(doorsList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = Doors.class, value = "Get a Doors") // label for swagger 
	@GetMapping("/{doorNumber}")
	public ResponseEntity<?> getDoors(@PathVariable String doorNumber) {
    	Doors doors = doorsService.getDoors(doorNumber);
    	log.info("Doors : " + doors);
		return new ResponseEntity<>(doors, HttpStatus.OK);
	}
    
    @ApiOperation(response = Doors.class, value = "Create Doors") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDoors(@Valid @RequestBody AddDoors newDoors, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Doors createdDoors = doorsService.createDoors(newDoors, loginUserID);
		return new ResponseEntity<>(createdDoors , HttpStatus.OK);
	}
    
    @ApiOperation(response = Doors.class, value = "Update Doors") // label for swagger
    @PatchMapping("/{doorNumber}")
	public ResponseEntity<?> patchDoors(@PathVariable String doorNumber, 
			@Valid @RequestBody UpdateDoors updateDoors, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Doors createdDoors = doorsService.updateDoors(doorNumber, updateDoors, loginUserID);
		return new ResponseEntity<>(createdDoors , HttpStatus.OK);
	}
    
    @ApiOperation(response = Doors.class, value = "Delete Doors") // label for swagger
	@DeleteMapping("/{doorNumber}")
	public ResponseEntity<?> deleteDoors(@PathVariable String doorNumber, @RequestParam String loginUserID) throws ParseException {
    	doorsService.deleteDoors(doorNumber, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}