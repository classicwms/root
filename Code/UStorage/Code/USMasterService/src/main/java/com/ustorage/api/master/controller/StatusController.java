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

import com.ustorage.api.master.model.status.AddStatus;
import com.ustorage.api.master.model.status.Status;
import com.ustorage.api.master.model.status.UpdateStatus;
import com.ustorage.api.master.service.StatusService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Status" }, value = "Status Operations related to StatusController") 
@SwaggerDefinition(tags = { @Tag(name = "Status", description = "Operations related to Status") })
@RequestMapping("/status")
@RestController
public class StatusController {

	@Autowired
	StatusService statusService;

	@ApiOperation(response = Status.class, value = "Get all Status details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Status> statusList = statusService.getStatus();
		return new ResponseEntity<>(statusList, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Get a Status") // label for swagger
	@GetMapping("/{statusId}")
	public ResponseEntity<?> getStatus(@PathVariable String statusId) {
		Status dbStatus = statusService.getStatus(statusId);
		log.info("Status : " + dbStatus);
		return new ResponseEntity<>(dbStatus, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Create Status") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStatus(@Valid @RequestBody AddStatus newStatus,
			@RequestParam String loginUserID) throws Exception {
		Status createdStatus = statusService.createStatus(newStatus, loginUserID);
		return new ResponseEntity<>(createdStatus, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Update Status") // label for swagger
	@PatchMapping("/{statusId}")
	public ResponseEntity<?> patchStatus(@PathVariable String statusId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateStatus updateStatus)
			throws IllegalAccessException, InvocationTargetException {
		Status updatedStatus = statusService.updateStatus(statusId, loginUserID,
				updateStatus);
		return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Delete Status") // label for swagger
	@DeleteMapping("/{statusId}")
	public ResponseEntity<?> deleteStatus(@PathVariable String statusId, @RequestParam String loginUserID) {
		statusService.deleteStatus(statusId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
