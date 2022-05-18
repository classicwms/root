package com.tekclover.wms.api.idmaster.controller;

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

import com.tekclover.wms.api.idmaster.model.strategyid.AddStrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.UpdateStrategyId;
import com.tekclover.wms.api.idmaster.service.StrategyIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StrategyId"}, value = "StrategyId  Operations related to StrategyIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StrategyId ",description = "Operations related to StrategyId ")})
@RequestMapping("/strategyid")
@RestController
public class StrategyIdController {
	
	@Autowired
	StrategyIdService strategyidService;
	
    @ApiOperation(response = StrategyId.class, value = "Get all StrategyId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StrategyId> strategyidList = strategyidService.getStrategyIds();
		return new ResponseEntity<>(strategyidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StrategyId.class, value = "Get a StrategyId") // label for swagger 
	@GetMapping("/{strategyNo}")
	public ResponseEntity<?> getStrategyId(@PathVariable String strategyNo, 
			@RequestParam String warehouseId, @RequestParam Long strategyTypeId) {
    	StrategyId strategyid = 
    			strategyidService.getStrategyId(warehouseId, strategyTypeId, strategyNo);
    	log.info("StrategyId : " + strategyid);
		return new ResponseEntity<>(strategyid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = StrategyId.class, value = "Search StrategyId") // label for swagger
//	@PostMapping("/findStrategyId")
//	public List<StrategyId> findStrategyId(@RequestBody SearchStrategyId searchStrategyId)
//			throws Exception {
//		return strategyidService.findStrategyId(searchStrategyId);
//	}
    
    @ApiOperation(response = StrategyId.class, value = "Create StrategyId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStrategyId(@Valid @RequestBody AddStrategyId newStrategyId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		StrategyId createdStrategyId = strategyidService.createStrategyId(newStrategyId, loginUserID);
		return new ResponseEntity<>(createdStrategyId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StrategyId.class, value = "Update StrategyId") // label for swagger
    @PatchMapping("/{strategyNo}")
	public ResponseEntity<?> patchStrategyId(@PathVariable String strategyNo, 
			@RequestParam String warehouseId, @RequestParam Long strategyTypeId, 
			@Valid @RequestBody UpdateStrategyId updateStrategyId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StrategyId createdStrategyId = 
				strategyidService.updateStrategyId(warehouseId, strategyTypeId, strategyNo, loginUserID, updateStrategyId);
		return new ResponseEntity<>(createdStrategyId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StrategyId.class, value = "Delete StrategyId") // label for swagger
	@DeleteMapping("/{strategyNo}")
	public ResponseEntity<?> deleteStrategyId(@PathVariable String strategyNo, 
			@RequestParam String warehouseId, @RequestParam Long strategyTypeId, @RequestParam String loginUserID) {
    	strategyidService.deleteStrategyId(warehouseId, strategyTypeId, strategyNo, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}