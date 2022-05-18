package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.handlingequipment.AddHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.SearchHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.UpdateHandlingEquipment;
import com.tekclover.wms.api.masters.service.HandlingEquipmentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"HandlingEquipment"}, value = "HandlingEquipment  Operations related to HandlingEquipmentController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HandlingEquipment ",description = "Operations related to HandlingEquipment ")})
@RequestMapping("/handlingequipment")
@RestController
public class HandlingEquipmentController {
	
	@Autowired
	HandlingEquipmentService handlingequipmentService;
	
    @ApiOperation(response = HandlingEquipment.class, value = "Get all HandlingEquipment details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HandlingEquipment> handlingequipmentList = handlingequipmentService.getHandlingEquipments();
		return new ResponseEntity<>(handlingequipmentList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Get a HandlingEquipment") // label for swagger 
	@GetMapping("/{handlingEquipmentId}")
	public ResponseEntity<?> getHandlingEquipment(@PathVariable String handlingEquipmentId) {
    	HandlingEquipment handlingequipment = handlingequipmentService.getHandlingEquipment(handlingEquipmentId);
    	log.info("HandlingEquipment : " + handlingequipment);
		return new ResponseEntity<>(handlingequipment, HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Get a HandlingEquipment") // label for swagger 
   	@GetMapping("/{heBarcode}/barCode")
   	public ResponseEntity<?> getHandlingEquipment(@PathVariable String heBarcode, @RequestParam String warehouseId) {
       	HandlingEquipment handlingequipment = handlingequipmentService.getHandlingEquipment(warehouseId, heBarcode);
       	log.info("HandlingEquipment : " + handlingequipment);
   		return new ResponseEntity<>(handlingequipment, HttpStatus.OK);
   	}
    
	@ApiOperation(response = BusinessPartner.class, value = "Search HandlingEquipment") // label for swagger
	@PostMapping("/findBusinessPartner")
	public List<HandlingEquipment> findBusinessPartner(@RequestBody SearchHandlingEquipment searchHandlingEquipment)
			throws Exception {
		return handlingequipmentService.findHandlingEquipment(searchHandlingEquipment);
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Create HandlingEquipment") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHandlingEquipment(@Valid @RequestBody AddHandlingEquipment newHandlingEquipment, 
			@RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipment createdHandlingEquipment = handlingequipmentService.createHandlingEquipment(newHandlingEquipment, 
				loginUserID);
		return new ResponseEntity<>(createdHandlingEquipment , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Update HandlingEquipment") // label for swagger
    @PatchMapping("/{handlingEquipmentId}")
	public ResponseEntity<?> patchHandlingEquipment(@PathVariable String handlingEquipmentId, 
			@Valid @RequestBody UpdateHandlingEquipment updateHandlingEquipment, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipment createdHandlingEquipment = handlingequipmentService.updateHandlingEquipment(handlingEquipmentId, 
				updateHandlingEquipment, loginUserID);
		return new ResponseEntity<>(createdHandlingEquipment , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Delete HandlingEquipment") // label for swagger
	@DeleteMapping("/{handlingEquipmentId}")
	public ResponseEntity<?> deleteHandlingEquipment(@PathVariable String handlingEquipmentId, @RequestParam String loginUserID) {
    	handlingequipmentService.deleteHandlingEquipment(handlingEquipmentId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}