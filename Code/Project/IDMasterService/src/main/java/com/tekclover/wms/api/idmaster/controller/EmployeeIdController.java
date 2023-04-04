package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.employeeid.*;
import com.tekclover.wms.api.idmaster.service.EmployeeIdService;
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
@Api(tags = {"EmployeeId"}, value = "EmployeeId  Operations related to EmployeeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "EmployeeId ",description = "Operations related to EmployeeId ")})
@RequestMapping("/employeeid")
@RestController
public class EmployeeIdController {
	
	@Autowired
	EmployeeIdService employeeidService;
	
    @ApiOperation(response = EmployeeId.class, value = "Get all EmployeeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<EmployeeId> employeeidList = employeeidService.getEmployeeIds();
		return new ResponseEntity<>(employeeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = EmployeeId.class, value = "Get a EmployeeId") // label for swagger 
	@GetMapping("/{employeeId}")
	public ResponseEntity<?> getEmployeeId(@PathVariable String employeeId,
			@RequestParam String warehouseId) {
    	EmployeeId employeeid = 
    			employeeidService.getEmployeeId(warehouseId, employeeId);
    	log.info("EmployeeId : " + employeeid);
		return new ResponseEntity<>(employeeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = EmployeeId.class, value = "Create EmployeeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postEmployeeId(@Valid @RequestBody AddEmployeeId newEmployeeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		EmployeeId createdEmployeeId = employeeidService.createEmployeeId(newEmployeeId, loginUserID);
		return new ResponseEntity<>(createdEmployeeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = EmployeeId.class, value = "Update EmployeeId") // label for swagger
    @PatchMapping("/{employeeId}")
	public ResponseEntity<?> patchEmployeeId(@PathVariable String employeeId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateEmployeeId updateEmployeeId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		EmployeeId createdEmployeeId = 
				employeeidService.updateEmployeeId(warehouseId, employeeId, loginUserID, updateEmployeeId);
		return new ResponseEntity<>(createdEmployeeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = EmployeeId.class, value = "Delete EmployeeId") // label for swagger
	@DeleteMapping("/{employeeId}")
	public ResponseEntity<?> deleteEmployeeId(@PathVariable String employeeId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	employeeidService.deleteEmployeeId(warehouseId, employeeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}