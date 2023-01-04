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

import com.ustorage.api.master.model.businesspartnertype.AddBusinessPartnerType;
import com.ustorage.api.master.model.businesspartnertype.BusinessPartnerType;
import com.ustorage.api.master.model.businesspartnertype.UpdateBusinessPartnerType;
import com.ustorage.api.master.service.BusinessPartnerTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "BusinessPartnerType" }, value = "BusinessPartnerType Operations related to BusinessPartnerTypeController") 
@SwaggerDefinition(tags = { @Tag(name = "BusinessPartnerType", description = "Operations related to BusinessPartnerType") })
@RequestMapping("/businessPartnerType")
@RestController
public class BusinessPartnerTypeController {

	@Autowired
	BusinessPartnerTypeService businessPartnerTypeService;

	@ApiOperation(response = BusinessPartnerType.class, value = "Get all BusinessPartnerType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BusinessPartnerType> businessPartnerTypeList = businessPartnerTypeService.getBusinessPartnerType();
		return new ResponseEntity<>(businessPartnerTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Get a BusinessPartnerType") // label for swagger
	@GetMapping("/{businessPartnerTypeId}")
	public ResponseEntity<?> getBusinessPartnerType(@PathVariable String businessPartnerTypeId) {
		BusinessPartnerType dbBusinessPartnerType = businessPartnerTypeService.getBusinessPartnerType(businessPartnerTypeId);
		log.info("BusinessPartnerType : " + dbBusinessPartnerType);
		return new ResponseEntity<>(dbBusinessPartnerType, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Create BusinessPartnerType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBusinessPartnerType(@Valid @RequestBody AddBusinessPartnerType newBusinessPartnerType,
			@RequestParam String loginUserID) throws Exception {
		BusinessPartnerType createdBusinessPartnerType = businessPartnerTypeService.createBusinessPartnerType(newBusinessPartnerType, loginUserID);
		return new ResponseEntity<>(createdBusinessPartnerType, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Update BusinessPartnerType") // label for swagger
	@PatchMapping("/{businessPartnerType}")
	public ResponseEntity<?> patchBusinessPartnerType(@PathVariable String businessPartnerType,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateBusinessPartnerType updateBusinessPartnerType)
			throws IllegalAccessException, InvocationTargetException {
		BusinessPartnerType updatedBusinessPartnerType = businessPartnerTypeService.updateBusinessPartnerType(businessPartnerType, loginUserID,
				updateBusinessPartnerType);
		return new ResponseEntity<>(updatedBusinessPartnerType, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Delete BusinessPartnerType") // label for swagger
	@DeleteMapping("/{businessPartnerType}")
	public ResponseEntity<?> deleteBusinessPartnerType(@PathVariable String businessPartnerType, @RequestParam String loginUserID) {
		businessPartnerTypeService.deleteBusinessPartnerType(businessPartnerType, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
