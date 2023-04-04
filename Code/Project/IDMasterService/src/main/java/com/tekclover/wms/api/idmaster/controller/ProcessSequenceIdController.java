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

import com.tekclover.wms.api.idmaster.model.processsequenceid.AddProcessSequenceId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.ProcessSequenceId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.UpdateProcessSequenceId;
import com.tekclover.wms.api.idmaster.service.ProcessSequenceIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ProcessSequenceId"}, value = "ProcessSequenceId  Operations related to ProcessSequenceIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProcessSequenceId ",description = "Operations related to ProcessSequenceId ")})
@RequestMapping("/processsequenceid")
@RestController
public class ProcessSequenceIdController {
	
	@Autowired
	ProcessSequenceIdService processsequenceidService;
	
    @ApiOperation(response = ProcessSequenceId.class, value = "Get all ProcessSequenceId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ProcessSequenceId> processsequenceidList = processsequenceidService.getProcessSequenceIds();
		return new ResponseEntity<>(processsequenceidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ProcessSequenceId.class, value = "Get a ProcessSequenceId") // label for swagger 
	@GetMapping("/{processId}")
	public ResponseEntity<?> getProcessSequenceId(@PathVariable Long processId, 
			@RequestParam String warehouseId, @RequestParam Long subLevelId) {
    	ProcessSequenceId processsequenceid = 
    			processsequenceidService.getProcessSequenceId(warehouseId, processId, subLevelId);
    	log.info("ProcessSequenceId : " + processsequenceid);
		return new ResponseEntity<>(processsequenceid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = ProcessSequenceId.class, value = "Search ProcessSequenceId") // label for swagger
//	@PostMapping("/findProcessSequenceId")
//	public List<ProcessSequenceId> findProcessSequenceId(@RequestBody SearchProcessSequenceId searchProcessSequenceId)
//			throws Exception {
//		return processsequenceidService.findProcessSequenceId(searchProcessSequenceId);
//	}
    
    @ApiOperation(response = ProcessSequenceId.class, value = "Create ProcessSequenceId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postProcessSequenceId(@Valid @RequestBody AddProcessSequenceId newProcessSequenceId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId createdProcessSequenceId = processsequenceidService.createProcessSequenceId(newProcessSequenceId, loginUserID);
		return new ResponseEntity<>(createdProcessSequenceId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessSequenceId.class, value = "Update ProcessSequenceId") // label for swagger
    @PatchMapping("/{processId}")
	public ResponseEntity<?> patchProcessSequenceId(@PathVariable Long processId, 
			@RequestParam String warehouseId, @RequestParam Long subLevelId,
			@Valid @RequestBody UpdateProcessSequenceId updateProcessSequenceId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId createdProcessSequenceId = 
				processsequenceidService.updateProcessSequenceId(warehouseId, processId, subLevelId, loginUserID, updateProcessSequenceId);
		return new ResponseEntity<>(createdProcessSequenceId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessSequenceId.class, value = "Delete ProcessSequenceId") // label for swagger
	@DeleteMapping("/{processId}")
	public ResponseEntity<?> deleteProcessSequenceId(@PathVariable Long processId, 
			@RequestParam String warehouseId, @RequestParam Long subLevelId, @RequestParam String loginUserID) {
    	processsequenceidService.deleteProcessSequenceId(warehouseId, processId, subLevelId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}