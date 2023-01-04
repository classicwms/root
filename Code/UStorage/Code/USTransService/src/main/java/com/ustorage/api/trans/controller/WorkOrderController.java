package com.ustorage.api.trans.controller;

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

import com.ustorage.api.trans.model.workorder.*;

import com.ustorage.api.trans.service.WorkOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "WorkOrder" }, value = "WorkOrder Operations related to WorkOrderController") 
@SwaggerDefinition(tags = { @Tag(name = "WorkOrder", description = "Operations related to WorkOrder") })
@RequestMapping("/workOrder")
@RestController
public class WorkOrderController {

	@Autowired
	WorkOrderService workOrderService;

	@ApiOperation(response = WorkOrder.class, value = "Get all WorkOrder details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<WorkOrder> workOrderList = workOrderService.getWorkOrder();
		return new ResponseEntity<>(workOrderList, HttpStatus.OK);
	}

	@ApiOperation(response = GWorkOrder.class, value = "Get a WorkOrder") // label for swagger
	@GetMapping("/{workOrderId}")
	public ResponseEntity<?> getWorkOrder(@PathVariable String workOrderId) {
		GWorkOrder dbWorkOrder = workOrderService.getWorkOrder(workOrderId);
		log.info("WorkOrder : " + dbWorkOrder);
		return new ResponseEntity<>(dbWorkOrder, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrder.class, value = "Create WorkOrder") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postWorkOrder(@Valid @RequestBody AddWorkOrder newWorkOrder,
			@RequestParam String loginUserID) throws Exception {
		WorkOrder createdWorkOrder = workOrderService.createWorkOrder(newWorkOrder, loginUserID);
		return new ResponseEntity<>(createdWorkOrder, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrder.class, value = "Update WorkOrder") // label for swagger
	@PatchMapping("/{workOrder}")
	public ResponseEntity<?> patchWorkOrder(@PathVariable String workOrder,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateWorkOrder updateWorkOrder)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrder updatedWorkOrder = workOrderService.updateWorkOrder(workOrder, loginUserID,
				updateWorkOrder);
		return new ResponseEntity<>(updatedWorkOrder, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrder.class, value = "Delete WorkOrder") // label for swagger
	@DeleteMapping("/{workOrder}")
	public ResponseEntity<?> deleteWorkOrder(@PathVariable String workOrder, @RequestParam String loginUserID) {
		workOrderService.deleteWorkOrder(workOrder, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = GWorkOrder.class, value = "Find WorkOrder") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findWorkOrder(@Valid @RequestBody FindWorkOrder findWorkOrder) throws Exception {
		List<GWorkOrder> createdWorkOrder = workOrderService.findWorkOrder(findWorkOrder);
		return new ResponseEntity<>(createdWorkOrder, HttpStatus.OK);
	}
}
