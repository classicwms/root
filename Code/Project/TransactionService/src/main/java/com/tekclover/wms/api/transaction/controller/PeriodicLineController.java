package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
	
	@ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine") // label for swagger
	@PostMapping("/findPeriodicLine")
	public List<PeriodicLine> findPeriodicLine (@RequestBody SearchPeriodicLine searchPeriodicLine)
			throws Exception {
		return periodicLineService.findPeriodicLine (searchPeriodicLine);
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
		List<PeriodicLine> createdPeriodicLine = 
				periodicLineService.updatePeriodicLine (cycleCountNo, updatePeriodicLine, loginUserID);
		return new ResponseEntity<>(createdPeriodicLine , HttpStatus.OK);
	}
}