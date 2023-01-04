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

import com.ustorage.api.master.model.workordercreatedby.AddWorkOrderCreatedBy;
import com.ustorage.api.master.model.workordercreatedby.WorkOrderCreatedBy;
import com.ustorage.api.master.model.workordercreatedby.UpdateWorkOrderCreatedBy;
import com.ustorage.api.master.service.WorkOrderCreatedByService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "WorkOrderCreatedBy" }, value = "WorkOrderCreatedBy Operations related to WorkOrderCreatedByController") 
@SwaggerDefinition(tags = { @Tag(name = "WorkOrderCreatedBy", description = "Operations related to WorkOrderCreatedBy") })
@RequestMapping("/workOrderCreatedBy")
@RestController
public class WorkOrderCreatedByController {

	@Autowired
	WorkOrderCreatedByService workOrderCreatedByService;

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Get all WorkOrderCreatedBy details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<WorkOrderCreatedBy> workOrderCreatedByList = workOrderCreatedByService.getWorkOrderCreatedBy();
		return new ResponseEntity<>(workOrderCreatedByList, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Get a WorkOrderCreatedBy") // label for swagger
	@GetMapping("/{workOrderCreatedById}")
	public ResponseEntity<?> getWorkOrderCreatedBy(@PathVariable String workOrderCreatedById) {
		WorkOrderCreatedBy dbWorkOrderCreatedBy = workOrderCreatedByService.getWorkOrderCreatedBy(workOrderCreatedById);
		log.info("WorkOrderCreatedBy : " + dbWorkOrderCreatedBy);
		return new ResponseEntity<>(dbWorkOrderCreatedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Create WorkOrderCreatedBy") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWorkOrderCreatedBy(@Valid @RequestBody AddWorkOrderCreatedBy newWorkOrderCreatedBy,
			@RequestParam String loginUserID) throws Exception {
		WorkOrderCreatedBy createdWorkOrderCreatedBy = workOrderCreatedByService.createWorkOrderCreatedBy(newWorkOrderCreatedBy, loginUserID);
		return new ResponseEntity<>(createdWorkOrderCreatedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Update WorkOrderCreatedBy") // label for swagger
	@PatchMapping("/{workOrderCreatedById}")
	public ResponseEntity<?> patchWorkOrderCreatedBy(@PathVariable String workOrderCreatedById,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateWorkOrderCreatedBy updateWorkOrderCreatedBy)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderCreatedBy updatedWorkOrderCreatedBy = workOrderCreatedByService.updateWorkOrderCreatedBy(workOrderCreatedById, loginUserID,
				updateWorkOrderCreatedBy);
		return new ResponseEntity<>(updatedWorkOrderCreatedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Delete WorkOrderCreatedBy") // label for swagger
	@DeleteMapping("/{workOrderCreatedById}")
	public ResponseEntity<?> deleteWorkOrderCreatedBy(@PathVariable String workOrderCreatedById, @RequestParam String loginUserID) {
		workOrderCreatedByService.deleteWorkOrderCreatedBy(workOrderCreatedById, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
