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

import com.ustorage.api.master.model.warehouse.AddWarehouse;
import com.ustorage.api.master.model.warehouse.Warehouse;
import com.ustorage.api.master.model.warehouse.UpdateWarehouse;
import com.ustorage.api.master.service.WarehouseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Warehouse" }, value = "Warehouse Operations related to WarehouseController") 
@SwaggerDefinition(tags = { @Tag(name = "Warehouse", description = "Operations related to Warehouse") })
@RequestMapping("/warehouse")
@RestController
public class WarehouseController {

	@Autowired
	WarehouseService warehouseService;

	@ApiOperation(response = Warehouse.class, value = "Get all Warehouse details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Warehouse> warehouseList = warehouseService.getWarehouse();
		return new ResponseEntity<>(warehouseList, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Get a Warehouse") // label for swagger
	@GetMapping("/{warehouseId}")
	public ResponseEntity<?> getWarehouse(@PathVariable String warehouseId) {
		Warehouse dbWarehouse = warehouseService.getWarehouse(warehouseId);
		log.info("Warehouse : " + dbWarehouse);
		return new ResponseEntity<>(dbWarehouse, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Create Warehouse") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWarehouse(@Valid @RequestBody AddWarehouse newWarehouse,
			@RequestParam String loginUserID) throws Exception {
		Warehouse createdWarehouse = warehouseService.createWarehouse(newWarehouse, loginUserID);
		return new ResponseEntity<>(createdWarehouse, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Update Warehouse") // label for swagger
	@PatchMapping("/{warehouseId}")
	public ResponseEntity<?> patchWarehouse(@PathVariable String warehouseId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateWarehouse updateWarehouse)
			throws IllegalAccessException, InvocationTargetException {
		Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouseId, loginUserID,
				updateWarehouse);
		return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Delete Warehouse") // label for swagger
	@DeleteMapping("/{warehouseId}")
	public ResponseEntity<?> deleteWarehouse(@PathVariable String warehouseId, @RequestParam String loginUserID) {
		warehouseService.deleteWarehouse(warehouseId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
