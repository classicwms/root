package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.decimalnotationid.AddDecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.DecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.UpdateDecimalNotationId;
import com.tekclover.wms.api.idmaster.service.DecimalNotationIdService;
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
@Api(tags = {"DecimalNotationId"}, value = "DecimalNotationId  Operations related to DecimalNotationIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DecimalNotationId ",description = "Operations related to DecimalNotationId ")})
@RequestMapping("/decimalnotationid")
@RestController
public class DecimalNotationIdController {
	
	@Autowired
	DecimalNotationIdService decimalnotationidService;
	
    @ApiOperation(response = DecimalNotationId.class, value = "Get all DecimalNotationId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<DecimalNotationId> decimalnotationidList = decimalnotationidService.getDecimalNotationIds();
		return new ResponseEntity<>(decimalnotationidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = DecimalNotationId.class, value = "Get a DecimalNotationId") // label for swagger 
	@GetMapping("/{decimalNotationId}")
	public ResponseEntity<?> getDecimalNotationId(@PathVariable String decimalNotationId,
			@RequestParam String warehouseId) {
    	DecimalNotationId decimalnotationid = 
    			decimalnotationidService.getDecimalNotationId(warehouseId, decimalNotationId);
    	log.info("DecimalNotationId : " + decimalnotationid);
		return new ResponseEntity<>(decimalnotationid, HttpStatus.OK);
	}
    
    @ApiOperation(response = DecimalNotationId.class, value = "Create DecimalNotationId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDecimalNotationId(@Valid @RequestBody AddDecimalNotationId newDecimalNotationId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		DecimalNotationId createdDecimalNotationId = decimalnotationidService.createDecimalNotationId(newDecimalNotationId, loginUserID);
		return new ResponseEntity<>(createdDecimalNotationId , HttpStatus.OK);
	}
    
    @ApiOperation(response = DecimalNotationId.class, value = "Update DecimalNotationId") // label for swagger
    @PatchMapping("/{decimalNotationId}")
	public ResponseEntity<?> patchDecimalNotationId(@PathVariable String decimalNotationId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateDecimalNotationId updateDecimalNotationId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		DecimalNotationId createdDecimalNotationId = 
				decimalnotationidService.updateDecimalNotationId(warehouseId, decimalNotationId, loginUserID, updateDecimalNotationId);
		return new ResponseEntity<>(createdDecimalNotationId , HttpStatus.OK);
	}
    
    @ApiOperation(response = DecimalNotationId.class, value = "Delete DecimalNotationId") // label for swagger
	@DeleteMapping("/{decimalNotationId}")
	public ResponseEntity<?> deleteDecimalNotationId(@PathVariable String decimalNotationId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	decimalnotationidService.deleteDecimalNotationId(warehouseId, decimalNotationId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}