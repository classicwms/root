package com.tekclover.wms.api.masters.controller;

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

import com.tekclover.wms.api.masters.model.storagebin.AddStorageBin;
import com.tekclover.wms.api.masters.model.storagebin.SearchStorageBin;
import com.tekclover.wms.api.masters.model.storagebin.StorageBin;
import com.tekclover.wms.api.masters.model.storagebin.StorageBinPutAway;
import com.tekclover.wms.api.masters.model.storagebin.UpdateStorageBin;
import com.tekclover.wms.api.masters.service.StorageBinService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageBin"}, value = "StorageBin  Operations related to StorageBinController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageBin ",description = "Operations related to StorageBin ")})
@RequestMapping("/storagebin")
@RestController
public class StorageBinController {
	
	@Autowired
	StorageBinService storagebinService;
	
    @ApiOperation(response = StorageBin.class, value = "Get all StorageBin details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageBin> storagebinList = storagebinService.getStorageBins();
		return new ResponseEntity<>(storagebinList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
	@GetMapping("/{storageBin}")
	public ResponseEntity<?> getStorageBin(@PathVariable String storageBin) {
    	StorageBin storagebin = storagebinService.getStorageBin(storageBin);
    	log.info("StorageBin : " + storagebin);
		return new ResponseEntity<>(storagebin, HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
   	@GetMapping("/{storageBin}/warehouseId")
   	public ResponseEntity<?> getStorageBin(@PathVariable String storageBin, @RequestParam String warehouseId) {
       	StorageBin storagebin = storagebinService.getStorageBin(warehouseId, storageBin);
       	log.info("StorageBin : " + storagebin);
   		return new ResponseEntity<>(storagebin, HttpStatus.OK);
   	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
   	@GetMapping("/{warehouseId}/status")
   	public ResponseEntity<?> getStorageBinByStatus(@PathVariable String warehouseId, @RequestParam Long statusId) {
       	List<StorageBin> storagebin = storagebinService.getStorageBinByStatus(warehouseId, statusId);
       	log.info("StorageBin : " + storagebin);
   		return new ResponseEntity<>(storagebin, HttpStatus.OK);
   	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin Status") // label for swagger 
   	@GetMapping("/{warehouseId}/status-not-equal")
   	public ResponseEntity<?> getStorageBinByStatusNotEqual (@PathVariable String warehouseId, 
   			@RequestParam Long statusId) {
       	List<StorageBin> storagebin = storagebinService.getStorageBinByStatusNotEqual(warehouseId, statusId);
       	log.info("StorageBin : " + storagebin);
   		return new ResponseEntity<>(storagebin, HttpStatus.OK);
   	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
   	@PostMapping("/putaway")
   	public ResponseEntity<?> getStorageBin(@RequestBody StorageBinPutAway storageBinPutAway) {
		List<StorageBin> storagebin = storagebinService.getStorageBin(storageBinPutAway);
		log.info("StorageBin : " + storagebin);
		return new ResponseEntity<>(storagebin, HttpStatus.OK);
   	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
   	@GetMapping("/sectionId")
   	public ResponseEntity<?> getStorageBinByStorageSectionId(@RequestParam String warehouseId, @RequestParam List<String> stSectionIds) {
		List<StorageBin> storagebin = storagebinService.getStorageBin(warehouseId, stSectionIds);
		log.info("StorageBin : " + storagebin);
		return new ResponseEntity<>(storagebin, HttpStatus.OK);
   	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
   	@GetMapping("/{warehouseId}/bins/{binClassId}")
   	public ResponseEntity<?> getStorageBin(@PathVariable String warehouseId, @PathVariable Long binClassId) {
       	StorageBin storagebin = storagebinService.getStorageBin(warehouseId, binClassId);
       	log.info("StorageBin : " + storagebin);
   		return new ResponseEntity<>(storagebin, HttpStatus.OK);
   	}
    
	@ApiOperation(response = StorageBin.class, value = "Search StorageBin") // label for swagger
	@PostMapping("/findStorageBin")
	public List<StorageBin> findStorageBin(@RequestBody SearchStorageBin searchStorageBin)
			throws Exception {
		return storagebinService.findStorageBin(searchStorageBin);
	}
    
    @ApiOperation(response = StorageBin.class, value = "Create StorageBin") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageBin(@Valid @RequestBody AddStorageBin newStorageBin, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageBin createdStorageBin = storagebinService.createStorageBin(newStorageBin, loginUserID);
		return new ResponseEntity<>(createdStorageBin , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageBin.class, value = "Update StorageBin") // label for swagger
    @PatchMapping("/{storageBin}")
	public ResponseEntity<?> patchStorageBin(@PathVariable String storageBin, 
			@Valid @RequestBody UpdateStorageBin updateStorageBin, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageBin createdStorageBin = storagebinService.updateStorageBin(storageBin, updateStorageBin, loginUserID);
		return new ResponseEntity<>(createdStorageBin , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageBin.class, value = "Delete StorageBin") // label for swagger
	@DeleteMapping("/{storageBin}")
	public ResponseEntity<?> deleteStorageBin(@PathVariable String storageBin, @RequestParam String loginUserID) {
    	storagebinService.deleteStorageBin(storageBin, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}