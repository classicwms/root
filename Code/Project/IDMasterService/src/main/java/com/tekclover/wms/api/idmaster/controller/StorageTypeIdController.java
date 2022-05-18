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

import com.tekclover.wms.api.idmaster.model.storagetypeid.AddStorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.UpdateStorageTypeId;
import com.tekclover.wms.api.idmaster.service.StorageTypeIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageTypeId"}, value = "StorageTypeId  Operations related to StorageTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageTypeId ",description = "Operations related to StorageTypeId ")})
@RequestMapping("/storagetypeid")
@RestController
public class StorageTypeIdController {
	
	@Autowired
	StorageTypeIdService storagetypeidService;
	
    @ApiOperation(response = StorageTypeId.class, value = "Get all StorageTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageTypeId> storagetypeidList = storagetypeidService.getStorageTypeIds();
		return new ResponseEntity<>(storagetypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StorageTypeId.class, value = "Get a StorageTypeId") // label for swagger 
	@GetMapping("/{storageTypeId}")
	public ResponseEntity<?> getStorageTypeId(@PathVariable Long storageTypeId, 
			@RequestParam String warehouseId, @RequestParam Long storageClassId) {
    	StorageTypeId storagetypeid = 
    			storagetypeidService.getStorageTypeId(warehouseId, storageClassId, storageTypeId);
    	log.info("StorageTypeId : " + storagetypeid);
		return new ResponseEntity<>(storagetypeid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = StorageTypeId.class, value = "Search StorageTypeId") // label for swagger
//	@PostMapping("/findStorageTypeId")
//	public List<StorageTypeId> findStorageTypeId(@RequestBody SearchStorageTypeId searchStorageTypeId)
//			throws Exception {
//		return storagetypeidService.findStorageTypeId(searchStorageTypeId);
//	}
    
    @ApiOperation(response = StorageTypeId.class, value = "Create StorageTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageTypeId(@Valid @RequestBody AddStorageTypeId newStorageTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		StorageTypeId createdStorageTypeId = storagetypeidService.createStorageTypeId(newStorageTypeId, loginUserID);
		return new ResponseEntity<>(createdStorageTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageTypeId.class, value = "Update StorageTypeId") // label for swagger
    @PatchMapping("/{storageTypeId}")
	public ResponseEntity<?> patchStorageTypeId(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId, 
			@Valid @RequestBody UpdateStorageTypeId updateStorageTypeId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageTypeId createdStorageTypeId = 
				storagetypeidService.updateStorageTypeId(warehouseId, storageClassId, storageTypeId, loginUserID, updateStorageTypeId);
		return new ResponseEntity<>(createdStorageTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageTypeId.class, value = "Delete StorageTypeId") // label for swagger
	@DeleteMapping("/{storageTypeId}")
	public ResponseEntity<?> deleteStorageTypeId(@PathVariable Long storageTypeId, 
			@RequestParam String warehouseId, @RequestParam Long storageClassId, @RequestParam String loginUserID) {
    	storagetypeidService.deleteStorageTypeId(warehouseId, storageClassId, storageTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}