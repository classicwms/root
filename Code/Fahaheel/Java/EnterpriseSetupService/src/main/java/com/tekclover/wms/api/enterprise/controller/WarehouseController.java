package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.enterprise.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.enterprise.repository.DbConfigRepository;
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

import com.tekclover.wms.api.enterprise.model.warehouse.AddWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.SearchWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.UpdateWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;
import com.tekclover.wms.api.enterprise.service.WarehouseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Warehouse"}, value = "Warehouse  Operations related to WarehouseController") 
@SwaggerDefinition(tags = {@Tag(name = "Warehouse ",description = "Operations related to Warehouse")})
@RequestMapping("/warehouse")
@RestController
public class WarehouseController {
	
	@Autowired
	WarehouseService warehouseService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = Warehouse.class, value = "Get all Warehouse details") 
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Warehouse> plantList = warehouseService.getWarehouses();
		return new ResponseEntity<>(plantList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = Warehouse.class, value = "Get a Warehouse") 
	@GetMapping("/{warehouseId}")
	public ResponseEntity<?> getWarehouse(@PathVariable String warehouseId, @RequestParam String modeOfImplementation,@RequestParam String companyId,@RequestParam String languageId,@RequestParam String plantId,
			Long warehouseTypeId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyId,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
    	Warehouse warehouse = warehouseService.getWarehouse(warehouseId, modeOfImplementation, warehouseTypeId,companyId,plantId,languageId);
    	log.info("Warehouse : " + warehouse);
		return new ResponseEntity<>(warehouse, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Warehouse.class, value = "Search Warehouse") // label for swagger
	@PostMapping("/findWarehouse")
	public List<Warehouse> findWarehouse(@RequestBody SearchWarehouse searchWarehouse)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(searchWarehouse.getCompanyId(), searchWarehouse.getPlantId(), searchWarehouse.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return warehouseService.findWarehouse(searchWarehouse);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Warehouse.class, value = "Create Warehouse") 
	@PostMapping("")
	public ResponseEntity<?> postWarehouse(@Valid @RequestBody AddWarehouse newWarehouse, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newWarehouse.getCompanyId(), newWarehouse.getPlantId(), newWarehouse.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		Warehouse createdWarehouse = warehouseService.createWarehouse(newWarehouse, loginUserID);
		return new ResponseEntity<>(createdWarehouse , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Warehouse.class, value = "Update Warehouse") 
    @PatchMapping("/{warehouseId}")
	public ResponseEntity<?> patchWarehouse(@PathVariable String warehouseId,@RequestParam String modeOfImplementation,@RequestParam Long warehouseTypeId,
											@RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId,
			@Valid @RequestBody UpdateWarehouse updateWarehouse, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyId,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouseId,modeOfImplementation,warehouseTypeId,companyId,plantId,languageId,updateWarehouse, loginUserID);
		return new ResponseEntity<>(updatedWarehouse , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Warehouse.class, value = "Delete Warehouse") 
	@DeleteMapping("/{warehouseId}")
	public ResponseEntity<?> deleteWarehouse(@PathVariable String warehouseId,@RequestParam String modeOfImplementation,@RequestParam Long warehouseTypeId,
											 @RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId, @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyId,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		warehouseService.deleteWarehouse(warehouseId,modeOfImplementation,warehouseTypeId,companyId,plantId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}