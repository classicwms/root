package com.ustorage.api.trans.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import com.ustorage.api.trans.model.enquiry.*;

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

import com.ustorage.api.trans.service.EnquiryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Enquiry" }, value = "Enquiry Operations related to EnquiryController") 
@SwaggerDefinition(tags = { @Tag(name = "Enquiry", description = "Operations related to Enquiry") })
@RequestMapping("/enquiry")
@RestController
public class EnquiryController {

	@Autowired
	EnquiryService enquiryService;

	@ApiOperation(response = Enquiry.class, value = "Get all Enquiry details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Enquiry> enquiryList = enquiryService.getEnquiry();
		return new ResponseEntity<>(enquiryList, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Get a Enquiry") // label for swagger
	@GetMapping("/{enquiryId}")
	public ResponseEntity<?> getEnquiry(@PathVariable String enquiryId) {
		Enquiry dbEnquiry = enquiryService.getEnquiry(enquiryId);
		log.info("Enquiry : " + dbEnquiry);
		return new ResponseEntity<>(dbEnquiry, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Create Enquiry") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postEnquiry(@Valid @RequestBody AddEnquiry newEnquiry,
			@RequestParam String loginUserID) throws Exception {
		Enquiry createdEnquiry = enquiryService.createEnquiry(newEnquiry, loginUserID);
		return new ResponseEntity<>(createdEnquiry, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Update Enquiry") // label for swagger
	@PatchMapping("/{enquiry}")
	public ResponseEntity<?> patchEnquiry(@PathVariable String enquiry,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateEnquiry updateEnquiry)
			throws IllegalAccessException, InvocationTargetException {
		Enquiry updatedEnquiry = enquiryService.updateEnquiry(enquiry, loginUserID,
				updateEnquiry);
		return new ResponseEntity<>(updatedEnquiry, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Delete Enquiry") // label for swagger
	@DeleteMapping("/{enquiry}")
	public ResponseEntity<?> deleteEnquiry(@PathVariable String enquiry, @RequestParam String loginUserID) {
		enquiryService.deleteEnquiry(enquiry, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Enquiry.class, value = "Find Enquiry") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findEnquiry(@Valid @RequestBody FindEnquiry findEnquiry) throws Exception {
		List<Enquiry> createdEnquiry = enquiryService.findEnquiry(findEnquiry);
		return new ResponseEntity<>(createdEnquiry, HttpStatus.OK);
	}
}
