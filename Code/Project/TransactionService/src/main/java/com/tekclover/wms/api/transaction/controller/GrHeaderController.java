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

import com.tekclover.wms.api.transaction.model.inbound.gr.AddGrHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.SearchGrHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.UpdateGrHeader;
import com.tekclover.wms.api.transaction.service.GrHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"GrHeader"}, value = "GrHeader  Operations related to GrHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "GrHeader ",description = "Operations related to GrHeader ")})
@RequestMapping("/grheader")
@RestController
public class GrHeaderController {
	
	@Autowired
	GrHeaderService grheaderService;
	
    @ApiOperation(response = GrHeader.class, value = "Get all GrHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<GrHeader> grheaderList = grheaderService.getGrHeaders();
		return new ResponseEntity<>(grheaderList, HttpStatus.OK);
	}
    
    @ApiOperation(response = GrHeader.class, value = "Get a GrHeader") // label for swagger 
	@GetMapping("/{goodsReceiptNo}")
	public ResponseEntity<?> getGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode) {
    	GrHeader grheader = grheaderService.getGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, 
    			palletCode, caseCode);
    	log.info("GrHeader : " + grheader);
		return new ResponseEntity<>(grheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = GrHeader.class, value = "Search GrHeader") // label for swagger
	@PostMapping("/findGrHeader")
	public List<GrHeader> findGrHeader(@RequestBody SearchGrHeader searchGrHeader)
			throws Exception {
		return grheaderService.findGrHeader(searchGrHeader);
	}
    
    @ApiOperation(response = GrHeader.class, value = "Create GrHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postGrHeader(@Valid @RequestBody AddGrHeader newGrHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		GrHeader createdGrHeader = grheaderService.createGrHeader(newGrHeader, loginUserID);
		return new ResponseEntity<>(createdGrHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = GrHeader.class, value = "Update GrHeader") // label for swagger
    @PatchMapping("/{goodsReceiptNo}")
	public ResponseEntity<?> patchGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode,
			@Valid @RequestBody UpdateGrHeader updateGrHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		GrHeader createdGrHeader = 
				grheaderService.updateGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID, updateGrHeader);
		return new ResponseEntity<>(createdGrHeader , HttpStatus.OK);
	}
    
//    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
//    @PatchMapping("/{refDocNumber}/reverse")
//	public ResponseEntity<?> patchPutAwayHeader(@PathVariable String refDocNumber, @RequestParam String packBarcodes, 
//			@RequestParam String warehouseId, @RequestParam String preInboundNo, @RequestParam String caseCode, 
//			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
//    	grheaderService.updatePutAwayHeader(refDocNumber, packBarcodes, warehouseId, preInboundNo, caseCode, loginUserID);
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
    
    @ApiOperation(response = GrHeader.class, value = "Delete GrHeader") // label for swagger
	@DeleteMapping("/{goodsReceiptNo}")
	public ResponseEntity<?> deleteGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID) {
    	grheaderService.deleteGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}