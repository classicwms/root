package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.movementtypeid.AddMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.UpdateMovementTypeId;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"MovementTypeId"}, value = "MovementTypeId  Operations related to MovementTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "MovementTypeId ",description = "Operations related to MovementTypeId ")})
@RequestMapping("/movementtypeid")
@RestController
public class MovementTypeIdController {
	
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
	public ResponseEntity<?> getMovementTypeId(@PathVariable String movementTypeId,
			@RequestParam String warehouseId) {
    	MovementTypeId movementtypeid = 
    			movementtypeidService.getMovementTypeId(warehouseId, movementTypeId);
    	log.info("MovementTypeId : " + movementtypeid);
		return new ResponseEntity<>(movementtypeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = MovementTypeId.class, value = "Create MovementTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postMovementTypeId(@Valid @RequestBody AddMovementTypeId newMovementTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		MovementTypeId createdMovementTypeId = movementtypeidService.createMovementTypeId(newMovementTypeId, loginUserID);
		return new ResponseEntity<>(createdMovementTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = MovementTypeId.class, value = "Update MovementTypeId") // label for swagger
    @PatchMapping("/{movementTypeId}")
	public ResponseEntity<?> patchMovementTypeId(@PathVariable String movementTypeId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateMovementTypeId updateMovementTypeId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		MovementTypeId createdMovementTypeId = 
				movementtypeidService.updateMovementTypeId(warehouseId, movementTypeId, loginUserID, updateMovementTypeId);
		return new ResponseEntity<>(createdMovementTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = MovementTypeId.class, value = "Delete MovementTypeId") // label for swagger
	@DeleteMapping("/{movementTypeId}")
	public ResponseEntity<?> deleteMovementTypeId(@PathVariable String movementTypeId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	movementtypeidService.deleteMovementTypeId(warehouseId, movementTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}