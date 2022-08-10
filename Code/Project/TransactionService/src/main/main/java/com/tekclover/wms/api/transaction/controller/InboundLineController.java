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

import com.tekclover.wms.api.transaction.model.inbound.AddInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundLine;
import com.tekclover.wms.api.transaction.service.InboundLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"InboundLine"}, value = "InboundLine  Operations related to InboundLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InboundLine ",description = "Operations related to InboundLine ")})
@RequestMapping("/inboundline")
@RestController
public class InboundLineController {
	
	@Autowired
	InboundLineService inboundlineService;
	
    @ApiOperation(response = InboundLine.class, value = "Get all InboundLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<InboundLine> inboundlineList = inboundlineService.getInboundLines();
		return new ResponseEntity<>(inboundlineList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = InboundLine.class, value = "Get a InboundLine") // label for swagger 
	@GetMapping("/{lineNo}")
	public ResponseEntity<?> getInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode) {
    	InboundLine inboundline = inboundlineService.getInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
    	log.info("InboundLine : " + inboundline);
		return new ResponseEntity<>(inboundline, HttpStatus.OK);
	}
    
    @ApiOperation(response = InboundLine.class, value = "Create InboundLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postInboundLine(@Valid @RequestBody AddInboundLine newInboundLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		InboundLine createdInboundLine = inboundlineService.createInboundLine(newInboundLine, loginUserID);
		return new ResponseEntity<>(createdInboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = InboundLine.class, value = "Create InboundLine") // label for swagger
	@PostMapping("/confirm")
	public ResponseEntity<?> postInboundLine(@Valid @RequestBody List<AddInboundLine> newInboundLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		InboundLine createdInboundLine = inboundlineService.confirmInboundLine(newInboundLine, loginUserID);
		return new ResponseEntity<>(createdInboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = InboundLine.class, value = "Update InboundLine") // label for swagger
    @PatchMapping("/{lineNo}")
	public ResponseEntity<?> patchInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode,
			@Valid @RequestBody UpdateInboundLine updateInboundLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		InboundLine createdInboundLine = 
				inboundlineService.updateInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine);
		return new ResponseEntity<>(createdInboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = InboundLine.class, value = "Delete InboundLine") // label for swagger
	@DeleteMapping("/{lineNo}")
	public ResponseEntity<?> deleteInboundLine(@PathVariable Long lineNo, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID) {
    	inboundlineService.deleteInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}