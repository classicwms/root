package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.AddHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.FindHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.HandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.UpdateHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.HandlingEquipmentIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.Data;
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
@Api(tags = {"HandlingEquipmentId"}, value = "HandlingEquipmentId  Operations related to HandlingEquipmentIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HandlingEquipmentId ",description = "Operations related to HandlingEquipmentId ")})
@RequestMapping("/handlingequipmentid")
@RestController
public class HandlingEquipmentIdController {
	
	@Autowired
	HandlingEquipmentIdService handlingequipmentidService;

	@Autowired
	DbConfigRepository  dbConfigRepository;
    @ApiOperation(response = HandlingEquipmentId.class, value = "Get all HandlingEquipmentId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HandlingEquipmentId> handlingequipmentidList = handlingequipmentidService.getHandlingEquipmentIds();
		return new ResponseEntity<>(handlingequipmentidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingEquipmentId.class, value = "Get a HandlingEquipmentId") // label for swagger 
	@GetMapping("/{handlingEquipmentNumber}")
	public ResponseEntity<?> getHandlingEquipmentId(@PathVariable Long handlingEquipmentNumber,@RequestParam String warehouseId,
													@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		HandlingEquipmentId handlingequipmentid =
    			handlingequipmentidService.getHandlingEquipmentId(warehouseId,handlingEquipmentNumber,companyCodeId,languageId,plantId);
    	log.info("HandlingEquipmentId : " + handlingequipmentid);
		return new ResponseEntity<>(handlingequipmentid, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = HandlingEquipmentId.class, value = "Create HandlingEquipmentId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHandlingEquipmentId(@Valid @RequestBody AddHandlingEquipmentId newHandlingEquipmentId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newHandlingEquipmentId.getCompanyCodeId(), newHandlingEquipmentId.getPlantId(), newHandlingEquipmentId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		HandlingEquipmentId createdHandlingEquipmentId = handlingequipmentidService.createHandlingEquipmentId(newHandlingEquipmentId, loginUserID);
		return new ResponseEntity<>(createdHandlingEquipmentId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = HandlingEquipmentId.class, value = "Update HandlingEquipmentId") // label for swagger
    @PatchMapping("/{handlingEquipmentNumber}")
	public ResponseEntity<?> patchHandlingEquipmentId(@PathVariable Long handlingEquipmentNumber,
													  @RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,
													  @RequestParam String plantId, @RequestParam String loginUserID,
													  @Valid @RequestBody UpdateHandlingEquipmentId updateHandlingEquipmentId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		HandlingEquipmentId createdHandlingEquipmentId =
				handlingequipmentidService.updateHandlingEquipmentId(warehouseId,handlingEquipmentNumber,companyCodeId,
						languageId,plantId,loginUserID, updateHandlingEquipmentId);
		return new ResponseEntity<>(createdHandlingEquipmentId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = HandlingEquipmentId.class, value = "Delete HandlingEquipmentId") // label for swagger
	@DeleteMapping("/{handlingEquipmentNumber}")
	public ResponseEntity<?> deleteHandlingEquipmentId(@PathVariable Long handlingEquipmentNumber,
														@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,
													   @RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		handlingequipmentidService.deleteHandlingEquipmentId(warehouseId,handlingEquipmentNumber,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	//Search
	@ApiOperation(response = HandlingEquipmentId.class, value = "Find HandlingEquipmentId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findHandlingEquipmentId(@Valid @RequestBody FindHandlingEquipmentId findHandlingEquipmentId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findHandlingEquipmentId.getCompanyCodeId(), findHandlingEquipmentId.getPlantId(), findHandlingEquipmentId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<HandlingEquipmentId> createdHandlingEquipmentId =
				handlingequipmentidService.findHandlingEquipmentId(findHandlingEquipmentId);
		return new ResponseEntity<>(createdHandlingEquipmentId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}