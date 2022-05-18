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

import com.tekclover.wms.api.enterprise.model.storagesection.AddStorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.SearchStorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.StorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.UpdateStorageSection;
import com.tekclover.wms.api.enterprise.service.StorageSectionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageSection "}, value = "StorageSection  Operations related to StorageSectionController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageSection ",description = "Operations related to StorageSection ")})
@RequestMapping("/storagesection")
@RestController
public class StorageSectionController {
	
	@Autowired
	StorageSectionService storagesectionService;
	
    @ApiOperation(response = StorageSection.class, value = "Get all StorageSection details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageSection> storagesectionList = storagesectionService.getStorageSections();
		return new ResponseEntity<>(storagesectionList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = StorageSection.class, value = "Get a StorageSection") 
	@GetMapping("/{storageSectionId}")
	public ResponseEntity<?> getStorageSection(@PathVariable String storageSectionId, @RequestParam String warehouseId, 
			@RequestParam Long floorId) {
	   	StorageSection storagesection = 
	   			storagesectionService.getStorageSection(warehouseId, floorId, storageSectionId);
	   	log.info("StorageSection : " + storagesection);
		return new ResponseEntity<>(storagesection, HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageSection.class, value = "Search StorageSection") // label for swagger
	@PostMapping("/findStorageSection")
	public List<StorageSection> findStorageSection(@RequestBody SearchStorageSection searchStorageSection)
			throws Exception {
		return storagesectionService.findStorageSection(searchStorageSection);
	}
    
    @ApiOperation(response = StorageSection.class, value = "Create StorageSection") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageSection(@Valid @RequestBody AddStorageSection newStorageSection, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		StorageSection createdStorageSection = 
				storagesectionService.createStorageSection(newStorageSection, loginUserID);
		return new ResponseEntity<>(createdStorageSection , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageSection.class, value = "Update StorageSection") // label for swagger
    @PatchMapping("/{storageSectionId}")
	public ResponseEntity<?> patchStorageSection(@PathVariable String storageSectionId, @RequestParam String warehouseId, 
			@RequestParam Long floorId, @Valid @RequestBody UpdateStorageSection updateStorageSection, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		StorageSection createdStorageSection = 
				storagesectionService.updateStorageSection(warehouseId, floorId, storageSectionId, updateStorageSection, loginUserID);
		return new ResponseEntity<>(createdStorageSection , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageSection.class, value = "Delete StorageSection") // label for swagger
	@DeleteMapping("/{storageSectionId}")
	public ResponseEntity<?> deleteStorageSection(@PathVariable String storageSectionId, @RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String loginUserID) {
    	storagesectionService.deleteStorageSection(warehouseId, floorId, storageSectionId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}