package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.auditlog.SearchAuditLog;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import lombok.Data;
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

import com.tekclover.wms.api.masters.model.auditlog.AddAuditLog;
import com.tekclover.wms.api.masters.model.auditlog.AuditLog;
import com.tekclover.wms.api.masters.model.auditlog.UpdateAuditLog;
import com.tekclover.wms.api.masters.service.AuditLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"AuditLog"}, value = "AuditLog  Operations related to AuditLogController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "AuditLog ",description = "Operations related to AuditLog ")})
@RequestMapping("/auditlog")
@RestController
public class AuditLogController {
	
	@Autowired
	AuditLogService auditlogService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = AuditLog.class, value = "Get all AuditLog details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<AuditLog> auditlogList = auditlogService.getAuditLogs();
		return new ResponseEntity<>(auditlogList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = AuditLog.class, value = "Get a AuditLog") // label for swagger 
	@GetMapping("/{auditFileNumber}")
	public ResponseEntity<?> getAuditLog(@PathVariable String auditFileNumber) {
    	AuditLog auditlog = auditlogService.getAuditLog(auditFileNumber);
//    	log.info("AuditLog : " + auditlog);
		return new ResponseEntity<>(auditlog, HttpStatus.OK);
	}
    
	@ApiOperation(response = AuditLog.class, value = "Search AuditLog") // label for swagger
	@PostMapping("/findAuditLog")
	public Stream<AuditLog> findAuditLog(@RequestBody SearchAuditLog searchAuditLog)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbNameList(searchAuditLog.getCompanyCodeId(), searchAuditLog.getPlantId(), searchAuditLog.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return auditlogService.findAuditLog(searchAuditLog);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	
    @ApiOperation(response = AuditLog.class, value = "Create AuditLog") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postAuditLog(@Valid @RequestBody AddAuditLog newAuditLog, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newAuditLog.getCompanyCodeId(), newAuditLog.getPlantId(), newAuditLog.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		AuditLog createdAuditLog = auditlogService.createAuditLog(newAuditLog, loginUserID);
		return new ResponseEntity<>(createdAuditLog , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = AuditLog.class, value = "Update AuditLog") // label for swagger
    @PatchMapping("/{auditFileNumber}")
	public ResponseEntity<?> patchAuditLog(@PathVariable String auditFileNumber, 
											@Valid @RequestBody UpdateAuditLog updateAuditLog,
										   	@RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(updateAuditLog.getCompanyCodeId(), updateAuditLog.getPlantId(), updateAuditLog.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		AuditLog createdAuditLog = auditlogService.updateAuditLog(auditFileNumber, updateAuditLog, loginUserID);
		return new ResponseEntity<>(createdAuditLog , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = AuditLog.class, value = "Delete AuditLog") // label for swagger
	@DeleteMapping("/{auditFileNumber}")
	public ResponseEntity<?> deleteAuditLog(@PathVariable String auditFileNumber, @RequestParam String loginUserID) {

		auditlogService.deleteAuditLog(auditFileNumber, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}