package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.shelfid.*;
import com.tekclover.wms.api.idmaster.service.ShelfIdService;
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
@Api(tags = {"ShelfId"}, value = "ShelfId  Operations related to ShelfIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ShelfId ",description = "Operations related to ShelfId ")})
@RequestMapping("/shelfid")
@RestController
public class ShelfIdController {
	
	@Autowired
	ShelfIdService shelfidService;
	
    @ApiOperation(response = ShelfId.class, value = "Get all ShelfId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ShelfId> shelfidList = shelfidService.getShelfIds();
		return new ResponseEntity<>(shelfidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ShelfId.class, value = "Get a ShelfId") // label for swagger 
	@GetMapping("/{shelfId}")
	public ResponseEntity<?> getShelfId(@PathVariable String shelfId,
			@RequestParam String warehouseId,@RequestParam String aisleId, @RequestParam String floorId,@RequestParam String spanId, @RequestParam String storageSectionId) {
    	ShelfId shelfid = 
    			shelfidService.getShelfId(warehouseId, aisleId,shelfId,spanId, floorId, storageSectionId);
    	log.info("ShelfId : " + shelfid);
		return new ResponseEntity<>(shelfid, HttpStatus.OK);
	}
    
    @ApiOperation(response = ShelfId.class, value = "Create ShelfId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postShelfId(@Valid @RequestBody AddShelfId newShelfId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		ShelfId createdShelfId = shelfidService.createShelfId(newShelfId, loginUserID);
		return new ResponseEntity<>(createdShelfId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ShelfId.class, value = "Update ShelfId") // label for swagger
    @PatchMapping("/{shelfId}")
	public ResponseEntity<?> patchShelfId(@PathVariable String shelfId,
			@RequestParam String warehouseId, @RequestParam String aisleId, @RequestParam String floorId, @RequestParam String storageSectionId,
			@Valid @RequestBody UpdateShelfId updateShelfId,@RequestParam String spanId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ShelfId createdShelfId = 
				shelfidService.updateShelfId(warehouseId,aisleId, shelfId,spanId,floorId, storageSectionId, loginUserID, updateShelfId);
		return new ResponseEntity<>(createdShelfId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ShelfId.class, value = "Delete ShelfId") // label for swagger
	@DeleteMapping("/{shelfId}")
	public ResponseEntity<?> deleteShelfId(@PathVariable String shelfId,@RequestParam String aisleId,@RequestParam String floorId, @RequestParam String storageSectionId,
			@RequestParam String warehouseId,@RequestParam String spanId, @RequestParam String loginUserID) {
    	shelfidService.deleteShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}