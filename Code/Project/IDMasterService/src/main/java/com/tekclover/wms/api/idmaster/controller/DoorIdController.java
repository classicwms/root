package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.doorid.*;
import com.tekclover.wms.api.idmaster.service.DoorIdService;
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
@Api(tags = {"DoorId"}, value = "DoorId  Operations related to DoorIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DoorId ",description = "Operations related to DoorId ")})
@RequestMapping("/doorid")
@RestController
public class DoorIdController {
	
	@Autowired
	DoorIdService dooridService;
	
    @ApiOperation(response = DoorId.class, value = "Get all DoorId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<DoorId> dooridList = dooridService.getDoorIds();
		return new ResponseEntity<>(dooridList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = DoorId.class, value = "Get a DoorId") // label for swagger 
	@GetMapping("/{doorId}")
	public ResponseEntity<?> getDoorId(@PathVariable String doorId,
			@RequestParam String warehouseId) {
    	DoorId doorid = 
    			dooridService.getDoorId(warehouseId, doorId);
    	log.info("DoorId : " + doorid);
		return new ResponseEntity<>(doorid, HttpStatus.OK);
	}
    
    @ApiOperation(response = DoorId.class, value = "Create DoorId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDoorId(@Valid @RequestBody AddDoorId newDoorId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		DoorId createdDoorId = dooridService.createDoorId(newDoorId, loginUserID);
		return new ResponseEntity<>(createdDoorId , HttpStatus.OK);
	}
    
    @ApiOperation(response = DoorId.class, value = "Update DoorId") // label for swagger
    @PatchMapping("/{doorId}")
	public ResponseEntity<?> patchDoorId(@PathVariable String doorId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateDoorId updateDoorId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		DoorId createdDoorId = 
				dooridService.updateDoorId(warehouseId, doorId, loginUserID, updateDoorId);
		return new ResponseEntity<>(createdDoorId , HttpStatus.OK);
	}
    
    @ApiOperation(response = DoorId.class, value = "Delete DoorId") // label for swagger
	@DeleteMapping("/{doorId}")
	public ResponseEntity<?> deleteDoorId(@PathVariable String doorId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	dooridService.deleteDoorId(warehouseId, doorId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}