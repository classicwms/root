package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.AddApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.FindApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.UpdateApprovalProcessId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.ApprovalProcessIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
@Api(tags = {"ApprovalProcessId"}, value = "ApprovalProcessId  Operations related to ApprovalProcessIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ApprovalProcessId ",description = "Operations related to ApprovalProcessId ")})
@RequestMapping("/approvalprocessid")
@RestController
public class ApprovalProcessIdController {
	
	@Autowired
	ApprovalProcessIdService approvalprocessidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = ApprovalProcessId.class, value = "Get all ApprovalProcessId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ApprovalProcessId> approvalprocessidList = approvalprocessidService.getApprovalProcessIds();
		return new ResponseEntity<>(approvalprocessidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ApprovalProcessId.class, value = "Get a ApprovalProcessId") // label for swagger 
	@GetMapping("/{approvalProcessId}")
	public ResponseEntity<?> getApprovalProcessId(@PathVariable String approvalProcessId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ApprovalProcessId approvalprocessid =
    			approvalprocessidService.getApprovalProcessId(warehouseId,approvalProcessId,companyCodeId,languageId,plantId);
    	log.info("ApprovalProcessId : " + approvalprocessid);
		return new ResponseEntity<>(approvalprocessid, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ApprovalProcessId.class, value = "Create ApprovalProcessId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postApprovalProcessId(@Valid @RequestBody AddApprovalProcessId newApprovalProcessId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {

		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newApprovalProcessId.getCompanyCodeId(), newApprovalProcessId.getPlantId(), newApprovalProcessId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ApprovalProcessId createdApprovalProcessId = approvalprocessidService.createApprovalProcessId(newApprovalProcessId, loginUserID);
		return new ResponseEntity<>(createdApprovalProcessId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ApprovalProcessId.class, value = "Update ApprovalProcessId") // label for swagger
    @PatchMapping("/{approvalProcessId}")
	public ResponseEntity<?> patchApprovalProcessId(@PathVariable String approvalProcessId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateApprovalProcessId updateApprovalProcessId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ApprovalProcessId createdApprovalProcessId =
				approvalprocessidService.updateApprovalProcessId(warehouseId, approvalProcessId, companyCodeId,languageId,plantId,loginUserID, updateApprovalProcessId);
		return new ResponseEntity<>(createdApprovalProcessId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ApprovalProcessId.class, value = "Delete ApprovalProcessId") // label for swagger
	@DeleteMapping("/{approvalProcessId}")
	public ResponseEntity<?> deleteApprovalProcessId(@PathVariable String approvalProcessId,
													 @RequestParam String warehouseId, @RequestParam String companyCodeId,
													 @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		approvalprocessidService.deleteApprovalProcessId(warehouseId,approvalProcessId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = ApprovalProcessId.class, value = "Find ApprovalProcessId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findApprovalProcessId(@Valid @RequestBody FindApprovalProcessId findApprovalProcessId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findApprovalProcessId.getCompanyCodeId(), findApprovalProcessId.getPlantId(), findApprovalProcessId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ApprovalProcessId> createdApprovalProcessId = approvalprocessidService.findApprovalProcessId(findApprovalProcessId);
		return new ResponseEntity<>(createdApprovalProcessId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}