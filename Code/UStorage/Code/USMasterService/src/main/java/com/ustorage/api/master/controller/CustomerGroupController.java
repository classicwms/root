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

import com.ustorage.api.master.model.customergroup.AddCustomerGroup;
import com.ustorage.api.master.model.customergroup.CustomerGroup;
import com.ustorage.api.master.model.customergroup.UpdateCustomerGroup;
import com.ustorage.api.master.service.CustomerGroupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "CustomerGroup" }, value = "CustomerGroup Operations related to CustomerGroupController") 
@SwaggerDefinition(tags = { @Tag(name = "CustomerGroup", description = "Operations related to CustomerGroup") })
@RequestMapping("/customerGroup")
@RestController
public class CustomerGroupController {

	@Autowired
	CustomerGroupService customerGroupService;

	@ApiOperation(response = CustomerGroup.class, value = "Get all CustomerGroup details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<CustomerGroup> customerGroupList = customerGroupService.getCustomerGroup();
		return new ResponseEntity<>(customerGroupList, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Get a CustomerGroup") // label for swagger
	@GetMapping("/{customerGroupId}")
	public ResponseEntity<?> getCustomerGroup(@PathVariable String customerGroupId) {
		CustomerGroup dbCustomerGroup = customerGroupService.getCustomerGroup(customerGroupId);
		log.info("CustomerGroup : " + dbCustomerGroup);
		return new ResponseEntity<>(dbCustomerGroup, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Create CustomerGroup") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postCustomerGroup(@Valid @RequestBody AddCustomerGroup newCustomerGroup,
			@RequestParam String loginUserID) throws Exception {
		CustomerGroup createdCustomerGroup = customerGroupService.createCustomerGroup(newCustomerGroup, loginUserID);
		return new ResponseEntity<>(createdCustomerGroup, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Update CustomerGroup") // label for swagger
	@PatchMapping("/{customerGroupId}")
	public ResponseEntity<?> patchCustomerGroup(@PathVariable String customerGroupId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateCustomerGroup updateCustomerGroup)
			throws IllegalAccessException, InvocationTargetException {
		CustomerGroup updatedCustomerGroup = customerGroupService.updateCustomerGroup(customerGroupId, loginUserID,
				updateCustomerGroup);
		return new ResponseEntity<>(updatedCustomerGroup, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Delete CustomerGroup") // label for swagger
	@DeleteMapping("/{customerGroupId}")
	public ResponseEntity<?> deleteCustomerGroup(@PathVariable String customerGroupId, @RequestParam String loginUserID) {
		customerGroupService.deleteCustomerGroup(customerGroupId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
