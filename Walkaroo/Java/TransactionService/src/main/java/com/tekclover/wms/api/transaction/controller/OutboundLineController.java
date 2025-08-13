package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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

import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.InboundReversalInput;
import com.tekclover.wms.api.transaction.model.outbound.AddOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLineReport;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.v2.OutboundReversalV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.DCReversalRequest;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineOutput;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.SearchOutboundLineV2;
import com.tekclover.wms.api.transaction.model.report.StockMovementReport;
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
	@PostMapping("/findOutboundLine-new")
	public List<OutboundLine> findOutboundLineNew(@RequestBody SearchOutboundLine searchOutboundLine)
			throws Exception {
		return outboundlineService.findOutboundLineNew(searchOutboundLine);
	}

	@ApiOperation(response = OutboundLine.class, value = "Search OutboundLine for Stock movement report") // label for swagger
	@PostMapping("/stock-movement-report/findOutboundLine")
	public List<StockMovementReport> findLinesForStockMovement(@RequestBody SearchOutboundLine searchOutboundLine)
			throws Exception {
		return outboundlineService.findLinesForStockMovement(searchOutboundLine);
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

	//=======================================================V2============================================================
   @ApiOperation(response = OutboundLineV2.class, value = "Get all OutboundLine details") // label for swagger
	@GetMapping("/v2")
	public ResponseEntity<?> getAllOutboundLineV2() {
		List<OutboundLineV2> outboundlineList = outboundlineService.getOutboundLinesV2();
		return new ResponseEntity<>(outboundlineList, HttpStatus.OK);
	}

    @ApiOperation(response = OutboundLineV2.class, value = "Get a OutboundLine") // label for swagger
	@GetMapping("/v2/delivery/line")
	public ResponseEntity<?> getOutboundLineV2(@RequestParam String warehouseId, @RequestParam String companyCodeId,
											 @RequestParam String plantId, @RequestParam String languageId,
											 @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode) {
    	List<OutboundLineV2> outboundline =
    			outboundlineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
    	log.info("OutboundLine : " + outboundline);
		return new ResponseEntity<>(outboundline, HttpStatus.OK);
	}

	@ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine") // label for swagger
	@PostMapping("/v2/findOutboundLine")
	public List<OutboundLineV2> findOutboundLineV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
			throws Exception {
		return outboundlineService.findOutboundLineV2(searchOutboundLine);
	}

	@ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine") // label for swagger
	@PostMapping("/v2/findOutboundLineStream")
	public Stream<OutboundLineV2> findOutboundLineNewStreamV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
			throws Exception {
		return outboundlineService.findOutboundLineNewStreamV2(searchOutboundLine);
	}

//	@ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLines v2") // label for swagger
//	@PatchMapping("/v2/lineNumbers")
//	public ResponseEntity<?> patchOutboundLines(@Valid @RequestBody List<OutboundLineV2> updateOutboundLine, @RequestParam String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		List<OutboundLineV2> outboundLines = outboundlineService.updateOutboundLinesV2(loginUserID, updateOutboundLine);
//		return new ResponseEntity<>(outboundLines, HttpStatus.OK);
//	}

	@ApiOperation(response = OutboundLineOutput.class, value = "Search OutboundLine") // label for swagger
	@PostMapping("/v2/findOutboundLineNew")
	public List<OutboundLineOutput> findOutboundLineNewV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
			throws Exception {
		return outboundlineService.findOutboundLineNewV2(searchOutboundLine);
	}


    @ApiOperation(response = OutboundLineV2.class, value = "Create OutboundLine") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postOutboundLineV2(@Valid @RequestBody OutboundLineV2 newOutboundLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		OutboundLineV2 createdOutboundLine = outboundlineService.createOutboundLineV2(newOutboundLine, loginUserID);
		return new ResponseEntity<>(createdOutboundLine , HttpStatus.OK);
	}

    @ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLine") // label for swagger
    @GetMapping("/v2/delivery/confirmation")
	public ResponseEntity<?> deliveryConfirmationV2(@RequestParam String warehouseId, @RequestParam String companyCodeId,
													@RequestParam String plantId, @RequestParam String languageId,
													@RequestParam String preOutboundNo, @RequestParam String refDocNumber,
													@RequestParam String partnerCode, @RequestParam String loginUserID)
			throws Exception {
//		List<OutboundLineV2> createdOutboundLine = outboundlineService.deliveryConfirmationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
		outboundlineService.partialDeliveryConfirmationV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, loginUserID);
		return new ResponseEntity<>(HttpStatus.OK);
	}

    @ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLine") // label for swagger
    @PatchMapping("/v2/{lineNumber}")
	public ResponseEntity<?> patchOutboundLineV2(@PathVariable Long lineNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
											   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
											   @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
			@Valid @RequestBody OutboundLineV2 updateOutboundLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		OutboundLineV2 updatedOutboundLine =
				outboundlineService.updateOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
						lineNumber, itemCode, loginUserID, updateOutboundLine);
		return new ResponseEntity<>(updatedOutboundLine , HttpStatus.OK);
	}

    @ApiOperation(response = OutboundLineV2.class, value = "Delete OutboundLine") // label for swagger
	@DeleteMapping("/v2/{lineNumber}")
	public ResponseEntity<?> deleteOutboundLine(@PathVariable Long lineNumber, @RequestParam String companyCodeId,
												@RequestParam String plantId, @RequestParam String languageId,
												@RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
			@RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID) throws ParseException {
    	outboundlineService.deleteOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
    			itemCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(response = OutboundLine.class, value = "Search OutboundLine for Stock movement report") // label for swagger
	@PostMapping("/stock-movement-report/v2/findOutboundLine")
	public List<StockMovementReport> findLinesForStockMovementV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
			throws Exception {
//		return outboundlineService.findLinesForStockMovementV2(searchOutboundLine);
		return outboundlineService.findLinesForStockMovementV3(searchOutboundLine);
	}

    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLineV2.class, value = "Get Delivery Lines") // label for swagger
   	@GetMapping("/v2/reversal/new")
   	public ResponseEntity<?> doReversalV2(@RequestParam String refDocNumber, @RequestParam String itemCode, 
   			@RequestParam String manufacturerName,
   			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
       	List<OutboundReversalV2> deliveryLines = outboundlineService.doReversalV2(refDocNumber, itemCode, manufacturerName, loginUserID);
       	log.info("deliveryLines : " + deliveryLines);
   		return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
   	}

	@ApiOperation(response = OutboundLineV2.class, value = "Reversal Batch") // label for swagger
	@PostMapping("/v2/reversal/batch")
	public ResponseEntity<?> doReversalBatchV2(@RequestBody List<InboundReversalInput> outboundReversalInput,
											   @RequestParam String loginUserID) throws Exception {
//		List<OutboundReversalV2> deliveryLines = outboundlineService.batchOutboundReversal(outboundReversalInput, loginUserID);
		outboundlineService.batchOutboundReversalV3(outboundReversalInput, loginUserID);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//=================================PGIReversal===================================================================
	@ApiOperation(response = OutboundLineV2.class, value = "PGIReversal") // label for swagger
	@PostMapping("/v2/reversal/dc")
	public ResponseEntity<?> doDeliveryConfirmationReversalV3(@RequestBody List<DCReversalRequest> dcReversalRequest) throws Exception {
		List<DCReversalRequest> response = outboundlineService.doPGIReversalV3(dcReversalRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}