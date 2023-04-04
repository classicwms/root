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

import com.tekclover.wms.api.idmaster.model.storagesectionid.AddStorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.UpdateStorageSectionId;
import com.tekclover.wms.api.idmaster.service.StorageSectionIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageSectionId"}, value = "StorageSectionId  Operations related to StorageSectionIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageSectionId ",description = "Operations related to StorageSectionId ")})
@RequestMapping("/storagesectionid")
@RestController
public class StorageSectionIdController {
	
	@Autowired
	StorageSectionIdService storagesectionidService;
	
    @ApiOperation(response = StorageSectionId.class, value = "Get all StorageSectionId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageSectionId> storagesectionidList = storagesectionidService.getStorageSectionIds();
		return new ResponseEntity<>(storagesectionidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StorageSectionId.class, value = "Get a StorageSectionId") // label for swagger 
	@GetMapping("/{storageSectionId}")
	public ResponseEntity<?> getStorageSectionId(@PathVariable String storageSectionId,
			@RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSection) {
    	StorageSectionId storagesectionid = 
    			storagesectionidService.getStorageSectionId(warehouseId, floorId, storageSectionId, storageSection);
    	log.info("StorageSectionId : " + storagesectionid);
		return new ResponseEntity<>(storagesectionid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = StorageSectionId.class, value = "Search StorageSectionId") // label for swagger
//	@PostMapping("/findStorageSectionId")
//	public List<StorageSectionId> findStorageSectionId(@RequestBody SearchStorageSectionId searchStorageSectionId)
//			throws Exception {
//		return storagesectionidService.findStorageSectionId(searchStorageSectionId);
//	}
    
    @ApiOperation(response = StorageSectionId.class, value = "Create StorageSectionId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageSectionId(@Valid @RequestBody AddStorageSectionId newStorageSectionId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		StorageSectionId createdStorageSectionId = storagesectionidService.createStorageSectionId(newStorageSectionId, loginUserID);
		return new ResponseEntity<>(createdStorageSectionId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageSectionId.class, value = "Update StorageSectionId") // label for swagger
    @PatchMapping("/{storageSectionId}")
	public ResponseEntity<?> patchStorageSectionId(@PathVariable String storageSectionId,
			@RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSection, 
			@Valid @RequestBody UpdateStorageSectionId updateStorageSectionId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageSectionId createdStorageSectionId = 
				storagesectionidService.updateStorageSectionId(warehouseId, floorId, storageSectionId, storageSection, loginUserID, updateStorageSectionId);
		return new ResponseEntity<>(createdStorageSectionId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageSectionId.class, value = "Delete StorageSectionId") // label for swagger
	@DeleteMapping("/{storageSectionId}")
	public ResponseEntity<?> deleteStorageSectionId(@PathVariable String storageSectionId,
			@RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSection, @RequestParam String loginUserID) {
    	storagesectionidService.deleteStorageSectionId(warehouseId, floorId, storageSectionId, storageSection, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}