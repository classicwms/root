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

import com.tekclover.wms.api.transaction.model.inbound.putaway.AddPutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.SearchPutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.UpdatePutAwayLine;
import com.tekclover.wms.api.transaction.service.PutAwayLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PutAwayLine"}, value = "PutAwayLine  Operations related to PutAwayLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PutAwayLine ",description = "Operations related to PutAwayLine ")})
@RequestMapping("/putawayline")
@RestController
public class PutAwayLineController {
	
	@Autowired
	PutAwayLineService putawaylineService;
	
    @ApiOperation(response = PutAwayLine.class, value = "Get all PutAwayLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PutAwayLine> putawaylineList = putawaylineService.getPutAwayLines();
		return new ResponseEntity<>(putawaylineList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger 
	@GetMapping("/{confirmedStorageBin}")
	public ResponseEntity<?> getPutAwayLine(@PathVariable List<String> confirmedStorageBin, @RequestParam String warehouseId, 
			@RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber, 
			@RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode, 
			@RequestParam String proposedStorageBin) {
    	PutAwayLine putawayline = 
    			putawaylineService.getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, 
    					lineNo, itemCode, proposedStorageBin, confirmedStorageBin);
    	log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger 
	@GetMapping("/{lineNo}/inboundline")
	public ResponseEntity<?> getPutAwayLineForInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			 @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode) {
    	List<PutAwayLine> putawayline = 
    			putawaylineService.getPutAwayLine(warehouseId, preInboundNo, refDocNumber, lineNo, itemCode);
    	log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger 
	@GetMapping("/{refDocNumber}/inboundreversal/palletId")
	public ResponseEntity<?> getPutAwayLineForInboundLine(@PathVariable String refDocNumber) {
    	List<PutAwayLine> putawayline = putawaylineService.getPutAwayLine(refDocNumber);
    	log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayLine") // label for swagger
	@PostMapping("/findPutAwayLine")
	public List<PutAwayLine> findPutAwayLine(@RequestBody SearchPutAwayLine searchPutAwayLine)
			throws Exception {
		return putawaylineService.findPutAwayLine(searchPutAwayLine);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Create PutAwayLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPutAwayLine(@Valid @RequestBody AddPutAwayLine newPutAwayLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayLine createdPutAwayLine = putawaylineService.createPutAwayLine(newPutAwayLine, loginUserID);
		return new ResponseEntity<>(createdPutAwayLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Create PutAwayLine") // label for swagger
   	@PostMapping("/confirm")
   	public ResponseEntity<?> postPutAwayLineConfirm(@Valid @RequestBody List<AddPutAwayLine> newPutAwayLine, 
   			@RequestParam String loginUserID) 
   			throws IllegalAccessException, InvocationTargetException {
   		List<PutAwayLine> createdPutAwayLine = putawaylineService.putAwayLineConfirm(newPutAwayLine, loginUserID);
   		return new ResponseEntity<>(createdPutAwayLine , HttpStatus.OK);
   	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Update PutAwayLine") // label for swagger
    @PatchMapping("/{confirmedStorageBin}")
	public ResponseEntity<?> patchPutAwayLine(@PathVariable List<String> confirmedStorageBin, @RequestParam String warehouseId, 
			@RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber, 
			@RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode, 
			@RequestParam String proposedStorageBin, @Valid @RequestBody UpdatePutAwayLine updatePutAwayLine, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		PutAwayLine createdPutAwayLine = 
				putawaylineService.updatePutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, 
						itemCode, proposedStorageBin, confirmedStorageBin, loginUserID, updatePutAwayLine);
		return new ResponseEntity<>(createdPutAwayLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Delete PutAwayLine") // label for swagger
	@DeleteMapping("/{confirmedStorageBin}")
	public ResponseEntity<?> deletePutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String loginUserID) {
    	putawaylineService.deletePutAwayLine(languageId, companyCodeId, plantId, warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode, proposedStorageBin, confirmedStorageBin, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}