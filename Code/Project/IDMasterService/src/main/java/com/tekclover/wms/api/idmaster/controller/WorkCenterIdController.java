package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.workcenterid.*;
import com.tekclover.wms.api.idmaster.service.WorkCenterIdService;
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
@Api(tags = {"WorkCenterId"}, value = "WorkCenterId  Operations related to WorkCenterIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "WorkCenterId ",description = "Operations related to WorkCenterId ")})
@RequestMapping("/workcenterid")
@RestController
public class WorkCenterIdController {
	
	@Autowired
	WorkCenterIdService workcenteridService;
	
    @ApiOperation(response = WorkCenterId.class, value = "Get all WorkCenterId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<WorkCenterId> workcenteridList = workcenteridService.getWorkCenterIds();
		return new ResponseEntity<>(workcenteridList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = WorkCenterId.class, value = "Get a WorkCenterId") // label for swagger 
	@GetMapping("/{workCenterId}")
	public ResponseEntity<?> getWorkCenterId(@PathVariable String workCenterId,
			@RequestParam String warehouseId) {
    	WorkCenterId workcenterid = 
    			workcenteridService.getWorkCenterId(warehouseId, workCenterId);
    	log.info("WorkCenterId : " + workcenterid);
		return new ResponseEntity<>(workcenterid, HttpStatus.OK);
	}
    
    @ApiOperation(response = WorkCenterId.class, value = "Create WorkCenterId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWorkCenterId(@Valid @RequestBody AddWorkCenterId newWorkCenterId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		WorkCenterId createdWorkCenterId = workcenteridService.createWorkCenterId(newWorkCenterId, loginUserID);
		return new ResponseEntity<>(createdWorkCenterId , HttpStatus.OK);
	}
    
    @ApiOperation(response = WorkCenterId.class, value = "Update WorkCenterId") // label for swagger
    @PatchMapping("/{workCenterId}")
	public ResponseEntity<?> patchWorkCenterId(@PathVariable String workCenterId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateWorkCenterId updateWorkCenterId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		WorkCenterId createdWorkCenterId = 
				workcenteridService.updateWorkCenterId(warehouseId, workCenterId, loginUserID, updateWorkCenterId);
		return new ResponseEntity<>(createdWorkCenterId , HttpStatus.OK);
	}
    
    @ApiOperation(response = WorkCenterId.class, value = "Delete WorkCenterId") // label for swagger
	@DeleteMapping("/{workCenterId}")
	public ResponseEntity<?> deleteWorkCenterId(@PathVariable String workCenterId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	workcenteridService.deleteWorkCenterId(warehouseId, workCenterId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}