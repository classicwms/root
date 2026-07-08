package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.mnc.AddInhouseTransferHeader;
import com.tekclover.wms.api.transaction.model.mnc.InhouseTransferHeader;
import com.tekclover.wms.api.transaction.model.mnc.InhouseTransferHeaderEntity;
import com.tekclover.wms.api.transaction.model.mnc.SearchInhouseTransferHeader;
import com.tekclover.wms.api.transaction.service.InhouseTransferHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"InHouseTransferHeader"}, value = "InHouseTransferHeader  Operations related to InHouseTransferHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InHouseTransferHeader ",description = "Operations related to InHouseTransferHeader ")})
@RequestMapping("/inhousetransferheader")
@RestController
public class InhouseTransferHeaderController {
	
	@Autowired
	InhouseTransferHeaderService inHouseTransferHeaderService;
	
    @ApiOperation(response = InhouseTransferHeader.class, value = "Get all InHouseTransferHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<InhouseTransferHeader> inhousetransferheaderList = 
				inHouseTransferHeaderService.getInHouseTransferHeaders();
		return new ResponseEntity<>(inhousetransferheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = InhouseTransferHeader.class, value = "Get a InHouseTransferHeader") // label for swagger 
	@GetMapping("/{transferNumber}")
	public ResponseEntity<?> getInHouseTransferHeader(@PathVariable String transferNumber, @RequestParam String warehouseId, @RequestParam Long transferTypeId) {
    	InhouseTransferHeader inhousetransferheader = inHouseTransferHeaderService.getInHouseTransferHeader(warehouseId, transferNumber, transferTypeId);
		return new ResponseEntity<>(inhousetransferheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader") // label for swagger
	@PostMapping("/findInHouseTransferHeader")
	public ResponseEntity<?> findInHouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader)
			throws Exception {
		log.info("called...");
		List<InhouseTransferHeader> results = inHouseTransferHeaderService.findInHouseTransferHeader(searchInHouseTransferHeader);
		return new ResponseEntity<>(results, HttpStatus.OK);
	}

	//Stream
	@ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader New") // label for swagger
	@PostMapping("/findInHouseTransferHeaderNew")
	public ResponseEntity<?> findInHouseTransferHeaderNew(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader)
			throws Exception {
		Stream<InhouseTransferHeader> results = inHouseTransferHeaderService.findInHouseTransferHeaderNew(searchInHouseTransferHeader);
		return new ResponseEntity<>(results, HttpStatus.OK);
	}
    
    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postInHouseTransferHeader(@Valid @RequestBody AddInhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		InhouseTransferHeaderEntity createdInHouseTransferHeader = 
				inHouseTransferHeaderService.createInHouseTransferHeader(newInHouseTransferHeader, loginUserID);
		return new ResponseEntity<>(createdInHouseTransferHeader , HttpStatus.OK);
	}
}