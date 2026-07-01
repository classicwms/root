package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.UpdatePreOutboundHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.AddPreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundHeader;
import com.tekclover.wms.api.transaction.service.PreOutboundHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PreOutboundHeader"}, value = "PreOutboundHeader  Operations related to PreOutboundHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PreOutboundHeader ",description = "Operations related to PreOutboundHeader ")})
@RequestMapping("/preoutboundheader")
@RestController
public class PreOutboundHeaderController {
	
	@Autowired
	PreOutboundHeaderService preoutboundheaderService;
	
    @ApiOperation(response = PreOutboundHeader.class, value = "Get all PreOutboundHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PreOutboundHeader> preoutboundheaderList = preoutboundheaderService.getPreOutboundHeaders();
		return new ResponseEntity<>(preoutboundheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PreOutboundHeader.class, value = "Get a PreOutboundHeader") // label for swagger 
	@GetMapping("/{preOutboundNo}")
	public ResponseEntity<?> getPreOutboundHeader(@PathVariable String preOutboundNo, 
			@RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode) {
    	PreOutboundHeader preoutboundheader = 
    			preoutboundheaderService.getPreOutboundHeader(warehouseId, refDocNumber, preOutboundNo, partnerCode);
    	log.info("PreOutboundHeader : " + preoutboundheader);
		return new ResponseEntity<>(preoutboundheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader") // label for swagger
	@PostMapping("/findPreOutboundHeader")
	public List<PreOutboundHeader> findPreOutboundHeader(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader)
			throws Exception {
		return preoutboundheaderService.findPreOutboundHeader(searchPreOutboundHeader);
	}
	//Stream - JPA
	@ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader New") // label for swagger
	@PostMapping("/findPreOutboundHeaderNew")
	public Stream<PreOutboundHeader> findPreOutboundHeaderNew(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader)
			throws Exception {
		return preoutboundheaderService.findPreOutboundHeaderNew(searchPreOutboundHeader);
	}
    @ApiOperation(response = PreOutboundHeader.class, value = "Create PreOutboundHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPreOutboundHeader(@Valid @RequestBody AddPreOutboundHeader newPreOutboundHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreOutboundHeader createdPreOutboundHeader = preoutboundheaderService.createPreOutboundHeader(newPreOutboundHeader, loginUserID);
		return new ResponseEntity<>(createdPreOutboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PreOutboundHeader.class, value = "Update PreOutboundHeader") // label for swagger
    @PatchMapping("/{preOutboundNo}")
	public ResponseEntity<?> patchPreOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
													@Valid @RequestBody UpdatePreOutboundHeader updatePreOutboundHeader, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PreOutboundHeader createdPreOutboundHeader =
				preoutboundheaderService.updatePreOutboundHeader(warehouseId, refDocNumber, preOutboundNo, partnerCode, loginUserID, updatePreOutboundHeader);
		return new ResponseEntity<>(createdPreOutboundHeader , HttpStatus.OK);
	}

    @ApiOperation(response = PreOutboundHeader.class, value = "Delete PreOutboundHeader") // label for swagger
	@DeleteMapping("/{preOutboundNo}")
	public ResponseEntity<?> deletePreOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String warehouseId, @RequestParam String refDocNumber,
													 @RequestParam String partnerCode, @RequestParam String loginUserID) {
    	preoutboundheaderService.deletePreOutboundHeader(warehouseId, refDocNumber, preOutboundNo, partnerCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}