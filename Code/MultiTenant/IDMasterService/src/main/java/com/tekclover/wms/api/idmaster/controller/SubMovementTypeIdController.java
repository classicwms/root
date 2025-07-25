package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.AddSubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.FindSubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.SubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.UpdateSubMovementTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.SubMovementTypeIdService;
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
@Api(tags = {"SubMovementTypeId"}, value = "SubMovementTypeId  Operations related to SubMovementTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SubMovementTypeId ",description = "Operations related to SubMovementTypeId ")})
@RequestMapping("/submovementtypeid")
@RestController
public class SubMovementTypeIdController {
	
	@Autowired
	SubMovementTypeIdService submovementtypeidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = SubMovementTypeId.class, value = "Get all SubMovementTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<SubMovementTypeId> submovementtypeidList = submovementtypeidService.getSubMovementTypeIds();
		return new ResponseEntity<>(submovementtypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Get a SubMovementTypeId") // label for swagger 
	@GetMapping("/{subMovementTypeId}")
	public ResponseEntity<?> getSubMovementTypeId(@RequestParam String warehouseId,@RequestParam String movementTypeId,
			@PathVariable String subMovementTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			SubMovementTypeId submovementtypeid =
					submovementtypeidService.getSubMovementTypeId(warehouseId, movementTypeId, subMovementTypeId, companyCodeId, languageId, plantId);
			log.info("SubMovementTypeId : " + submovementtypeid);
			return new ResponseEntity<>(submovementtypeid, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Create SubMovementTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postSubMovementTypeId(@Valid @RequestBody AddSubMovementTypeId newSubMovementTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newSubMovementTypeId.getCompanyCodeId(), newSubMovementTypeId.getPlantId(), newSubMovementTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			SubMovementTypeId createdSubMovementTypeId = submovementtypeidService.createSubMovementTypeId(newSubMovementTypeId, loginUserID);
			return new ResponseEntity<>(createdSubMovementTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Update SubMovementTypeId") // label for swagger
    @PatchMapping("/{subMovementTypeId}")
	public ResponseEntity<?> patchSubMovementTypeId(@RequestParam String warehouseId,@RequestParam String movementTypeId, @PathVariable String subMovementTypeId,
			@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID,
			@Valid @RequestBody UpdateSubMovementTypeId updateSubMovementTypeId )
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			SubMovementTypeId createdSubMovementTypeId =
					submovementtypeidService.updateSubMovementTypeId(warehouseId, movementTypeId, subMovementTypeId, companyCodeId, languageId, plantId, loginUserID, updateSubMovementTypeId);
			return new ResponseEntity<>(createdSubMovementTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Delete SubMovementTypeId") // label for swagger
	@DeleteMapping("/{subMovementTypeId}")
	public ResponseEntity<?> deleteSubMovementTypeId(@RequestParam String warehouseId,@RequestParam String movementTypeId,
			 @PathVariable String subMovementTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			submovementtypeidService.deleteSubMovementTypeId(warehouseId, movementTypeId, subMovementTypeId, companyCodeId, languageId, plantId, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
	//Search
	@ApiOperation(response = SubMovementTypeId.class, value = "Find SubMovementTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findSubMovementTypeId(@Valid @RequestBody FindSubMovementTypeId findSubMovementTypeId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findSubMovementTypeId.getCompanyCodeId(), findSubMovementTypeId.getPlantId(), findSubMovementTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<SubMovementTypeId> createdSubMovementTypeId = submovementtypeidService.findSubMovementTypeId(findSubMovementTypeId);
			return new ResponseEntity<>(createdSubMovementTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
}