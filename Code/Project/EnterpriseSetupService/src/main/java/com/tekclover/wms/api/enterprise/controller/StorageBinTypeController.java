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

import com.tekclover.wms.api.enterprise.model.storagebintype.AddStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.SearchStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.StorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.UpdateStorageBinType;
import com.tekclover.wms.api.enterprise.service.StorageBinTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageBinType "}, value = "StorageBinType  Operations related to StorageBinTypeController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageBinType ",description = "Operations related to StorageBinType ")})
@RequestMapping("/storagebintype")
@RestController
public class StorageBinTypeController {
	
	@Autowired
	StorageBinTypeService storagebintypeService;
	
    @ApiOperation(response = StorageBinType.class, value = "Get all StorageBinType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageBinType> storagebintypeList = storagebintypeService.getStorageBinTypes();
		return new ResponseEntity<>(storagebintypeList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = StorageBinType.class, value = "Get a StorageBinType") 
	@GetMapping("/{storageBinTypeId}")
	public ResponseEntity<?> getStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, 
			@RequestParam Long storageTypeId) {
	   	StorageBinType storagebintype = 
	   			storagebintypeService.getStorageBinType(warehouseId, storageTypeId, storageBinTypeId);
	   	log.info("StorageBinType : " + storagebintype);
		return new ResponseEntity<>(storagebintype, HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageBinType.class, value = "Search StorageBinType") // label for swagger
	@PostMapping("/findStorageBinType")
	public List<StorageBinType> findStorageBinType(@RequestBody SearchStorageBinType searchStorageBinType)
			throws Exception {
		return storagebintypeService.findStorageBinType(searchStorageBinType);
	}
    
    @ApiOperation(response = StorageBinType.class, value = "Create StorageBinType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageBinType(@Valid @RequestBody AddStorageBinType newStorageBinType, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		StorageBinType createdStorageBinType = 
				storagebintypeService.createStorageBinType(newStorageBinType, loginUserID);
		return new ResponseEntity<>(createdStorageBinType , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageBinType.class, value = "Update StorageBinType") // label for swagger
    @PatchMapping("/{storageBinTypeId}")
	public ResponseEntity<?> patchStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, @RequestParam Long storageTypeId,
			@Valid @RequestBody UpdateStorageBinType updateStorageBinType, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageBinType createdStorageBinType = storagebintypeService.updateStorageBinType(warehouseId, storageTypeId, storageBinTypeId, updateStorageBinType, loginUserID);
		return new ResponseEntity<>(createdStorageBinType , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageBinType.class, value = "Delete StorageBinType") // label for swagger
	@DeleteMapping("/{storageBinTypeId}")
	public ResponseEntity<?> deleteStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, @RequestParam Long storageTypeId, 
			@RequestParam String loginUserID) {
    	storagebintypeService.deleteStorageBinType(warehouseId, storageTypeId, storageBinTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}