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

import com.ustorage.api.master.model.workorderstatus.AddWorkOrderStatus;
import com.ustorage.api.master.model.workorderstatus.WorkOrderStatus;
import com.ustorage.api.master.model.workorderstatus.UpdateWorkOrderStatus;
import com.ustorage.api.master.service.WorkOrderStatusService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "WorkOrderStatus" }, value = "WorkOrderStatus Operations related to WorkOrderStatusController") 
@SwaggerDefinition(tags = { @Tag(name = "WorkOrderStatus", description = "Operations related to WorkOrderStatus") })
@RequestMapping("/workOrderStatus")
@RestController
public class WorkOrderStatusController {

	@Autowired
	WorkOrderStatusService workOrderStatusService;

	@ApiOperation(response = WorkOrderStatus.class, value = "Get all WorkOrderStatus details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<WorkOrderStatus> workOrderStatusList = workOrderStatusService.getWorkOrderStatus();
		return new ResponseEntity<>(workOrderStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Get a WorkOrderStatus") // label for swagger
	@GetMapping("/{workOrderStatusId}")
	public ResponseEntity<?> getWorkOrderStatus(@PathVariable String workOrderStatusId) {
		WorkOrderStatus dbWorkOrderStatus = workOrderStatusService.getWorkOrderStatus(workOrderStatusId);
		log.info("WorkOrderStatus : " + dbWorkOrderStatus);
		return new ResponseEntity<>(dbWorkOrderStatus, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Create WorkOrderStatus") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWorkOrderStatus(@Valid @RequestBody AddWorkOrderStatus newWorkOrderStatus,
			@RequestParam String loginUserID) throws Exception {
		WorkOrderStatus createdWorkOrderStatus = workOrderStatusService.createWorkOrderStatus(newWorkOrderStatus, loginUserID);
		return new ResponseEntity<>(createdWorkOrderStatus, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Update WorkOrderStatus") // label for swagger
	@PatchMapping("/{workOrderStatusId}")
	public ResponseEntity<?> patchWorkOrderStatus(@PathVariable String workOrderStatusId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateWorkOrderStatus updateWorkOrderStatus)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderStatus updatedWorkOrderStatus = workOrderStatusService.updateWorkOrderStatus(workOrderStatusId, loginUserID,
				updateWorkOrderStatus);
		return new ResponseEntity<>(updatedWorkOrderStatus, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Delete WorkOrderStatus") // label for swagger
	@DeleteMapping("/{workOrderStatusId}")
	public ResponseEntity<?> deleteWorkOrderStatus(@PathVariable String workOrderStatusId, @RequestParam String loginUserID) {
		workOrderStatusService.deleteWorkOrderStatus(workOrderStatusId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
