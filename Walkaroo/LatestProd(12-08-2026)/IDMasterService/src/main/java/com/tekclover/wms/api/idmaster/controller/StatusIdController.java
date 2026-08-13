package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;
import com.tekclover.wms.api.idmaster.model.statusid.FindStatusId;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
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

import com.tekclover.wms.api.idmaster.model.statusid.AddStatusId;
import com.tekclover.wms.api.idmaster.model.statusid.StatusId;
import com.tekclover.wms.api.idmaster.model.statusid.UpdateStatusId;
import com.tekclover.wms.api.idmaster.service.StatusIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StatusId"}, value = "StatusId  Operations related to StatusIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StatusId ",description = "Operations related to StatusId ")})
@RequestMapping("/statusid")
@RestController
public class StatusIdController {
	@Autowired
	private LanguageIdRepository languageIdRepository;

	@Autowired
	StatusIdService statusidService;
	
    @ApiOperation(response = StatusId.class, value = "Get all StatusId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StatusId> statusidList = statusidService.getStatusIds();
		return new ResponseEntity<>(statusidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StatusId.class, value = "Get a StatusId") // label for swagger 
	@GetMapping("/{statusId}")
	public ResponseEntity<?> getStatusId(@PathVariable Long statusId,@RequestParam String languageId) {
    	StatusId statusid = 
    			statusidService.getStatusId(statusId,languageId);
    	log.info("StatusId : " + statusid);
		return new ResponseEntity<>(statusid, HttpStatus.OK);
	}

//	@ApiOperation(response = StatusId.class, value = "Get a StatusId") // label for swagger
//	@GetMapping("/v2/{statusId}")
//	public ResponseEntity<?> getStatusId(@PathVariable Long statusId, @RequestParam String languageId, @RequestParam String warehouseId) {
//    	StatusId statusid =
//    			statusidService.getStatusId(statusId,languageId, warehouseId);
//    	log.info("StatusId : " + statusid);
//		return new ResponseEntity<>(statusid, HttpStatus.OK);
//	}
    
//	@ApiOperation(response = StatusId.class, value = "Search StatusId") // label for swagger
//	@PostMapping("/findStatusId")
//	public List<StatusId> findStatusId(@RequestBody SearchStatusId searchStatusId)
//			throws Exception {
//		return statusidService.findStatusId(searchStatusId);
//	}
    
    @ApiOperation(response = StatusId.class, value = "Create StatusId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStatusId(@Valid @RequestBody AddStatusId newStatusId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		StatusId createdStatusId = statusidService.createStatusId(newStatusId, loginUserID);
		return new ResponseEntity<>(createdStatusId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StatusId.class, value = "Update StatusId") // label for swagger
    @PatchMapping("/{statusId}")
	public ResponseEntity<?> patchStatusId(@PathVariable Long statusId, 
			 @RequestParam String languageId,
			@Valid @RequestBody UpdateStatusId updateStatusId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StatusId createdStatusId = 
				statusidService.updateStatusId(statusId,languageId,loginUserID, updateStatusId);
		return new ResponseEntity<>(createdStatusId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StatusId.class, value = "Delete StatusId") // label for swagger
	@DeleteMapping("/{statusId}")
	public ResponseEntity<?> deleteStatusId(@PathVariable Long statusId, 
			 @RequestParam String languageId, @RequestParam String loginUserID) {
    	statusidService.deleteStatusId(statusId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = StatusId.class, value = "Find StatusId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findStatusId(@Valid @RequestBody FindStatusId findStatusId) throws Exception {
		List<StatusId> createdStatusId = statusidService.findStatusId(findStatusId);
		return new ResponseEntity<>(createdStatusId, HttpStatus.OK);
	}
}