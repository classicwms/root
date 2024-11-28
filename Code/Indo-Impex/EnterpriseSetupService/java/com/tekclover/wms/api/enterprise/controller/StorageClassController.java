package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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

import com.tekclover.wms.api.enterprise.model.storageclass.AddStorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.StorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.SearchStorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.UpdateStorageClass;
import com.tekclover.wms.api.enterprise.service.StorageClassService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageClass "}, value = "StorageClass  Operations related to StorageClassController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageClass ",description = "Operations related to StorageClass ")})
@RequestMapping("/storageclass")
@RestController
public class StorageClassController {
	
	@Autowired
	StorageClassService storageclassService;
	
    @ApiOperation(response = StorageClass.class, value = "Get all StorageClass details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageClass> storageclassList = storageclassService.getStorageClasss();
		return new ResponseEntity<>(storageclassList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = StorageClass.class, value = "Get a StorageClass") 
	@GetMapping("/{storageClassId}")
	public ResponseEntity<?> getStorageClass(@PathVariable Long storageClassId,@RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId,@RequestParam String warehouseId) {
	   	StorageClass storageclass = storageclassService.getStorageClass(warehouseId, storageClassId,companyId,languageId,plantId);
	   	log.info("StorageClass : " + storageclass);
		return new ResponseEntity<>(storageclass, HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageClass.class, value = "Search StorageClass") // label for swagger
	@PostMapping("/findStorageClass")
	public List<StorageClass> findStorageClass(@RequestBody SearchStorageClass searchStorageClass)
			throws Exception {
		return storageclassService.findStorageClass(searchStorageClass);
	}
    
    @ApiOperation(response = StorageClass.class, value = "Create StorageClass") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageClass(@Valid @RequestBody AddStorageClass newStorageClass, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageClass createdStorageClass = storageclassService.createStorageClass(newStorageClass, loginUserID);
		return new ResponseEntity<>(createdStorageClass , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageClass.class, value = "Update StorageClass") // label for swagger
    @PatchMapping("/{storageClassId}")
	public ResponseEntity<?> patchStorageClass(@PathVariable Long storageClassId, @RequestParam String warehouseId,@RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId,
			@Valid @RequestBody UpdateStorageClass updateStorageClass, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageClass createdStorageClass = storageclassService.updateStorageClass(warehouseId, storageClassId,companyId,languageId,plantId,updateStorageClass, loginUserID);
		return new ResponseEntity<>(createdStorageClass , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageClass.class, value = "Delete StorageClass") // label for swagger
	@DeleteMapping("/{storageClassId}")
	public ResponseEntity<?> deleteStorageClass(@PathVariable Long storageClassId, @RequestParam String warehouseId,@RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId,
			@RequestParam String loginUserID) throws ParseException {
    	storageclassService.deleteStorageClass(warehouseId, storageClassId,companyId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}