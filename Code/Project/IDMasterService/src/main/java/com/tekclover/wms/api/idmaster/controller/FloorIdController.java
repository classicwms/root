package com.tekclover.wms.api.idmaster.controller;

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

import com.tekclover.wms.api.idmaster.model.floorid.AddFloorId;
import com.tekclover.wms.api.idmaster.model.floorid.FloorId;
import com.tekclover.wms.api.idmaster.model.floorid.UpdateFloorId;
import com.tekclover.wms.api.idmaster.service.FloorIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"FloorId"}, value = "FloorId  Operations related to FloorIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "FloorId ",description = "Operations related to FloorId ")})
@RequestMapping("/floorid")
@RestController
public class FloorIdController {
	
	@Autowired
	FloorIdService flooridService;
	
    @ApiOperation(response = FloorId.class, value = "Get all FloorId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<FloorId> flooridList = flooridService.getFloorIds();
		return new ResponseEntity<>(flooridList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = FloorId.class, value = "Get a FloorId") // label for swagger 
	@GetMapping("/{floorId}")
	public ResponseEntity<?> getFloorId(@PathVariable Long floorId, 
			@RequestParam String warehouseId) {
    	FloorId floorid = 
    			flooridService.getFloorId(warehouseId, floorId);
    	log.info("FloorId : " + floorid);
		return new ResponseEntity<>(floorid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = FloorId.class, value = "Search FloorId") // label for swagger
//	@PostMapping("/findFloorId")
//	public List<FloorId> findFloorId(@RequestBody SearchFloorId searchFloorId)
//			throws Exception {
//		return flooridService.findFloorId(searchFloorId);
//	}
    
    @ApiOperation(response = FloorId.class, value = "Create FloorId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postFloorId(@Valid @RequestBody AddFloorId newFloorId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		FloorId createdFloorId = flooridService.createFloorId(newFloorId, loginUserID);
		return new ResponseEntity<>(createdFloorId , HttpStatus.OK);
	}
    
    @ApiOperation(response = FloorId.class, value = "Update FloorId") // label for swagger
    @PatchMapping("/{floorId}")
	public ResponseEntity<?> patchFloorId(@PathVariable Long floorId, 
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateFloorId updateFloorId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		FloorId createdFloorId = 
				flooridService.updateFloorId(warehouseId, floorId, loginUserID, updateFloorId);
		return new ResponseEntity<>(createdFloorId , HttpStatus.OK);
	}
    
    @ApiOperation(response = FloorId.class, value = "Delete FloorId") // label for swagger
	@DeleteMapping("/{floorId}")
	public ResponseEntity<?> deleteFloorId(@PathVariable Long floorId, 
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	flooridService.deleteFloorId(warehouseId, floorId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}