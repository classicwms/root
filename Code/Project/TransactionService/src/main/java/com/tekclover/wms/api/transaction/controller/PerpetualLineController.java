package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AssignHHTUserCC;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.UpdatePerpetualLine;
import com.tekclover.wms.api.transaction.service.PerpetualLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PerpetualLine"}, value = "PerpetualLine  Operations related to PerpetualLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PerpetualLine ",description = "Operations related to PerpetualLine ")})
@RequestMapping("/perpetualline")
@RestController
public class PerpetualLineController {
	
	@Autowired
	PerpetualLineService perpetuallineService;
	
    @ApiOperation(response = PerpetualLine[].class, value = "AssignHHTUser") // label for swagger
    @PatchMapping("/assigingHHTUser")
	public ResponseEntity<?> patchAssingHHTUser (@RequestBody List<AssignHHTUserCC> assignHHTUser, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PerpetualLine> createdPerpetualLine = perpetuallineService.updateAssingHHTUser (assignHHTUser, loginUserID);
		return new ResponseEntity<>(createdPerpetualLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = PerpetualLine.class, value = "Update PerpetualLine") // label for swagger
    @PatchMapping("/{cycleCountNo}")
	public ResponseEntity<?> patchPerpetualLine (@PathVariable String cycleCountNo, 
			@RequestBody List<UpdatePerpetualLine> updatePerpetualLine, @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PerpetualLine> createdPerpetualLine = 
				perpetuallineService.updatePerpetualLine (cycleCountNo, updatePerpetualLine, loginUserID);
		return new ResponseEntity<>(createdPerpetualLine , HttpStatus.OK);
	}
}