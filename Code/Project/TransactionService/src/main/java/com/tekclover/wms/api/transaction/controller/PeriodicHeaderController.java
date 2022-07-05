package com.tekclover.wms.api.transaction.controller;

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

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.AddPeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLineEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.SearchPeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.UpdatePeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLineEntity;
import com.tekclover.wms.api.transaction.service.PeriodicHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PeriodicHeader"}, value = "PeriodicHeader  Operations related to PeriodicHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PeriodicHeader ",description = "Operations related to PeriodicHeader ")})
@RequestMapping("/periodicheader")
@RestController
public class PeriodicHeaderController {
	
	@Autowired
	PeriodicHeaderService periodicheaderService;
	
    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PeriodicHeader> periodicheaderList = periodicheaderService.getPeriodicHeaders();
		return new ResponseEntity<>(periodicheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Get a PeriodicHeader") // label for swagger 
	@GetMapping("/{cycleCountNo}")
	public ResponseEntity<?> getPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, 
		@RequestParam String palntId, @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId) {
    	PeriodicHeader periodicheader = 
    			periodicheaderService.getPeriodicHeader(companyCodeId, palntId, warehouseId, cycleCountTypeId, cycleCountNo);
		return new ResponseEntity<>(periodicheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PeriodicHeader.class, value = "Search PeriodicHeader") // label for swagger
	@PostMapping("/findPeriodicHeader")
	public List<PeriodicHeader> findPeriodicHeader(@RequestBody SearchPeriodicHeader searchPeriodicHeader)
			throws Exception {
		return periodicheaderService.findPeriodicHeader(searchPeriodicHeader);
	}
	
	@ApiOperation(response = PerpetualLineEntity.class, value = "Run PeriodicHeader") // label for swagger
   	@PostMapping("/run")
   	public ResponseEntity<?> postRunPerpetualHeader(@RequestParam String warehouseId, @RequestParam List<String> stSecIds) 
   			throws IllegalAccessException, InvocationTargetException {
   		List<PeriodicLineEntity> inventoryMovements = periodicheaderService.runPeriodicHeader(warehouseId, stSecIds);
   		return new ResponseEntity<>(inventoryMovements , HttpStatus.OK);
   	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPeriodicHeader(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PeriodicHeader createdPeriodicHeader = periodicheaderService.createPeriodicHeader(newPeriodicHeader, loginUserID);
		return new ResponseEntity<>(createdPeriodicHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader") // label for swagger
    @PatchMapping("/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, 
			@RequestParam String plantId, @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId,
			@Valid @RequestBody UpdatePeriodicHeader updatePeriodicHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PeriodicHeader createdPeriodicHeader = 
				periodicheaderService.updatePeriodicHeader(companyCodeId, plantId, warehouseId, cycleCountTypeId, 
						cycleCountNo, loginUserID, updatePeriodicHeader);
		return new ResponseEntity<>(createdPeriodicHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader") // label for swagger
	@DeleteMapping("/{cycleCountNo}")
	public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, 
			@RequestParam String palntId, @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId,
			@RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId, @RequestParam String loginUserID) {
    	periodicheaderService.deletePeriodicHeader(companyCodeId, palntId, warehouseId, cycleCountTypeId, cycleCountNo, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}