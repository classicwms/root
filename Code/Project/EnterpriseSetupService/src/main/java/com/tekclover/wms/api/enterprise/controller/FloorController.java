package com.tekclover.wms.api.enterprise.controller;

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

import com.tekclover.wms.api.enterprise.model.floor.AddFloor;
import com.tekclover.wms.api.enterprise.model.floor.Floor;
import com.tekclover.wms.api.enterprise.model.floor.SearchFloor;
import com.tekclover.wms.api.enterprise.model.floor.UpdateFloor;
import com.tekclover.wms.api.enterprise.service.FloorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Floor "}, value = "Floor  Operations related to FloorController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Floor ",description = "Operations related to Floor ")})
@RequestMapping("/floor")
@RestController
public class FloorController {
	
	@Autowired
	FloorService floorService;
	
    @ApiOperation(response = Floor.class, value = "Get all Floor details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Floor> floorList = floorService.getFloors();
		return new ResponseEntity<>(floorList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = Floor.class, value = "Get a Floor") 
	@GetMapping("/{floorId}")
	public ResponseEntity<?> getFloor(@PathVariable Long floorId, @RequestParam String warehouseId) {
	   	Floor floor = floorService.getFloor(warehouseId, floorId);
	   	log.info("Floor : " + floor);
		return new ResponseEntity<>(floor, HttpStatus.OK);
	}
    
    @ApiOperation(response = Floor.class, value = "Search Floor") // label for swagger
	@PostMapping("/findFloor")
	public List<Floor> findFloor(@RequestBody SearchFloor searchFloor)
			throws Exception {
		return floorService.findFloor(searchFloor);
	}
    
    @ApiOperation(response = Floor.class, value = "Create Floor") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postFloor(@Valid @RequestBody AddFloor newFloor, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Floor createdFloor = floorService.createFloor(newFloor, loginUserID);
		return new ResponseEntity<>(createdFloor , HttpStatus.OK);
	}
    
    @ApiOperation(response = Floor.class, value = "Update Floor") // label for swagger
    @PatchMapping("/{floorId}")
	public ResponseEntity<?> patchFloor(@PathVariable Long floorId, @RequestParam String warehouseId, 
			@Valid @RequestBody UpdateFloor updateFloor, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Floor createdFloor = floorService.updateFloor(warehouseId, floorId, updateFloor, loginUserID);
		return new ResponseEntity<>(createdFloor , HttpStatus.OK);
	}
    
    @ApiOperation(response = Floor.class, value = "Delete Floor") // label for swagger
	@DeleteMapping("/{floorId}")
	public ResponseEntity<?> deleteFloor(@PathVariable Long floorId, @RequestParam String warehouseId, 
		@RequestParam String loginUserID) {
    	floorService.deleteFloor(warehouseId, floorId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}