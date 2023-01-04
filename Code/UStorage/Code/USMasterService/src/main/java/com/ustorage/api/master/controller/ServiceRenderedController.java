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

import com.ustorage.api.master.model.servicerendered.AddServiceRendered;
import com.ustorage.api.master.model.servicerendered.ServiceRendered;
import com.ustorage.api.master.model.servicerendered.UpdateServiceRendered;
import com.ustorage.api.master.service.ServiceRenderedService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "ServiceRendered" }, value = "ServiceRendered Operations related to ServiceRenderedController") 
@SwaggerDefinition(tags = { @Tag(name = "ServiceRendered", description = "Operations related to ServiceRendered") })
@RequestMapping("/serviceRendered")
@RestController
public class ServiceRenderedController {

	@Autowired
	ServiceRenderedService serviceRenderedService;

	@ApiOperation(response = ServiceRendered.class, value = "Get all ServiceRendered details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ServiceRendered> serviceRenderedList = serviceRenderedService.getServiceRendered();
		return new ResponseEntity<>(serviceRenderedList, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Get a ServiceRendered") // label for swagger
	@GetMapping("/{serviceRenderedId}")
	public ResponseEntity<?> getServiceRendered(@PathVariable String serviceRenderedId) {
		ServiceRendered dbServiceRendered = serviceRenderedService.getServiceRendered(serviceRenderedId);
		log.info("ServiceRendered : " + dbServiceRendered);
		return new ResponseEntity<>(dbServiceRendered, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Create ServiceRendered") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postServiceRendered(@Valid @RequestBody AddServiceRendered newServiceRendered,
			@RequestParam String loginUserID) throws Exception {
		ServiceRendered createdServiceRendered = serviceRenderedService.createServiceRendered(newServiceRendered, loginUserID);
		return new ResponseEntity<>(createdServiceRendered, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Update ServiceRendered") // label for swagger
	@PatchMapping("/{serviceRenderedId}")
	public ResponseEntity<?> patchServiceRendered(@PathVariable String serviceRenderedId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateServiceRendered updateServiceRendered)
			throws IllegalAccessException, InvocationTargetException {
		ServiceRendered updatedServiceRendered = serviceRenderedService.updateServiceRendered(serviceRenderedId, loginUserID,
				updateServiceRendered);
		return new ResponseEntity<>(updatedServiceRendered, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Delete ServiceRendered") // label for swagger
	@DeleteMapping("/{serviceRenderedId}")
	public ResponseEntity<?> deleteServiceRendered(@PathVariable String serviceRenderedId, @RequestParam String loginUserID) {
		serviceRenderedService.deleteServiceRendered(serviceRenderedId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
