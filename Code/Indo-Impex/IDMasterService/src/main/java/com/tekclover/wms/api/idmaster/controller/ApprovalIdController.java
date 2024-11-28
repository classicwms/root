package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.approvalid.*;
import com.tekclover.wms.api.idmaster.service.ApprovalIdService;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ApprovalId"}, value = "ApprovalId  Operations related to ApprovalIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ApprovalId ",description = "Operations related to ApprovalId ")})
@RequestMapping("/approvalid")
@RestController
public class ApprovalIdController {
	
	@Autowired
	ApprovalIdService approvalidService;
	
    @ApiOperation(response = ApprovalId.class, value = "Get all ApprovalId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ApprovalId> approvalidList = approvalidService.getApprovalIds();
		return new ResponseEntity<>(approvalidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ApprovalId.class, value = "Get a ApprovalId") // label for swagger 
	@GetMapping("/{approvalId}")
	public ResponseEntity<?> getApprovalId(@PathVariable String approvalId,@RequestParam String approvalProcessId,
			@RequestParam String warehouseId,@RequestParam String approvalLevel,@RequestParam String companyCodeId,
										   @RequestParam String languageId,@RequestParam String plantId) {
    	ApprovalId approvalid = 
    			approvalidService.getApprovalId(warehouseId, approvalId, approvalLevel,approvalProcessId,companyCodeId,languageId,plantId);
    	log.info("ApprovalId : " + approvalid);
		return new ResponseEntity<>(approvalid, HttpStatus.OK);
	}
    
    @ApiOperation(response = ApprovalId.class, value = "Create ApprovalId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postApprovalId(@Valid @RequestBody AddApprovalId newApprovalId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		ApprovalId createdApprovalId = approvalidService.createApprovalId(newApprovalId, loginUserID);
		return new ResponseEntity<>(createdApprovalId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ApprovalId.class, value = "Update ApprovalId") // label for swagger
    @PatchMapping("/{approvalId}")
	public ResponseEntity<?> patchApprovalId(@PathVariable String approvalId,@RequestParam String approvalProcessId,
			@RequestParam String warehouseId,@RequestParam String approvalLevel,@RequestParam String companyCodeId,
											 @RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateApprovalId updateApprovalId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ApprovalId createdApprovalId = 
				approvalidService.updateApprovalId(warehouseId, approvalId,approvalLevel,approvalProcessId,companyCodeId,languageId,plantId,loginUserID, updateApprovalId);
		return new ResponseEntity<>(createdApprovalId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ApprovalId.class, value = "Delete ApprovalId") // label for swagger
	@DeleteMapping("/{approvalId}")
	public ResponseEntity<?> deleteApprovalId(@PathVariable String approvalId,@RequestParam String approvalProcessId,
			@RequestParam String warehouseId,@RequestParam String approvalLevel,@RequestParam String companyCodeId,
											  @RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
    	approvalidService.deleteApprovalId(warehouseId, approvalId, approvalLevel,approvalProcessId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = ApprovalId.class, value = "Find ApprovalId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findApprovalId(@Valid @RequestBody FindApprovalId findApprovalId) throws Exception {
		List<ApprovalId> createdApprovalId = approvalidService.findApprovalId(findApprovalId);
		return new ResponseEntity<>(createdApprovalId, HttpStatus.OK);
	}
}