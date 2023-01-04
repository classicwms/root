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

import com.ustorage.api.master.model.customertype.AddCustomerType;
import com.ustorage.api.master.model.customertype.CustomerType;
import com.ustorage.api.master.model.customertype.UpdateCustomerType;
import com.ustorage.api.master.service.CustomerTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "CustomerType" }, value = "CustomerType Operations related to CustomerTypeController") 
@SwaggerDefinition(tags = { @Tag(name = "CustomerType", description = "Operations related to CustomerType") })
@RequestMapping("/customerType")
@RestController
public class CustomerTypeController {

	@Autowired
	CustomerTypeService customerTypeService;

	@ApiOperation(response = CustomerType.class, value = "Get all CustomerType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<CustomerType> customerTypeList = customerTypeService.getCustomerType();
		return new ResponseEntity<>(customerTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Get a CustomerType") // label for swagger
	@GetMapping("/{customerTypeId}")
	public ResponseEntity<?> getCustomerType(@PathVariable String customerTypeId) {
		CustomerType dbCustomerType = customerTypeService.getCustomerType(customerTypeId);
		log.info("CustomerType : " + dbCustomerType);
		return new ResponseEntity<>(dbCustomerType, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Create CustomerType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postCustomerType(@Valid @RequestBody AddCustomerType newCustomerType,
			@RequestParam String loginUserID) throws Exception {
		CustomerType createdCustomerType = customerTypeService.createCustomerType(newCustomerType, loginUserID);
		return new ResponseEntity<>(createdCustomerType, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Update CustomerType") // label for swagger
	@PatchMapping("/{customerTypeId}")
	public ResponseEntity<?> patchCustomerType(@PathVariable String customerTypeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateCustomerType updateCustomerType)
			throws IllegalAccessException, InvocationTargetException {
		CustomerType updatedCustomerType = customerTypeService.updateCustomerType(customerTypeId, loginUserID,
				updateCustomerType);
		return new ResponseEntity<>(updatedCustomerType, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Delete CustomerType") // label for swagger
	@DeleteMapping("/{customerTypeId}")
	public ResponseEntity<?> deleteCustomerType(@PathVariable String customerTypeId, @RequestParam String loginUserID) {
		customerTypeService.deleteCustomerType(customerTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
