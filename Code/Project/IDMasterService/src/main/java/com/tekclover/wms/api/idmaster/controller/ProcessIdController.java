package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.processid.AddProcessId;
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import com.tekclover.wms.api.idmaster.model.processid.UpdateProcessId;
import com.tekclover.wms.api.idmaster.service.ProcessIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ProcessId"}, value = "ProcessId  Operations related to ProcessIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProcessId ",description = "Operations related to ProcessId ")})
@RequestMapping("/processid")
@RestController
public class ProcessIdController {
	
	@Autowired
	ProcessIdService processidService;
	
    @ApiOperation(response = ProcessId.class, value = "Get all ProcessId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ProcessId> processidList = processidService.getProcessIds();
		return new ResponseEntity<>(processidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ProcessId.class, value = "Get a ProcessId") // label for swagger 
	@GetMapping("/{processId}")
	public ResponseEntity<?> getProcessId(@PathVariable String processId,
			@RequestParam String warehouseId) {
    	ProcessId processid = 
    			processidService.getProcessId(warehouseId, processId);
    	log.info("ProcessId : " + processid);
		return new ResponseEntity<>(processid, HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessId.class, value = "Create ProcessId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postProcessId(@Valid @RequestBody AddProcessId newProcessId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		ProcessId createdProcessId = processidService.createProcessId(newProcessId, loginUserID);
		return new ResponseEntity<>(createdProcessId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessId.class, value = "Update ProcessId") // label for swagger
    @PatchMapping("/{processId}")
	public ResponseEntity<?> patchProcessId(@PathVariable String processId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateProcessId updateProcessId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ProcessId createdProcessId = 
				processidService.updateProcessId(warehouseId, processId, loginUserID, updateProcessId);
		return new ResponseEntity<>(createdProcessId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessId.class, value = "Delete ProcessId") // label for swagger
	@DeleteMapping("/{processId}")
	public ResponseEntity<?> deleteProcessId(@PathVariable String processId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	processidService.deleteProcessId(warehouseId, processId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}