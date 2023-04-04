package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.handlingequipmentid.AddHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.HandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.UpdateHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.service.HandlingEquipmentIdService;
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
@Api(tags = {"HandlingEquipmentId"}, value = "HandlingEquipmentId  Operations related to HandlingEquipmentIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HandlingEquipmentId ",description = "Operations related to HandlingEquipmentId ")})
@RequestMapping("/handlingequipmentid")
@RestController
public class HandlingEquipmentIdController {
	
	@Autowired
	HandlingEquipmentIdService handlingequipmentidService;
	
    @ApiOperation(response = HandlingEquipmentId.class, value = "Get all HandlingEquipmentId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HandlingEquipmentId> handlingequipmentidList = handlingequipmentidService.getHandlingEquipmentIds();
		return new ResponseEntity<>(handlingequipmentidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingEquipmentId.class, value = "Get a HandlingEquipmentId") // label for swagger 
	@GetMapping("/{handlingEquipmentId}")
	public ResponseEntity<?> getHandlingEquipmentId(@PathVariable String handlingEquipmentId, @RequestParam String handlingEquipmentNumber,
			@RequestParam String warehouseId) {
    	HandlingEquipmentId handlingequipmentid = 
    			handlingequipmentidService.getHandlingEquipmentId(warehouseId, handlingEquipmentId, handlingEquipmentNumber);
    	log.info("HandlingEquipmentId : " + handlingequipmentid);
		return new ResponseEntity<>(handlingequipmentid, HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipmentId.class, value = "Create HandlingEquipmentId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHandlingEquipmentId(@Valid @RequestBody AddHandlingEquipmentId newHandlingEquipmentId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		HandlingEquipmentId createdHandlingEquipmentId = handlingequipmentidService.createHandlingEquipmentId(newHandlingEquipmentId, loginUserID);
		return new ResponseEntity<>(createdHandlingEquipmentId , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipmentId.class, value = "Update HandlingEquipmentId") // label for swagger
    @PatchMapping("/{handlingEquipmentId}")
	public ResponseEntity<?> patchHandlingEquipmentId(@PathVariable String handlingEquipmentId,@RequestParam String handlingEquipmentNumber,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateHandlingEquipmentId updateHandlingEquipmentId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipmentId createdHandlingEquipmentId = 
				handlingequipmentidService.updateHandlingEquipmentId(warehouseId, handlingEquipmentId,handlingEquipmentNumber, loginUserID, updateHandlingEquipmentId);
		return new ResponseEntity<>(createdHandlingEquipmentId , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipmentId.class, value = "Delete HandlingEquipmentId") // label for swagger
	@DeleteMapping("/{handlingEquipmentId}")
	public ResponseEntity<?> deleteHandlingEquipmentId(@PathVariable String handlingEquipmentId,@RequestParam String handlingEquipmentNumber,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	handlingequipmentidService.deleteHandlingEquipmentId(warehouseId, handlingEquipmentId,handlingEquipmentNumber, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}