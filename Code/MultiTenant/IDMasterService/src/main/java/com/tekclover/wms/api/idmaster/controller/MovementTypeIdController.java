package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.movementtypeid.AddMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.FindMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.UpdateMovementTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.service.MovementTypeIdService;
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
@Api(tags = {"MovementTypeId"}, value = "MovementTypeId  Operations related to MovementTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "MovementTypeId ",description = "Operations related to MovementTypeId ")})
@RequestMapping("/movementtypeid")
@RestController
public class MovementTypeIdController {
	@Autowired
	private PlantIdRepository plantIdRepository;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	MovementTypeIdService movementtypeidService;
	
    @ApiOperation(response = MovementTypeId.class, value = "Get all MovementTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<MovementTypeId> movementtypeidList = movementtypeidService.getMovementTypeIds();
		return new ResponseEntity<>(movementtypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = MovementTypeId.class, value = "Get a MovementTypeId") // label for swagger 
	@GetMapping("/{movementTypeId}")
	public ResponseEntity<?> getMovementTypeId(@RequestParam String warehouseId,@PathVariable String movementTypeId,
											   @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		MovementTypeId movementtypeid =
    			movementtypeidService.getMovementTypeId(warehouseId, movementTypeId,companyCodeId,languageId,plantId);
    	log.info("MovementTypeId : " + movementtypeid);
		return new ResponseEntity<>(movementtypeid, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = MovementTypeId.class, value = "Create MovementTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postMovementTypeId(@Valid @RequestBody AddMovementTypeId newMovementTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newMovementTypeId.getCompanyCodeId(), newMovementTypeId.getPlantId(), newMovementTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		MovementTypeId createdMovementTypeId = movementtypeidService.createMovementTypeId(newMovementTypeId, loginUserID);
		return new ResponseEntity<>(createdMovementTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = MovementTypeId.class, value = "Update MovementTypeId") // label for swagger
    @PatchMapping("/{movementTypeId}")
	public ResponseEntity<?> patchMovementTypeId(@RequestParam String warehouseId,@PathVariable String movementTypeId,
												 @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateMovementTypeId updateMovementTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		MovementTypeId createdMovementTypeId =
				movementtypeidService.updateMovementTypeId(warehouseId, movementTypeId,companyCodeId,languageId,plantId,loginUserID, updateMovementTypeId);
		return new ResponseEntity<>(createdMovementTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = MovementTypeId.class, value = "Delete MovementTypeId") // label for swagger
	@DeleteMapping("/{movementTypeId}")
	public ResponseEntity<?> deleteMovementTypeId(@RequestParam String warehouseId,@PathVariable String movementTypeId,
												  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			 @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		movementtypeidService.deleteMovementTypeId(warehouseId, movementTypeId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = MovementTypeId.class, value = "Find MovementTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findMovementTypeId(@Valid @RequestBody FindMovementTypeId findMovementTypeId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findMovementTypeId.getCompanyCodeId(), findMovementTypeId.getPlantId(), findMovementTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<MovementTypeId> createdMovementTypeId = movementtypeidService.findMovementTypeId(findMovementTypeId);
		return new ResponseEntity<>(createdMovementTypeId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}