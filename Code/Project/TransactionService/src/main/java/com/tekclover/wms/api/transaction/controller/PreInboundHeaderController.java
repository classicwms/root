	package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

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

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.AddPreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.AddPreInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeaderEntity;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.SearchPreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.UpdatePreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.service.PreInboundHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PreInboundHeader"}, value = "PreInboundHeader  Operations related to PreInboundHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PreInboundHeader ",description = "Operations related to PreInboundHeader ")})
@RequestMapping("/preinboundheader")
@RestController
public class PreInboundHeaderController {
	
	@Autowired
	PreInboundHeaderService preinboundheaderService;
	
	/*----------------------PROCESS-INBOUND-RECEIVED----------------------------------------------*/
	/*
     * Process the Preinbound Integraion data
     */
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Process ASN") // label for swagger
   	@PostMapping("/{refDocNumber}/processInboundReceived")
   	public ResponseEntity<?> processInboundReceived(@PathVariable String refDocNumber, 
   			@RequestBody InboundIntegrationHeader inboundIntegrationHeader) 
   			throws BadRequestException, Exception {
   		preinboundheaderService.processInboundReceived(refDocNumber, inboundIntegrationHeader);
   		return new ResponseEntity<>(HttpStatus.OK);
   	}
	
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Get all PreInboundHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PreInboundHeader> preinboundheaderList = preinboundheaderService.getPreInboundHeaders();
		return new ResponseEntity<>(preinboundheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Get a PreInboundHeader") // label for swagger 
	@GetMapping("/{preInboundNo}")
	public ResponseEntity<?> getPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId) {
    	PreInboundHeader preinboundheader = preinboundheaderService.getPreInboundHeader(preInboundNo, warehouseId);
//    	log.info("PreInboundHeader : " + preinboundheader);
		return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
	}
    
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Get a PreInboundHeader") // label for swagger 
   	@GetMapping("/inboundconfirm")
   	public ResponseEntity<?> getPreInboundHeader(@RequestParam String warehouseId) {
       	List<PreInboundHeader> preinboundheader = preinboundheaderService.getPreInboundHeaderWithStatusId(warehouseId);
//       	log.info("PreInboundHeader : " + preinboundheader);
   		return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
   	}
    
	@ApiOperation(response = PreInboundHeaderEntity.class, value = "Search PreInboundHeader") // label for swagger
	@PostMapping("/findPreInboundHeader")
	public List<PreInboundHeaderEntity> findPreInboundHeader(@RequestBody SearchPreInboundHeader searchPreInboundHeader)
			throws Exception {
		return preinboundheaderService.findPreInboundHeader(searchPreInboundHeader);
	}

	//Streaming
	@ApiOperation(response = PreInboundHeaderEntity.class, value = "Search PreInboundHeader New") // label for swagger
	@PostMapping("/findPreInboundHeaderNew")
	public Stream<PreInboundHeaderEntity> findPreInboundHeaderNew(@RequestBody SearchPreInboundHeader searchPreInboundHeader)
			throws Exception {
		return preinboundheaderService.findPreInboundHeaderNew(searchPreInboundHeader);
	}

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Create PreInboundHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPreInboundHeader(@Valid @RequestBody AddPreInboundHeader newPreInboundHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreInboundHeaderEntity createdPreInboundHeader = 
				preinboundheaderService.createPreInboundHeader(newPreInboundHeader, loginUserID);
		return new ResponseEntity<>(createdPreInboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Update PreInboundHeader") // label for swagger
    @PatchMapping("/{preInboundNo}")
	public ResponseEntity<?> patchPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
			@Valid @RequestBody UpdatePreInboundHeader updatePreInboundHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreInboundHeader createdPreInboundHeader = 
				preinboundheaderService.updatePreInboundHeader(preInboundNo, warehouseId, updatePreInboundHeader, loginUserID);
		return new ResponseEntity<>(createdPreInboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Delete PreInboundHeader") // label for swagger
	@DeleteMapping("/{preInboundNo}")
	public ResponseEntity<?> deletePreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId, 
			@RequestParam String loginUserID) {
    	preinboundheaderService.deletePreInboundHeader(preInboundNo, warehouseId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    @ApiOperation(response = PreInboundHeader.class, value = "Process ASN") // label for swagger
   	@PostMapping("/processASN")
   	public ResponseEntity<?> processASN(@RequestBody List<AddPreInboundLine> newPreInboundLine, @RequestParam String loginUserID) 
   			throws IllegalAccessException, InvocationTargetException {
    	StagingHeader createdStagingHeader = preinboundheaderService.processASN(newPreInboundLine, loginUserID);
   		return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
   	}
}