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

import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.transaction.service.QualityHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"QualityHeader"}, value = "QualityHeader  Operations related to QualityHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "QualityHeader ",description = "Operations related to QualityHeader ")})
@RequestMapping("/qualityheader")
@RestController
public class QualityHeaderController {
	
	@Autowired
	QualityHeaderService qualityheaderService;
	
    @ApiOperation(response = QualityHeader.class, value = "Get all QualityHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<QualityHeader> qualityheaderList = qualityheaderService.getQualityHeaders();
		return new ResponseEntity<>(qualityheaderList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = QualityHeader.class, value = "Search QualityHeader") // label for swagger
	@PostMapping("/findQualityHeader")
	public List<QualityHeader> findQualityHeader(@RequestBody SearchQualityHeader searchQualityHeader)
			throws Exception {
		return qualityheaderService.findQualityHeader(searchQualityHeader);
	}
    
    @ApiOperation(response = QualityHeader.class, value = "Create QualityHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postQualityHeader(@Valid @RequestBody AddQualityHeader newQualityHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		QualityHeader createdQualityHeader = qualityheaderService.createQualityHeader(newQualityHeader, loginUserID);
		return new ResponseEntity<>(createdQualityHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = QualityHeader.class, value = "Update QualityHeader") // label for swagger
    @PatchMapping("/{qualityInspectionNo}")
	public ResponseEntity<?> patchQualityHeader(@PathVariable String qualityInspectionNo, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam String pickupNumber, @RequestParam String actualHeNo,
			@Valid @RequestBody UpdateQualityHeader updateQualityHeader, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		QualityHeader updatedQualityHeader = 
				qualityheaderService.updateQualityHeader(warehouseId, preOutboundNo, refDocNumber, 
						partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, updateQualityHeader);
		return new ResponseEntity<>(updatedQualityHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = QualityHeader.class, value = "Delete QualityHeader") // label for swagger
	@DeleteMapping("/{qualityInspectionNo}")
	public ResponseEntity<?> deleteQualityHeader(@PathVariable String qualityInspectionNo, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam String pickupNumber, @RequestParam String actualHeNo, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
    	qualityheaderService.deleteQualityHeader(warehouseId, preOutboundNo, refDocNumber, 
				partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}