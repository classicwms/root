package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.outboundorderstatusid.*;
import com.tekclover.wms.api.idmaster.service.OutboundOrderStatusIdService;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"OutboundOrderStatusId"}, value = "OutboundOrderStatusId  Operations related to OutboundOrderStatusIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundOrderStatusId ",description = "Operations related to OutboundOrderStatusId ")})
@RequestMapping("/outboundorderstatusid")
@RestController
public class OutboundOrderStatusIdController {
	
	@Autowired
	OutboundOrderStatusIdService outboundorderstatusidService;
	
    @ApiOperation(response = OutboundOrderStatusId.class, value = "Get all OutboundOrderStatusId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<OutboundOrderStatusId> outboundorderstatusidList = outboundorderstatusidService.getOutboundOrderStatusIds();
		return new ResponseEntity<>(outboundorderstatusidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = OutboundOrderStatusId.class, value = "Get a OutboundOrderStatusId") // label for swagger 
	@GetMapping("/{outboundOrderStatusId}")
	public ResponseEntity<?> getOutboundOrderStatusId(@RequestParam String warehouseId,@PathVariable String outboundOrderStatusId,@RequestParam String companyCodeId,
													  @RequestParam String languageId,@RequestParam String plantId) {
    	OutboundOrderStatusId outboundorderstatusid = 
    			outboundorderstatusidService.getOutboundOrderStatusId(warehouseId,outboundOrderStatusId,companyCodeId,languageId,plantId);
    	log.info("OutboundOrderStatusId : " + outboundorderstatusid);
		return new ResponseEntity<>(outboundorderstatusid, HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundOrderStatusId.class, value = "Create OutboundOrderStatusId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postOutboundOrderStatusId(@Valid @RequestBody AddOutboundOrderStatusId newOutboundOrderStatusId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		OutboundOrderStatusId createdOutboundOrderStatusId = outboundorderstatusidService.createOutboundOrderStatusId(newOutboundOrderStatusId, loginUserID);
		return new ResponseEntity<>(createdOutboundOrderStatusId , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundOrderStatusId.class, value = "Update OutboundOrderStatusId") // label for swagger
    @PatchMapping("/{outboundOrderStatusId}")
	public ResponseEntity<?> patchOutboundOrderStatusId(@RequestParam String warehouseId,@PathVariable String outboundOrderStatusId,@RequestParam String companyCodeId,@RequestParam String languageId,
														@RequestParam String plantId, @RequestParam String loginUserID,
			@Valid @RequestBody UpdateOutboundOrderStatusId updateOutboundOrderStatusId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		OutboundOrderStatusId createdOutboundOrderStatusId = 
				outboundorderstatusidService.updateOutboundOrderStatusId(warehouseId, outboundOrderStatusId,companyCodeId,languageId,plantId,loginUserID,updateOutboundOrderStatusId);
		return new ResponseEntity<>(createdOutboundOrderStatusId , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundOrderStatusId.class, value = "Delete OutboundOrderStatusId") // label for swagger
	@DeleteMapping("/{outboundOrderStatusId}")
	public ResponseEntity<?> deleteOutboundOrderStatusId(@RequestParam String warehouseId,@PathVariable String outboundOrderStatusId,@RequestParam String companyCodeId,
														 @RequestParam String languageId, @RequestParam String plantId,
			 @RequestParam String loginUserID) {
    	outboundorderstatusidService.deleteOutboundOrderStatusId(warehouseId, outboundOrderStatusId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = OutboundOrderStatusId.class, value = "Find OutboundOrderStatusId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findOutboundOrderStatusId(@Valid @RequestBody FindOutboundOrderStatusId findOutboundOrderStatusId) throws Exception {
		List<OutboundOrderStatusId> createdOutboundOrderStatusId = outboundorderstatusidService.findOutboundOrderStatusId(findOutboundOrderStatusId);
		return new ResponseEntity<>(createdOutboundOrderStatusId, HttpStatus.OK);
	}
}