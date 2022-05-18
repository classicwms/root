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

import com.tekclover.wms.api.transaction.model.outbound.AddOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundHeader;
import com.tekclover.wms.api.transaction.service.OutboundHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"OutboundHeader"}, value = "OutboundHeader  Operations related to OutboundHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundHeader ",description = "Operations related to OutboundHeader ")})
@RequestMapping("/outboundheader")
@RestController
public class OutboundHeaderController {
	
	@Autowired
	OutboundHeaderService outboundheaderService;
	
    @ApiOperation(response = OutboundHeader.class, value = "Get all OutboundHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<OutboundHeader> outboundheaderList = outboundheaderService.getOutboundHeaders();
		return new ResponseEntity<>(outboundheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = OutboundHeader.class, value = "Get a OutboundHeader") // label for swagger 
	@GetMapping("/{preOutboundNo}")
	public ResponseEntity<?> getOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String warehouseId, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode) {
    	OutboundHeader outboundheader = 
    			outboundheaderService.getOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
    	log.info("OutboundHeader : " + outboundheader);
		return new ResponseEntity<>(outboundheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader") // label for swagger
	@PostMapping("/findOutboundHeader")
	public List<OutboundHeader> findOutboundHeader(@RequestBody SearchOutboundHeader searchOutboundHeader)
			throws Exception {
		return outboundheaderService.findOutboundHeader(searchOutboundHeader);
	}
    
    @ApiOperation(response = OutboundHeader.class, value = "Create OutboundHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postOutboundHeader(@Valid @RequestBody AddOutboundHeader newOutboundHeader, 
			@RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundHeader createdOutboundHeader = outboundheaderService.createOutboundHeader(newOutboundHeader, loginUserID);
		return new ResponseEntity<>(createdOutboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundHeader.class, value = "Update OutboundHeader") // label for swagger
    @PatchMapping("/{preOutboundNo}")
	public ResponseEntity<?> patchOutboundHeader(@PathVariable String preOutboundNo, 
			@RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
			@Valid @RequestBody UpdateOutboundHeader updateOutboundHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundHeader createdOutboundHeader = 
				outboundheaderService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
						updateOutboundHeader, loginUserID);
		return new ResponseEntity<>(createdOutboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundHeader.class, value = "Delete OutboundHeader") // label for swagger
	@DeleteMapping("/{preOutboundNo}")
	public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo, 
			@RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam String loginUserID) {
    	outboundheaderService.deleteOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}