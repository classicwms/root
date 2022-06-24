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

import com.tekclover.wms.api.transaction.model.outbound.AddOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLineReport;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.service.OutboundLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"OutboundLine"}, value = "OutboundLine  Operations related to OutboundLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundLine ",description = "Operations related to OutboundLine ")})
@RequestMapping("/outboundline")
@RestController
public class OutboundLineController {
	
	@Autowired
	OutboundLineService outboundlineService;
	
    @ApiOperation(response = OutboundLine.class, value = "Get all OutboundLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<OutboundLine> outboundlineList = outboundlineService.getOutboundLines();
		return new ResponseEntity<>(outboundlineList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Get a OutboundLine") // label for swagger 
	@GetMapping("/delivery/line")
	public ResponseEntity<?> getOutboundLine(@RequestParam String warehouseId, 
			@RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode) {
    	List<OutboundLine> outboundline = 
    			outboundlineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode);
    	log.info("OutboundLine : " + outboundline);
		return new ResponseEntity<>(outboundline, HttpStatus.OK);
	}
    
	@ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
	@PostMapping("/findOutboundLine")
	public List<OutboundLine> findOutboundLine(@RequestBody SearchOutboundLine searchOutboundLine)
			throws Exception {
		return outboundlineService.findOutboundLine(searchOutboundLine);
	}
	
	@ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
	@PostMapping("/findOutboundLineReport")
	public List<OutboundLine> findOutboundLineForReport(@RequestBody SearchOutboundLineReport searchOutboundLineReport)
			throws Exception {
		return outboundlineService.findOutboundLineReport (searchOutboundLineReport);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Create OutboundLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postOutboundLine(@Valid @RequestBody AddOutboundLine newOutboundLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundLine createdOutboundLine = outboundlineService.createOutboundLine(newOutboundLine, loginUserID);
		return new ResponseEntity<>(createdOutboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @GetMapping("/delivery/confirmation")
	public ResponseEntity<?> deliveryConfirmation (@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<OutboundLine> createdOutboundLine = 
				outboundlineService.deliveryConfirmation(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
		return new ResponseEntity<>(createdOutboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @PatchMapping("/{lineNumber}")
	public ResponseEntity<?> patchOutboundLine(@PathVariable Long lineNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
			@Valid @RequestBody UpdateOutboundLine updateOutboundLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundLine updatedOutboundLine = 
				outboundlineService.updateOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
						lineNumber, itemCode, loginUserID, updateOutboundLine);
		return new ResponseEntity<>(updatedOutboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Delete OutboundLine") // label for swagger
	@DeleteMapping("/{lineNumber}")
	public ResponseEntity<?> deleteOutboundLine(@PathVariable Long lineNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, 
			@RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID) {
    	outboundlineService.deleteOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
    			itemCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLine.class, value = "Get Delivery Lines") // label for swagger 
   	@GetMapping("/reversal/new")
   	public ResponseEntity<?> doReversal(@RequestParam String refDocNumber, @RequestParam String itemCode,
   			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
       	List<OutboundReversal> deliveryLines = outboundlineService.doReversal(refDocNumber, itemCode, loginUserID);
       	log.info("deliveryLines : " + deliveryLines);
   		return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
   	}
}