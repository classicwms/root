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

import com.tekclover.wms.api.masters.model.bom.AddBomLine;
import com.tekclover.wms.api.masters.model.bom.BomLine;
import com.tekclover.wms.api.masters.model.bom.UpdateBomLine;
import com.tekclover.wms.api.masters.service.BomLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"BomLine"}, value = "BomLine  Operations related to BomLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BomLine ",description = "Operations related to BomLine ")})
@RequestMapping("/bomline")
@RestController
public class BomLineController {
	
	@Autowired
	BomLineService bomlineService;
	
    @ApiOperation(response = BomLine.class, value = "Get all BomLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BomLine> bomlineList = bomlineService.getBomLines();
		return new ResponseEntity<>(bomlineList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BomLine.class, value = "Get a BomLine") // label for swagger 
	@GetMapping("/{bomNumber}")
	public ResponseEntity<?> getBomLine(@PathVariable Long bomNumber, @RequestParam String warehouseId, 
			@RequestParam String childItemCode) {
    	BomLine bomline = bomlineService.getBomLine(warehouseId, bomNumber, childItemCode);
    	log.info("BomLine : " + bomline);
		return new ResponseEntity<>(bomline, HttpStatus.OK);
	}
    
    @ApiOperation(response = BomLine.class, value = "Get a BomLine") // label for swagger 
	@GetMapping("/{bomNumber}/bomline")
	public ResponseEntity<?> getBomLine(@PathVariable Long bomNumber, @RequestParam String warehouseId) {
    	List<BomLine> bomline = bomlineService.getBomLine(warehouseId, bomNumber);
    	log.info("BomLine : " + bomline);
		return new ResponseEntity<>(bomline, HttpStatus.OK);
	}
    
    @ApiOperation(response = BomLine.class, value = "Create BomLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBomLine(@Valid @RequestBody AddBomLine newBomLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BomLine createdBomLine = bomlineService.createBomLine(newBomLine, loginUserID);
		return new ResponseEntity<>(createdBomLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = BomLine.class, value = "Update BomLine") // label for swagger
    @PatchMapping("/{bomNumber}")
	public ResponseEntity<?> patchBomLine(@PathVariable Long bomNumber, @RequestParam String warehouseId, @RequestParam String childItemCode,
			@Valid @RequestBody UpdateBomLine updateBomLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BomLine createdBomLine = 
				bomlineService.updateBomLine(warehouseId, bomNumber, childItemCode, loginUserID, updateBomLine);
		return new ResponseEntity<>(createdBomLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = BomLine.class, value = "Delete BomLine") // label for swagger
	@DeleteMapping("/{bomNumber}")
	public ResponseEntity<?> deleteBomLine(@PathVariable Long bomNumber, @RequestParam String warehouseId, @RequestParam String childItemCode, @RequestParam String loginUserID) {
    	bomlineService.deleteBomLine(warehouseId, bomNumber, childItemCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}