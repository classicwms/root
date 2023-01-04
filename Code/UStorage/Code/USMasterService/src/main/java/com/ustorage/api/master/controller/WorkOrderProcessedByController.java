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

import com.ustorage.api.master.model.workorderprocessedby.AddWorkOrderProcessedBy;
import com.ustorage.api.master.model.workorderprocessedby.WorkOrderProcessedBy;
import com.ustorage.api.master.model.workorderprocessedby.UpdateWorkOrderProcessedBy;
import com.ustorage.api.master.service.WorkOrderProcessedByService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "WorkOrderProcessedBy" }, value = "WorkOrderProcessedBy Operations related to WorkOrderProcessedByController") 
@SwaggerDefinition(tags = { @Tag(name = "WorkOrderProcessedBy", description = "Operations related to WorkOrderProcessedBy") })
@RequestMapping("/workOrderProcessedBy")
@RestController
public class WorkOrderProcessedByController {

	@Autowired
	WorkOrderProcessedByService workOrderProcessedByService;

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Get all WorkOrderProcessedBy details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<WorkOrderProcessedBy> workOrderProcessedByList = workOrderProcessedByService.getWorkOrderProcessedBy();
		return new ResponseEntity<>(workOrderProcessedByList, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Get a WorkOrderProcessedBy") // label for swagger
	@GetMapping("/{workOrderProcessedById}")
	public ResponseEntity<?> getWorkOrderProcessedBy(@PathVariable String workOrderProcessedById) {
		WorkOrderProcessedBy dbWorkOrderProcessedBy = workOrderProcessedByService.getWorkOrderProcessedBy(workOrderProcessedById);
		log.info("WorkOrderProcessedBy : " + dbWorkOrderProcessedBy);
		return new ResponseEntity<>(dbWorkOrderProcessedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Create WorkOrderProcessedBy") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWorkOrderProcessedBy(@Valid @RequestBody AddWorkOrderProcessedBy newWorkOrderProcessedBy,
			@RequestParam String loginUserID) throws Exception {
		WorkOrderProcessedBy createdWorkOrderProcessedBy = workOrderProcessedByService.createWorkOrderProcessedBy(newWorkOrderProcessedBy, loginUserID);
		return new ResponseEntity<>(createdWorkOrderProcessedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Update WorkOrderProcessedBy") // label for swagger
	@PatchMapping("/{workOrderProcessedById}")
	public ResponseEntity<?> patchWorkOrderProcessedBy(@PathVariable String workOrderProcessedById,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateWorkOrderProcessedBy updateWorkOrderProcessedBy)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderProcessedBy updatedWorkOrderProcessedBy = workOrderProcessedByService.updateWorkOrderProcessedBy(workOrderProcessedById, loginUserID,
				updateWorkOrderProcessedBy);
		return new ResponseEntity<>(updatedWorkOrderProcessedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Delete WorkOrderProcessedBy") // label for swagger
	@DeleteMapping("/{workOrderProcessedById}")
	public ResponseEntity<?> deleteWorkOrderProcessedBy(@PathVariable String workOrderProcessedById, @RequestParam String loginUserID) {
		workOrderProcessedByService.deleteWorkOrderProcessedBy(workOrderProcessedById, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
