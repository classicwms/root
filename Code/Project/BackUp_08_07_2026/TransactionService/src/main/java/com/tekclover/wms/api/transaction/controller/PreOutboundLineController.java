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

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.AddPreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundLine;
import com.tekclover.wms.api.transaction.service.PreOutboundLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PreOutboundLine"}, value = "PreOutboundLine  Operations related to PreOutboundLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PreOutboundLine ",description = "Operations related to PreOutboundLine ")})
@RequestMapping("/preoutboundline")
@RestController
public class PreOutboundLineController {
	
	@Autowired
	PreOutboundLineService preoutboundlineService;
	
    @ApiOperation(response = PreOutboundLine.class, value = "Get all PreOutboundLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PreOutboundLine> preoutboundlineList = preoutboundlineService.getPreOutboundLines();
		return new ResponseEntity<>(preoutboundlineList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = PreOutboundLine.class, value = "Search PreOutboundLine") // label for swagger
	@PostMapping("/findPreOutboundLine")
	public List<PreOutboundLine> findPreOutboundLine(@RequestBody SearchPreOutboundLine searchPreOutboundLine)
			throws Exception {
		return preoutboundlineService.findPreOutboundLine(searchPreOutboundLine);
	}
    
    @ApiOperation(response = PreOutboundLine.class, value = "Create PreOutboundLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPreOutboundLine(@Valid @RequestBody AddPreOutboundLine newPreOutboundLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreOutboundLine createdPreOutboundLine = preoutboundlineService.createPreOutboundLine(newPreOutboundLine, loginUserID);
		return new ResponseEntity<>(createdPreOutboundLine , HttpStatus.OK);
	}
    
//    @ApiOperation(response = PreOutboundLine.class, value = "Update PreOutboundLine") // label for swagger
//    @PatchMapping("/{lineNumber}")
//	public ResponseEntity<?> patchPreOutboundLine(@PathVariable String lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam String itemCode,
//			@Valid @RequestBody UpdatePreOutboundLine updatePreOutboundLine, @RequestParam String loginUserID) 
//			throws IllegalAccessException, InvocationTargetException {
//		PreOutboundLine createdPreOutboundLine = 
//				preoutboundlineService.updatePreOutboundLine(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode, loginUserID);
//		return new ResponseEntity<>(createdPreOutboundLine , HttpStatus.OK);
//	}
//    
//    @ApiOperation(response = PreOutboundLine.class, value = "Delete PreOutboundLine") // label for swagger
//	@DeleteMapping("/{lineNumber}")
//	public ResponseEntity<?> deletePreOutboundLine(@PathVariable String lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID) {
//    	preoutboundlineService.deletePreOutboundLine(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode, loginUserID);
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//	}
}