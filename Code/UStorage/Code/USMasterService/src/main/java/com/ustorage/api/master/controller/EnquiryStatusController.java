package com.ustorage.api.master.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ustorage.api.master.model.enquirystatus.AddEnquiryStatus;
import com.ustorage.api.master.model.enquirystatus.EnquiryStatus;
import com.ustorage.api.master.model.enquirystatus.UpdateEnquiryStatus;
import com.ustorage.api.master.service.EnquiryStatusService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "EnquiryStatus" }, value = "EnquiryStatus Operations related to EnquiryStatusController") 
@SwaggerDefinition(tags = { @Tag(name = "EnquiryStatus", description = "Operations related to EnquiryStatus") })
@RequestMapping("/enquiryStatus")
@RestController
public class EnquiryStatusController {

	@Autowired
	EnquiryStatusService enquiryStatusService;

	@ApiOperation(response = EnquiryStatus.class, value = "Get all EnquiryStatus details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<EnquiryStatus> enquiryStatusList = enquiryStatusService.getEnquiryStatus();
		return new ResponseEntity<>(enquiryStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Get a EnquiryStatus") // label for swagger
	@GetMapping("/{enquiryStatusId}")
	public ResponseEntity<?> getEnquiryStatus(@PathVariable String enquiryStatusId) {
		EnquiryStatus dbEnquiryStatus = enquiryStatusService.getEnquiryStatus(enquiryStatusId);
		log.info("EnquiryStatus : " + dbEnquiryStatus);
		return new ResponseEntity<>(dbEnquiryStatus, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Create EnquiryStatus") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postEnquiryStatus(@Valid @RequestBody AddEnquiryStatus newEnquiryStatus,
			@RequestParam String loginUserID) throws Exception {
		EnquiryStatus createdEnquiryStatus = enquiryStatusService.createEnquiryStatus(newEnquiryStatus, loginUserID);
		return new ResponseEntity<>(createdEnquiryStatus, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Update EnquiryStatus") // label for swagger
	@PatchMapping("/{enquiryStatusId}")
	public ResponseEntity<?> patchEnquiryStatus(@PathVariable String enquiryStatusId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateEnquiryStatus updateEnquiryStatus)
			throws IllegalAccessException, InvocationTargetException {
		EnquiryStatus updatedEnquiryStatus = enquiryStatusService.updateEnquiryStatus(enquiryStatusId, loginUserID,
				updateEnquiryStatus);
		return new ResponseEntity<>(updatedEnquiryStatus, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Delete EnquiryStatus") // label for swagger
	@DeleteMapping("/{enquiryStatusId}")
	public ResponseEntity<?> deleteEnquiryStatus(@PathVariable String enquiryStatusId, @RequestParam String loginUserID) {
		enquiryStatusService.deleteEnquiryStatus(enquiryStatusId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
