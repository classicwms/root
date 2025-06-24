package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.shelfid.*;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.ShelfIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ShelfId"}, value = "ShelfId  Operations related to ShelfIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ShelfId ",description = "Operations related to ShelfId ")})
@RequestMapping("/shelfid")
@RestController
public class ShelfIdController {
	
	@Autowired
	ShelfIdService shelfidService;
	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = ShelfId.class, value = "Get all ShelfId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ShelfId> shelfidList = shelfidService.getShelfIds();
		return new ResponseEntity<>(shelfidList, HttpStatus.OK); 
	}

    @ApiOperation(response = ShelfId.class, value = "Get a ShelfId") // label for swagger 
	@GetMapping("/{shelfId}")
	public ResponseEntity<?> getShelfId(@RequestParam String warehouseId,@RequestParam String aisleId,@PathVariable String shelfId,
										@RequestParam String spanId,@RequestParam Long floorId,@RequestParam String storageSectionId,
										@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ShelfId shelfid =
    			shelfidService.getShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId,companyCodeId,languageId,plantId);
    	log.info("ShelfId : " + shelfid);
		return new ResponseEntity<>(shelfid, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ShelfId.class, value = "Create ShelfId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postShelfId(@Valid @RequestBody AddShelfId newShelfId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newShelfId.getCompanyCodeId(), newShelfId.getPlantId(), newShelfId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ShelfId createdShelfId = shelfidService.createShelfId(newShelfId, loginUserID);
		return new ResponseEntity<>(createdShelfId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ShelfId.class, value = "Update ShelfId") // label for swagger
    @PatchMapping("/{shelfId}")
	public ResponseEntity<?> patchShelfId(@RequestParam String warehouseId,@RequestParam String aisleId,@PathVariable String shelfId,
										  @RequestParam String spanId,@RequestParam Long floorId,@RequestParam String storageSectionId,
										  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID,
										  @Valid @RequestBody UpdateShelfId updateShelfId )
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ShelfId createdShelfId =
				shelfidService.updateShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId,companyCodeId,languageId,plantId, loginUserID, updateShelfId);
		return new ResponseEntity<>(createdShelfId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ShelfId.class, value = "Delete ShelfId") // label for swagger
	@DeleteMapping("/{shelfId}")
	public ResponseEntity<?> deleteShelfId(@RequestParam String warehouseId,@RequestParam String aisleId,@PathVariable String shelfId,
										   @RequestParam String spanId,@RequestParam Long floorId,@RequestParam String storageSectionId,
										   @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		shelfidService.deleteShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = ShelfId.class, value = "Find ShelfId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findShelfId(@Valid @RequestBody FindShelfId findShelfId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findShelfId.getCompanyCodeId(), findShelfId.getPlantId(), findShelfId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ShelfId> createdShelfId = shelfidService.findShelfId(findShelfId);
		return new ResponseEntity<>(createdShelfId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}