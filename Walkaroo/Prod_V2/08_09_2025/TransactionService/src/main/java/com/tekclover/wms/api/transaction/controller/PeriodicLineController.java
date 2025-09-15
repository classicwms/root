package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicUpdateResponseV2;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicZeroStockLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.SearchPeriodicLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.service.PeriodicZeroStkLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicUpdateResponse;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.SearchPeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.UpdatePeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AssignHHTUserCC;
import com.tekclover.wms.api.transaction.service.PeriodicLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PeriodicLine"}, value = "PeriodicLine  Operations related to PeriodicLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PeriodicLine ",description = "Operations related to PeriodicLine ")})
@RequestMapping("/periodicline")
@RestController
public class PeriodicLineController {
	
	@Autowired
	PeriodicLineService periodicLineService;

	@Autowired
	PeriodicZeroStkLineService periodicZeroStkLineService;
	
	@ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine") // label for swagger
	@PostMapping("/findPeriodicLine")
	public List<PeriodicLine> findPeriodicLine (@RequestBody SearchPeriodicLine searchPeriodicLine)
			throws Exception {
		return periodicLineService.findPeriodicLine (searchPeriodicLine);
	}

	//Stream
	@ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine Stream") // label for swagger
	@PostMapping("/findPeriodicLineStream")
	public Stream<PeriodicLine> findPeriodicLineStream (@RequestBody SearchPeriodicLine searchPeriodicLine)
			throws Exception {
		return periodicLineService.findPeriodicLineStream (searchPeriodicLine);
	}
	
	@ApiOperation(response = PeriodicLine[].class, value = "AssignHHTUser") // label for swagger
    @PatchMapping("/assigingHHTUser")
	public ResponseEntity<?> patchAssingHHTUser (@RequestBody List<AssignHHTUserCC> assignHHTUser, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PeriodicLine> updatedPeriodicLine = periodicLineService.updateAssingHHTUser (assignHHTUser, loginUserID);
		return new ResponseEntity<>(updatedPeriodicLine , HttpStatus.OK);
	}
	
	@ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine") // label for swagger
    @PatchMapping("/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLine (@PathVariable String cycleCountNo, 
			@RequestBody List<UpdatePeriodicLine> updatePeriodicLine, @RequestParam String loginUserID) 
					throws IllegalAccessException, InvocationTargetException {
		PeriodicUpdateResponse createdPeriodicLine = 
				periodicLineService.updatePeriodicLine (cycleCountNo, updatePeriodicLine, loginUserID);
		return new ResponseEntity<>(createdPeriodicLine , HttpStatus.OK);
	}


	//=========================================================V2===============================================

	@ApiOperation(response = PeriodicLineV2.class, value = "SearchPeriodicLineV2") // label for swagger
	@PostMapping("/v2/findPeriodicLine")
	public List<PeriodicLineV2> findPeriodicLineV2(@RequestBody SearchPeriodicLineV2 searchPeriodicLineV2)
			throws Exception {
		return periodicLineService.findPeriodicLineStreamV2(searchPeriodicLineV2);
	}

	@ApiOperation(response = PeriodicLineV2[].class, value = "AssignHHTUser") // label for swagger
	@PatchMapping("/v2/assigingHHTUser")
	public ResponseEntity<?> patchAssingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
												  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PeriodicLineV2> createdPeriodicLine = periodicLineService.updateAssingHHTUserV2(assignHHTUser, loginUserID);
		return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
	}

	@ApiOperation(response = PeriodicLineV2.class, value = "Update PeriodicLineV2") // label for swagger
	@PatchMapping("/v2/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLineV2(@PathVariable String cycleCountNo,
												  @RequestBody List<PeriodicLineV2> updatePeriodicLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		PeriodicUpdateResponseV2 createdPeriodicResponse =
				periodicLineService.updatePeriodicLineV2(cycleCountNo, updatePeriodicLine, loginUserID);
		return new ResponseEntity<>(createdPeriodicResponse, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseApiResponse.class, value = "Update PeriodicLine") // label for swagger
	@PatchMapping("/v2/confirm/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLineConfirmV2(@PathVariable String cycleCountNo,
														 @RequestBody List<PeriodicLineV2> updatePerpetualLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		WarehouseApiResponse createdPerpetualLine =
				periodicLineService.updatePeriodicLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID);
		return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
	}


	//=================================================Zero Stock to Create Inventory===========================================================
	@ApiOperation(response = PeriodicLineV2.class, value = "Update PeriodicLine Zero Stock") // label for swagger
	@PostMapping("/v2/createPeriodicFromZeroStk")
	public ResponseEntity<?> createPeriodicLineV2(@RequestBody List<PeriodicZeroStockLine> updatePeriodicLine, @RequestParam String loginUserID) {
		List<PeriodicLineV2> createdPeriodicLine =
				periodicZeroStkLineService.updatePeriodicZeroStkLine(updatePeriodicLine, loginUserID);
		return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
	}
}