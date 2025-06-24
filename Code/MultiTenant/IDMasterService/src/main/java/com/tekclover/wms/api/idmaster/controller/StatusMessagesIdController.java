package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.statusmessagesid.AddStatusMessagesId;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.FindStatusMessagesId;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.StatusMessagesId;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.UpdateStatusMessagesId;
import com.tekclover.wms.api.idmaster.service.StatusMessagesIdService;
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
@Api(tags = {"StatusMessagesId"}, value = "StatusMessagesId  Operations related to StatusMessagesIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StatusMessagesId ",description = "Operations related to StatusMessagesId ")})
@RequestMapping("/statusmessagesid")
@RestController
public class StatusMessagesIdController {
	
	@Autowired
	StatusMessagesIdService statusmessagesidService;


    @ApiOperation(response = StatusMessagesId.class, value = "Get all StatusMessagesId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StatusMessagesId> statusmessagesidList = statusmessagesidService.getStatusMessagesIds();
		return new ResponseEntity<>(statusmessagesidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StatusMessagesId.class, value = "Get a StatusMessagesId") // label for swagger 
	@GetMapping("/{messagesId}")
	public ResponseEntity<?> getStatusMessagesId(@PathVariable String messagesId,
												 @RequestParam String languageId,
												 @RequestParam String messageType) {
    	StatusMessagesId statusmessagesid = 
    			statusmessagesidService.getStatusMessagesId(messagesId, languageId, messageType);
    	log.info("StatusMessagesId : " + statusmessagesid);
		return new ResponseEntity<>(statusmessagesid, HttpStatus.OK);
	}
    
    @ApiOperation(response = StatusMessagesId.class, value = "Create StatusMessagesId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStatusMessagesId(@Valid @RequestBody AddStatusMessagesId newStatusMessagesId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		StatusMessagesId createdStatusMessagesId = statusmessagesidService.createStatusMessagesId(newStatusMessagesId, loginUserID);
		return new ResponseEntity<>(createdStatusMessagesId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StatusMessagesId.class, value = "Update StatusMessagesId") // label for swagger
    @PatchMapping("/{messagesId}")
	public ResponseEntity<?> patchStatusMessagesId(@PathVariable String messagesId,
												   @RequestParam String languageId,
												   @RequestParam String messageType,
			@Valid @RequestBody UpdateStatusMessagesId updateStatusMessagesId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StatusMessagesId createdStatusMessagesId = 
				statusmessagesidService.updateStatusMessagesId(messagesId, languageId, messageType, loginUserID, updateStatusMessagesId);
		return new ResponseEntity<>(createdStatusMessagesId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StatusMessagesId.class, value = "Delete StatusMessagesId") // label for swagger
	@DeleteMapping("/{messagesId}")
	public ResponseEntity<?> deleteStatusMessagesId(@PathVariable String messagesId,
													@RequestParam String languageId,
													@RequestParam String messageType,
											  		@RequestParam String loginUserID) {
    	statusmessagesidService.deleteStatusMessagesId(messagesId, languageId, messageType, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = StatusMessagesId.class, value = "Find StatusMessagesId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findStatusMessagesId(@Valid @RequestBody FindStatusMessagesId findStatusMessagesId) throws Exception {
		List<StatusMessagesId> createdStatusMessagesId = statusmessagesidService.findStatusMessagesId(findStatusMessagesId);
		return new ResponseEntity<>(createdStatusMessagesId, HttpStatus.OK);
	}
}