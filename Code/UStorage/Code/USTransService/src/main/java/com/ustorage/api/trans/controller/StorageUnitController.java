package com.ustorage.api.trans.controller;

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

import com.ustorage.api.trans.model.storageunit.*;

import com.ustorage.api.trans.service.StorageUnitService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "StorageUnit" }, value = "StorageUnit Operations related to StorageUnitController") 
@SwaggerDefinition(tags = { @Tag(name = "StorageUnit", description = "Operations related to StorageUnit") })
@RequestMapping("/storageUnit")
@RestController
public class StorageUnitController {

	@Autowired
	StorageUnitService storageUnitService;

	@ApiOperation(response = StorageUnit.class, value = "Get all StorageUnit details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageUnit> storageUnitList = storageUnitService.getStorageUnit();
		return new ResponseEntity<>(storageUnitList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Get a StorageUnit") // label for swagger
	@GetMapping("/{storageUnitId}")
	public ResponseEntity<?> getStorageUnit(@PathVariable String storageUnitId) {
		StorageUnit dbStorageUnit = storageUnitService.getStorageUnit(storageUnitId);
		log.info("StorageUnit : " + dbStorageUnit);
		return new ResponseEntity<>(dbStorageUnit, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Create StorageUnit") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageUnit(@Valid @RequestBody AddStorageUnit newStorageUnit,
			@RequestParam String loginUserID) throws Exception {
		StorageUnit createdStorageUnit = storageUnitService.createStorageUnit(newStorageUnit, loginUserID);
		return new ResponseEntity<>(createdStorageUnit, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Update StorageUnit") // label for swagger
	@PatchMapping("/{storageUnit}")
	public ResponseEntity<?> patchStorageUnit(@PathVariable String storageUnit,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateStorageUnit updateStorageUnit)
			throws IllegalAccessException, InvocationTargetException {
		StorageUnit updatedStorageUnit = storageUnitService.updateStorageUnit(storageUnit, loginUserID,
				updateStorageUnit);
		return new ResponseEntity<>(updatedStorageUnit, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Delete StorageUnit") // label for swagger
	@DeleteMapping("/{storageUnit}")
	public ResponseEntity<?> deleteStorageUnit(@PathVariable String storageUnit, @RequestParam String loginUserID) {
		storageUnitService.deleteStorageUnit(storageUnit, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = StorageUnit.class, value = "Find StorageUnit") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findStorageUnit(@Valid @RequestBody FindStorageUnit findStorageUnit) throws Exception {
		List<StorageUnit> createdStorageUnit = storageUnitService.findStorageUnit(findStorageUnit);
		return new ResponseEntity<>(createdStorageUnit, HttpStatus.OK);
	}
}
