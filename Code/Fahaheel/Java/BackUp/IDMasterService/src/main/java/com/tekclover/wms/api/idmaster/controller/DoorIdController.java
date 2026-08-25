package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.doorid.*;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.DoorIdService;
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
@Api(tags = {"DoorId"}, value = "DoorId  Operations related to DoorIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DoorId ",description = "Operations related to DoorId ")})
@RequestMapping("/doorid")
@RestController
public class DoorIdController {
	@Autowired
	private CompanyIdRepository companyIdRepository;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	DoorIdService dooridService;
	
    @ApiOperation(response = DoorId.class, value = "Get all DoorId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<DoorId> dooridList = dooridService.getDoorIds();
		return new ResponseEntity<>(dooridList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = DoorId.class, value = "Get a DoorId") // label for swagger 
	@GetMapping("/{doorId}")
	public ResponseEntity<?> getDoorId(@RequestParam String warehouseId,@PathVariable String doorId,@RequestParam String companyCodeId,
									   @RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		DoorId doorid =
    			dooridService.getDoorId(warehouseId,doorId,companyCodeId,languageId,plantId);
    	log.info("DoorId : " + doorid);
		return new ResponseEntity<>(doorid, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = DoorId.class, value = "Create DoorId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDoorId(@Valid @RequestBody AddDoorId newDoorId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newDoorId.getCompanyCodeId(), newDoorId.getPlantId(), newDoorId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		DoorId createdDoorId = dooridService.createDoorId(newDoorId, loginUserID);
		return new ResponseEntity<>(createdDoorId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = DoorId.class, value = "Update DoorId") // label for swagger
    @PatchMapping("/{doorId}")
	public ResponseEntity<?> patchDoorId(@RequestParam String warehouseId,@PathVariable String doorId,@RequestParam String companyCodeId,@RequestParam String languageId,
			@RequestParam String plantId,@Valid @RequestBody UpdateDoorId updateDoorId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		DoorId createdDoorId =
				dooridService.updateDoorId(warehouseId, doorId,companyCodeId,languageId,plantId,loginUserID, updateDoorId);
		return new ResponseEntity<>(createdDoorId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = DoorId.class, value = "Delete DoorId") // label for swagger
	@DeleteMapping("/{doorId}")
	public ResponseEntity<?> deleteDoorId(@RequestParam String warehouseId,@PathVariable String doorId,
										  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		dooridService.deleteDoorId(warehouseId, doorId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = DoorId.class, value = "Find DoorId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findDoorId(@Valid @RequestBody FindDoorId findDoorId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findDoorId.getCompanyCodeId(), findDoorId.getPlantId(), findDoorId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<DoorId> createdDoorId = dooridService.findDoorId(findDoorId);
		return new ResponseEntity<>(createdDoorId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}