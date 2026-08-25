package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.enterprise.model.batchserial.*;
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

import com.tekclover.wms.api.enterprise.service.BatchSerialService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"BatchSerial "}, value = "BatchSerial  Operations related to BatchSerialController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BatchSerial ",description = "Operations related to BatchSerial ")})
@RequestMapping("/batchserial")
@RestController
public class BatchSerialController {
	
	@Autowired
	BatchSerialService batchserialService;
	
    @ApiOperation(response = BatchSerial.class, value = "Get all BatchSerial details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BatchSerial> batchserialList = batchserialService.getBatchSerials();
		return new ResponseEntity<>(batchserialList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BatchSerial.class, value = "Get a BatchSerial") // label for swagger
	@GetMapping("/{storageMethod}")
	public ResponseEntity<?> getBatchSerial(@PathVariable String storageMethod,@RequestParam String plantId,
											@RequestParam String companyId,@RequestParam String languageId,
											@RequestParam String warehouseId,@RequestParam Long levelId,@RequestParam String maintenance) {
		List<BatchSerial> batchserial = batchserialService.getBatchSerialOutput(storageMethod,plantId,companyId,languageId,warehouseId,levelId,maintenance);
    	log.info("BatchSerial : " + batchserial);
		return new ResponseEntity<>(batchserial, HttpStatus.OK);
	}
    
    @ApiOperation(response = BatchSerial.class, value = "Search BatchSerial") // label for swagger
	@PostMapping("/findBatchSerial")
	public List<BatchSerial> findBatchSerial(@RequestBody SearchBatchSerial searchBatchSerial)
			throws Exception {
		return batchserialService.findBatchSerial(searchBatchSerial);
	}
    
    @ApiOperation(response = BatchSerial.class, value = "Create BatchSerial") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBatchSerial(@Valid @RequestBody List<AddBatchSerial> newBatchSerial, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		List<BatchSerial> createdBatchSerial = batchserialService.createBatchSerial(newBatchSerial, loginUserID);
		return new ResponseEntity<>(createdBatchSerial , HttpStatus.OK);
	}
    
    @ApiOperation(response = BatchSerial.class, value = "Update BatchSerial") // label for swagger
    @PatchMapping("/{storageMethod}")
	public ResponseEntity<?> patchBatchSerial(@PathVariable String storageMethod,@RequestParam String companyId,
											  @RequestParam String plantId,@RequestParam String languageId,
											  @RequestParam String warehouseId,@RequestParam Long levelId,
											  @Valid @RequestBody List<UpdateBatchSerial> updateBatchSerial,@RequestParam String maintenance,
											  @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<BatchSerial> createdBatchSerial = batchserialService.updateBatchSerial(storageMethod,companyId,plantId,
				languageId,maintenance,warehouseId,levelId,updateBatchSerial, loginUserID);
		return new ResponseEntity<>(createdBatchSerial, HttpStatus.OK);
	}
    
    @ApiOperation(response = BatchSerial.class, value = "Delete BatchSerial") // label for swagger
	@DeleteMapping("/{storageMethod}")
	public ResponseEntity<?> deleteBatchSerial(@PathVariable String storageMethod,@RequestParam String companyId,
											   @RequestParam String languageId,@RequestParam String plantId,
											   @RequestParam Long levelId,@RequestParam String warehouseId,@RequestParam String maintenance,
											   @RequestParam String loginUserID) throws ParseException {
    	batchserialService.deleteBatchSerial(storageMethod,companyId,languageId,plantId,warehouseId,levelId,maintenance,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}