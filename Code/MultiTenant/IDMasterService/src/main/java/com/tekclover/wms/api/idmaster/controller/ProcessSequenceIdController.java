package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.processsequenceid.FindProcessSequenceId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
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

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = ProcessSequenceId.class, value = "Get all ProcessSequenceId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ProcessSequenceId> processsequenceidList = processsequenceidService.getProcessSequenceIds();
		return new ResponseEntity<>(processsequenceidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ProcessSequenceId.class, value = "Get a ProcessSequenceId") // label for swagger 
	@GetMapping("/{processSequenceId}")
	public ResponseEntity<?> getProcessSequenceId(@RequestParam String processId,
			@RequestParam String warehouseId, @PathVariable Long processSequenceId,
												  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ProcessSequenceId processsequenceid =
    			processsequenceidService.getProcessSequenceId(warehouseId, processId,processSequenceId,companyCodeId,languageId,plantId);
    	log.info("ProcessSequenceId : " + processsequenceid);
		return new ResponseEntity<>(processsequenceid, HttpStatus.OK);
	}
finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ProcessSequenceId.class, value = "Create ProcessSequenceId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postProcessSequenceId(@Valid @RequestBody AddProcessSequenceId newProcessSequenceId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newProcessSequenceId.getCompanyCodeId(), newProcessSequenceId.getPlantId(), newProcessSequenceId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ProcessSequenceId createdProcessSequenceId = processsequenceidService.createProcessSequenceId(newProcessSequenceId, loginUserID);
		return new ResponseEntity<>(createdProcessSequenceId , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ProcessSequenceId.class, value = "Update ProcessSequenceId") // label for swagger
    @PatchMapping("/{processSequenceId}")
	public ResponseEntity<?> patchProcessSequenceId(@RequestParam String processId,
			@RequestParam String warehouseId, @PathVariable Long processSequenceId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateProcessSequenceId updateProcessSequenceId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ProcessSequenceId createdProcessSequenceId =
				processsequenceidService.updateProcessSequenceId(warehouseId, processId, processSequenceId,companyCodeId,languageId,plantId,loginUserID, updateProcessSequenceId);
		return new ResponseEntity<>(createdProcessSequenceId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ProcessSequenceId.class, value = "Delete ProcessSequenceId") // label for swagger
	@DeleteMapping("/{processSequenceId}")
	public ResponseEntity<?> deleteProcessSequenceId(@RequestParam String processId,
			@RequestParam String warehouseId, @PathVariable Long processSequenceId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		processsequenceidService.deleteProcessSequenceId(warehouseId, processId, processSequenceId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = ProcessSequenceId.class, value = "Find ProcessSequenceId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findProcessSequenceId(@Valid @RequestBody FindProcessSequenceId findProcessSequenceId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findProcessSequenceId.getCompanyCodeId(), findProcessSequenceId.getPlantId(), findProcessSequenceId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ProcessSequenceId> createdProcessSequenceId= processsequenceidService.findProcessSequenceId(findProcessSequenceId);
		return new ResponseEntity<>(createdProcessSequenceId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}