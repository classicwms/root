package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.processid.AddProcessId;
import com.tekclover.wms.api.idmaster.model.processid.FindProcessId;
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import com.tekclover.wms.api.idmaster.model.processid.UpdateProcessId;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
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
import java.sql.Struct;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ProcessId"}, value = "ProcessId  Operations related to ProcessIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProcessId ",description = "Operations related to ProcessId ")})
@RequestMapping("/processid")
@RestController
public class ProcessIdController {
	@Autowired
	private CompanyIdRepository companyIdRepository;

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
	public ResponseEntity<?> getProcessId(@RequestParam String warehouseId,@PathVariable String processId,@RequestParam String companyCodeId,
										  @RequestParam String languageId,@RequestParam String plantId ) {
    	ProcessId processid = 
    			processidService.getProcessId(warehouseId,processId,companyCodeId,languageId,plantId);
    	log.info("ProcessId : " + processid);
		return new ResponseEntity<>(processid, HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessId.class, value = "Create ProcessId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postProcessId(@Valid @RequestBody AddProcessId newProcessId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		ProcessId createdProcessId = processidService.createProcessId(newProcessId, loginUserID);
		return new ResponseEntity<>(createdProcessId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessId.class, value = "Update ProcessId") // label for swagger
    @PatchMapping("/{processId}")
	public ResponseEntity<?> patchProcessId(@RequestParam String warehouseId,@PathVariable String processId,@RequestParam String companyCodeId,
											@RequestParam String languageId,@RequestParam String plantId,
											@Valid @RequestBody UpdateProcessId updateProcessId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ProcessId createdProcessId = 
				processidService.updateProcessId(warehouseId, processId,companyCodeId,languageId,plantId,loginUserID,updateProcessId);
		return new ResponseEntity<>(createdProcessId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ProcessId.class, value = "Delete ProcessId") // label for swagger
	@DeleteMapping("/{processId}")
	public ResponseEntity<?> deleteProcessId(@RequestParam String warehouseId, @PathVariable String processId, @RequestParam String companyCodeId,
											 @RequestParam String languageId, @RequestParam String plantId,@RequestParam String loginUserID) {
    	processidService.deleteProcessId(warehouseId, processId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = ProcessId.class, value = "Find ProcessId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findProcessId(@Valid @RequestBody FindProcessId findProcessId) throws Exception {
		List<ProcessId> createdProcessId = processidService.findProcessId(findProcessId);
		return new ResponseEntity<>(createdProcessId, HttpStatus.OK);
	}
}