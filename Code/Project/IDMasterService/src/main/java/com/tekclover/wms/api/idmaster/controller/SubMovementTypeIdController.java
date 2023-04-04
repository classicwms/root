package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.submovementtypeid.AddSubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.SubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.UpdateSubMovementTypeId;
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
	
    @ApiOperation(response = SubMovementTypeId.class, value = "Get all SubMovementTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<SubMovementTypeId> submovementtypeidList = submovementtypeidService.getSubMovementTypeIds();
		return new ResponseEntity<>(submovementtypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Get a SubMovementTypeId") // label for swagger 
	@GetMapping("/{subMovementTypeId}")
	public ResponseEntity<?> getSubMovementTypeId(@PathVariable String subMovementTypeId,@RequestParam String movementTypeId,
			@RequestParam String warehouseId) {
    	SubMovementTypeId submovementtypeid = 
    			submovementtypeidService.getSubMovementTypeId(warehouseId,movementTypeId, subMovementTypeId);
    	log.info("SubMovementTypeId : " + submovementtypeid);
		return new ResponseEntity<>(submovementtypeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Create SubMovementTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postSubMovementTypeId(@Valid @RequestBody AddSubMovementTypeId newSubMovementTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		SubMovementTypeId createdSubMovementTypeId = submovementtypeidService.createSubMovementTypeId(newSubMovementTypeId, loginUserID);
		return new ResponseEntity<>(createdSubMovementTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Update SubMovementTypeId") // label for swagger
    @PatchMapping("/{subMovementTypeId}")
	public ResponseEntity<?> patchSubMovementTypeId(@PathVariable String subMovementTypeId,@RequestParam String movementTypeId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateSubMovementTypeId updateSubMovementTypeId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		SubMovementTypeId createdSubMovementTypeId = 
				submovementtypeidService.updateSubMovementTypeId(warehouseId, subMovementTypeId,movementTypeId, loginUserID, updateSubMovementTypeId);
		return new ResponseEntity<>(createdSubMovementTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = SubMovementTypeId.class, value = "Delete SubMovementTypeId") // label for swagger
	@DeleteMapping("/{subMovementTypeId}")
	public ResponseEntity<?> deleteSubMovementTypeId(@PathVariable String subMovementTypeId,@RequestParam String movementTypeId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	submovementtypeidService.deleteSubMovementTypeId(warehouseId,movementTypeId, subMovementTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}