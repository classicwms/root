package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.AddOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.service.OutboundReversalService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"OutboundReversal"}, value = "OutboundReversal  Operations related to OutboundReversalController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundReversal ",description = "Operations related to OutboundReversal ")})
@RequestMapping("/outboundreversal")
@RestController
public class OutboundReversalController {
	
	@Autowired
	OutboundReversalService outboundreversalService;
	
    @ApiOperation(response = OutboundReversal.class, value = "Get all OutboundReversal details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<OutboundReversal> outboundreversalList = outboundreversalService.getOutboundReversals();
		return new ResponseEntity<>(outboundreversalList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal") // label for swagger
	@PostMapping("/findOutboundReversal")
	public List<OutboundReversal> findOutboundReversal(@RequestBody SearchOutboundReversal searchOutboundReversal)
			throws Exception {
		return outboundreversalService.findOutboundReversal(searchOutboundReversal);
	}
    
    @ApiOperation(response = OutboundReversal.class, value = "Create OutboundReversal") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postOutboundReversal(@Valid @RequestBody AddOutboundReversal newOutboundReversal, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundReversal createdOutboundReversal = 
				outboundreversalService.createOutboundReversal(newOutboundReversal, loginUserID);
		return new ResponseEntity<>(createdOutboundReversal , HttpStatus.OK);
	}
}