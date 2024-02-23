package com.tekclover.wms.api.idmaster.controller;

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

import com.tekclover.wms.api.idmaster.model.warehouseid.AddWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.FindWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.UpdateWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import com.tekclover.wms.api.idmaster.service.WarehouseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Warehouse"}, value = "Warehouse  Operations related to WarehouseController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Warehouse ",description = "Operations related to Warehouse ")})
@RequestMapping("/warehouseid")
@RestController
public class WarehouseController {
	@Autowired
	private LanguageIdRepository languageIdRepository;

	@Autowired
	WarehouseService warehouseService;
	
    @ApiOperation(response = Warehouse.class, value = "Get all Warehouse details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Warehouse> warehouseidList = warehouseService.getWarehouses();
		return new ResponseEntity<>(warehouseidList, HttpStatus.OK);
	}
    
    @ApiOperation(response = Warehouse.class, value = "Get a Warehouse") // label for swagger 
	@GetMapping("/{warehouseId}")
	public ResponseEntity<?> getWarehouse(@PathVariable String warehouseId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId) {
    	Warehouse warehouseid = warehouseService.getWarehouse(warehouseId,companyCodeId,plantId,languageId);
    	log.info("Warehouse : " + warehouseid);
		return new ResponseEntity<>(warehouseid, HttpStatus.OK);
	}
    
	@ApiOperation(response = Warehouse.class, value = "Get a Warehouse") // label for swagger 
	@GetMapping("/warehouseId")
	public ResponseEntity<?> getWarehouse (@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId) {
    	Warehouse warehouseid = warehouseService.getWarehouse(companyCodeId, plantId, languageId);
    	log.info("Warehouse : " + warehouseid);
		return new ResponseEntity<>(warehouseid, HttpStatus.OK);
	}
    
    @ApiOperation(response = Warehouse.class, value = "Create Warehouse") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWarehouse(@Valid @RequestBody AddWarehouse newWarehouse, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		Warehouse createdWarehouse = warehouseService.createWarehouse(newWarehouse, loginUserID);
		return new ResponseEntity<>(createdWarehouse , HttpStatus.OK);
	}
    
    @ApiOperation(response = Warehouse.class, value = "Update Warehouse") // label for swagger
    @PatchMapping("/{warehouseId}")
	public ResponseEntity<?> patchWarehouse(@PathVariable String warehouseId, @RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,
			@Valid @RequestBody UpdateWarehouse updateWarehouse, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Warehouse createdWarehouse = 
				warehouseService.updateWarehouse(warehouseId,companyCodeId,plantId,languageId,loginUserID, updateWarehouse);
		return new ResponseEntity<>(createdWarehouse , HttpStatus.OK);
	}
    
    @ApiOperation(response = Warehouse.class, value = "Delete Warehouse") // label for swagger
	@DeleteMapping("/{warehouseId}")
	public ResponseEntity<?> deleteWarehouse(@PathVariable String warehouseId, @RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,
			@RequestParam String loginUserID) {
    	warehouseService.deleteWarehouse(warehouseId,companyCodeId,plantId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Warehouse.class, value = "Find Warehouse") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findWarehouse(@Valid @RequestBody FindWarehouse findWarehouse) throws Exception {
		List<Warehouse> createdWarehouse = warehouseService.findWarehouse(findWarehouse);
		return new ResponseEntity<>(createdWarehouse, HttpStatus.OK);
	}
}