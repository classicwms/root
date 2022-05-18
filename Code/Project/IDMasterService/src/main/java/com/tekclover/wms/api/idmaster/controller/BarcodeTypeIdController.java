package com.tekclover.wms.api.idmaster.controller;

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

import com.tekclover.wms.api.idmaster.model.barcodetypeid.AddBarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.UpdateBarcodeTypeId;
import com.tekclover.wms.api.idmaster.service.BarcodeTypeIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"BarcodeTypeId"}, value = "BarcodeTypeId  Operations related to BarcodeTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BarcodeTypeId ",description = "Operations related to BarcodeTypeId ")})
@RequestMapping("/barcodetypeid")
@RestController
public class BarcodeTypeIdController {
	
	@Autowired
	BarcodeTypeIdService barcodetypeidService;
	
    @ApiOperation(response = BarcodeTypeId.class, value = "Get all BarcodeTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BarcodeTypeId> barcodetypeidList = barcodetypeidService.getBarcodeTypeIds();
		return new ResponseEntity<>(barcodetypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Get a BarcodeTypeId") // label for swagger 
	@GetMapping("/{barcodeTypeId}")
	public ResponseEntity<?> getBarcodeTypeId(@PathVariable Long barcodeTypeId, 
			@RequestParam String warehouseId) {
    	BarcodeTypeId barcodetypeid = 
    			barcodetypeidService.getBarcodeTypeId(warehouseId, barcodeTypeId);
    	log.info("BarcodeTypeId : " + barcodetypeid);
		return new ResponseEntity<>(barcodetypeid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = BarcodeTypeId.class, value = "Search BarcodeTypeId") // label for swagger
//	@PostMapping("/findBarcodeTypeId")
//	public List<BarcodeTypeId> findBarcodeTypeId(@RequestBody SearchBarcodeTypeId searchBarcodeTypeId)
//			throws Exception {
//		return barcodetypeidService.findBarcodeTypeId(searchBarcodeTypeId);
//	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Create BarcodeTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBarcodeTypeId(@Valid @RequestBody AddBarcodeTypeId newBarcodeTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		BarcodeTypeId createdBarcodeTypeId = barcodetypeidService.createBarcodeTypeId(newBarcodeTypeId, loginUserID);
		return new ResponseEntity<>(createdBarcodeTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Update BarcodeTypeId") // label for swagger
    @PatchMapping("/{barcodeTypeId}")
	public ResponseEntity<?> patchBarcodeTypeId(@PathVariable Long barcodeTypeId, 
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateBarcodeTypeId updateBarcodeTypeId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BarcodeTypeId createdBarcodeTypeId = 
				barcodetypeidService.updateBarcodeTypeId(warehouseId, barcodeTypeId, loginUserID, updateBarcodeTypeId);
		return new ResponseEntity<>(createdBarcodeTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Delete BarcodeTypeId") // label for swagger
	@DeleteMapping("/{barcodeTypeId}")
	public ResponseEntity<?> deleteBarcodeTypeId(@PathVariable Long barcodeTypeId, 
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	barcodetypeidService.deleteBarcodeTypeId(warehouseId, barcodeTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}