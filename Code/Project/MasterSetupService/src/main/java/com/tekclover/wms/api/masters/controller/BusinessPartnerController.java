package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

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

import com.tekclover.wms.api.masters.model.businesspartner.AddBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.SearchBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.UpdateBusinessPartner;
import com.tekclover.wms.api.masters.service.BusinessPartnerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"BusinessPartner"}, value = "BusinessPartner  Operations related to BusinessPartnerController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BusinessPartner ",description = "Operations related to BusinessPartner ")})
@RequestMapping("/businesspartner")
@RestController
public class BusinessPartnerController {
	
	@Autowired
	BusinessPartnerService businesspartnerService;
	
    @ApiOperation(response = BusinessPartner.class, value = "Get all BusinessPartner details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BusinessPartner> businesspartnerList = businesspartnerService.getBusinessPartners();
		return new ResponseEntity<>(businesspartnerList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BusinessPartner.class, value = "Get a BusinessPartner") // label for swagger 
	@GetMapping("/{partnerCode}")
	public ResponseEntity<?> getBusinessPartner(@PathVariable String partnerCode) {
    	BusinessPartner businesspartner = businesspartnerService.getBusinessPartner(partnerCode);
    	log.info("BusinessPartner : " + businesspartner);
		return new ResponseEntity<>(businesspartner, HttpStatus.OK);
	}
    
	@ApiOperation(response = BusinessPartner.class, value = "Search BusinessPartner") // label for swagger
	@PostMapping("/findBusinessPartner")
	public List<BusinessPartner> findBusinessPartner(@RequestBody SearchBusinessPartner searchBusinessPartner)
			throws Exception {
		return businesspartnerService.findBusinessPartner(searchBusinessPartner);
	}
	
    @ApiOperation(response = BusinessPartner.class, value = "Create BusinessPartner") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBusinessPartner(@Valid @RequestBody AddBusinessPartner newBusinessPartner, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BusinessPartner createdBusinessPartner = businesspartnerService.createBusinessPartner(newBusinessPartner, loginUserID);
		return new ResponseEntity<>(createdBusinessPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = BusinessPartner.class, value = "Update BusinessPartner") // label for swagger
    @PatchMapping("/{partnerCode}")
	public ResponseEntity<?> patchBusinessPartner(@PathVariable String partnerCode, 
			@Valid @RequestBody UpdateBusinessPartner updateBusinessPartner, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BusinessPartner createdBusinessPartner = businesspartnerService.updateBusinessPartner(partnerCode, updateBusinessPartner, loginUserID);
		return new ResponseEntity<>(createdBusinessPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = BusinessPartner.class, value = "Delete BusinessPartner") // label for swagger
	@DeleteMapping("/{partnerCode}")
	public ResponseEntity<?> deleteBusinessPartner(@PathVariable String partnerCode, @RequestParam String loginUserID) {
    	businesspartnerService.deleteBusinessPartner(partnerCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}