package com.ustorage.api.master.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ustorage.api.master.model.storagetype.AddStorageType;
import com.ustorage.api.master.model.storagetype.StorageType;
import com.ustorage.api.master.model.storagetype.UpdateStorageType;
import com.ustorage.api.master.service.StorageTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "StorageType" }, value = "StorageType Operations related to StorageTypeController") 
@SwaggerDefinition(tags = { @Tag(name = "StorageType", description = "Operations related to StorageType") })
@RequestMapping("/storageType")
@RestController
public class StorageTypeController {

	@Autowired
	StorageTypeService storageTypeService;

	@ApiOperation(response = StorageType.class, value = "Get all StorageType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageType> storageTypeList = storageTypeService.getStorageType();
		return new ResponseEntity<>(storageTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Get a StorageType") // label for swagger
	@GetMapping("/{storageTypeId}")
	public ResponseEntity<?> getStorageType(@PathVariable String storageTypeId) {
		StorageType dbStorageType = storageTypeService.getStorageType(storageTypeId);
		log.info("StorageType : " + dbStorageType);
		return new ResponseEntity<>(dbStorageType, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Create StorageType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageType(@Valid @RequestBody AddStorageType newStorageType,
			@RequestParam String loginUserID) throws Exception {
		StorageType createdStorageType = storageTypeService.createStorageType(newStorageType, loginUserID);
		return new ResponseEntity<>(createdStorageType, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Update StorageType") // label for swagger
	@PatchMapping("/{storageTypeId}")
	public ResponseEntity<?> patchStorageType(@PathVariable String storageTypeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateStorageType updateStorageType)
			throws IllegalAccessException, InvocationTargetException {
		StorageType updatedStorageType = storageTypeService.updateStorageType(storageTypeId, loginUserID,
				updateStorageType);
		return new ResponseEntity<>(updatedStorageType, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Delete StorageType") // label for swagger
	@DeleteMapping("/{storageTypeId}")
	public ResponseEntity<?> deleteStorageType(@PathVariable String storageTypeId, @RequestParam String loginUserID) {
		storageTypeService.deleteStorageType(storageTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
