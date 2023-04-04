package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.handlingunitid.AddHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.UpdateHandlingUnitId;
import com.tekclover.wms.api.idmaster.service.HandlingUnitIdService;
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
@Api(tags = {"HandlingUnitId"}, value = "HandlingUnitId  Operations related to HandlingUnitIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HandlingUnitId ",description = "Operations related to HandlingUnitId ")})
@RequestMapping("/handlingunitid")
@RestController
public class HandlingUnitIdController {
	
	@Autowired
	HandlingUnitIdService handlingunitidService;
	
    @ApiOperation(response = HandlingUnitId.class, value = "Get all HandlingUnitId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HandlingUnitId> handlingunitidList = handlingunitidService.getHandlingUnitIds();
		return new ResponseEntity<>(handlingunitidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Get a HandlingUnitId") // label for swagger 
	@GetMapping("/{handlingUnitId}")
	public ResponseEntity<?> getHandlingUnitId(@PathVariable String handlingUnitId, @RequestParam String handlingUnitNumber,
			@RequestParam String warehouseId) {
    	HandlingUnitId handlingunitid = 
    			handlingunitidService.getHandlingUnitId(warehouseId, handlingUnitId, handlingUnitNumber);
    	log.info("HandlingUnitId : " + handlingunitid);
		return new ResponseEntity<>(handlingunitid, HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Create HandlingUnitId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHandlingUnitId(@Valid @RequestBody AddHandlingUnitId newHandlingUnitId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		HandlingUnitId createdHandlingUnitId = handlingunitidService.createHandlingUnitId(newHandlingUnitId, loginUserID);
		return new ResponseEntity<>(createdHandlingUnitId , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Update HandlingUnitId") // label for swagger
    @PatchMapping("/{handlingUnitId}")
	public ResponseEntity<?> patchHandlingUnitId(@PathVariable String handlingUnitId,@RequestParam String handlingUnitNumber,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateHandlingUnitId updateHandlingUnitId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnitId createdHandlingUnitId = 
				handlingunitidService.updateHandlingUnitId(warehouseId, handlingUnitId,handlingUnitNumber, loginUserID, updateHandlingUnitId);
		return new ResponseEntity<>(createdHandlingUnitId , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Delete HandlingUnitId") // label for swagger
	@DeleteMapping("/{handlingUnitId}")
	public ResponseEntity<?> deleteHandlingUnitId(@PathVariable String handlingUnitId,@RequestParam String handlingUnitNumber,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	handlingunitidService.deleteHandlingUnitId(warehouseId, handlingUnitId,handlingUnitNumber, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}