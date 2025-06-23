package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.aisleid.*;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import com.tekclover.wms.api.idmaster.service.AisleIdService;
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
@Api(tags = {"AisleId"}, value = "AisleId  Operations related to AisleIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "AisleId ",description = "Operations related to AisleId ")})
@RequestMapping("/aisleid")
@RestController
public class AisleIdController {
	@Autowired
	private LanguageIdRepository languageIdRepository;

	@Autowired
	AisleIdService aisleidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = AisleId.class, value = "Get all AisleId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<AisleId> aisleidList = aisleidService.getAisleIds();
		return new ResponseEntity<>(aisleidList, HttpStatus.OK); 
	}
	@ApiOperation(response = AisleId.class, value = "Get a AisleId") // label for swagger
	@GetMapping("/{aisleId}")
	public ResponseEntity<?> getAisleId(@RequestParam String warehouseId,@PathVariable String aisleId,@RequestParam Long floorId,@RequestParam String storageSectionId,
										@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		AisleId aisleid =
    			aisleidService.getAisleId(warehouseId,aisleId,floorId,storageSectionId,companyCodeId,languageId,plantId);
    	log.info("AisleId : " + aisleid);
		return new ResponseEntity<>(aisleid, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = AisleId.class, value = "Create AisleId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postAisleId(@Valid @RequestBody AddAisleId newAisleId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newAisleId.getCompanyCodeId(), newAisleId.getPlantId(), newAisleId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		AisleId createdAisleId = aisleidService.createAisleId(newAisleId, loginUserID);
		return new ResponseEntity<>(createdAisleId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

    @ApiOperation(response = AisleId.class, value = "Update AisleId") // label for swagger
    @PatchMapping("/{aisleId}")
	public ResponseEntity<?> patchAisleId(@RequestParam String warehouseId,@PathVariable String aisleId, @RequestParam Long floorId, @RequestParam String storageSectionId,
										  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID,
										  @Valid @RequestBody UpdateAisleId updateAisleId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		AisleId createdAisleId = 
				aisleidService.updateAisleId(warehouseId,aisleId,floorId,storageSectionId,companyCodeId,languageId,plantId,loginUserID,updateAisleId);
		return new ResponseEntity<>(createdAisleId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

    @ApiOperation(response = AisleId.class, value = "Delete AisleId") // label for swagger
	@DeleteMapping("/{aisleId}")
	public ResponseEntity<?> deleteAisleId(@RequestParam String warehouseId,@PathVariable String aisleId,@RequestParam Long floorId, @RequestParam String storageSectionId,
			                               @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		aisleidService.deleteAisleId(warehouseId, aisleId, floorId, storageSectionId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = AisleId.class, value = "Find AisleId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findAisleId(@Valid @RequestBody FindAisleId findAisleId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findAisleId.getCompanyCodeId(), findAisleId.getPlantId(), findAisleId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<AisleId> createdAisleId = aisleidService.findAisleId(findAisleId);
		return new ResponseEntity<>(createdAisleId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}