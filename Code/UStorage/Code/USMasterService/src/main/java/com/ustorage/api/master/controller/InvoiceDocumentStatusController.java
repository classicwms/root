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

import com.ustorage.api.master.model.invoicedocumentstatus.AddInvoiceDocumentStatus;
import com.ustorage.api.master.model.invoicedocumentstatus.InvoiceDocumentStatus;
import com.ustorage.api.master.model.invoicedocumentstatus.UpdateInvoiceDocumentStatus;
import com.ustorage.api.master.service.InvoiceDocumentStatusService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "InvoiceDocumentStatus" }, value = "InvoiceDocumentStatus Operations related to InvoiceDocumentStatusController") 
@SwaggerDefinition(tags = { @Tag(name = "InvoiceDocumentStatus", description = "Operations related to InvoiceDocumentStatus") })
@RequestMapping("/invoiceDocumentStatus")
@RestController
public class InvoiceDocumentStatusController {

	@Autowired
	InvoiceDocumentStatusService invoiceDocumentStatusService;

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Get all InvoiceDocumentStatus details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<InvoiceDocumentStatus> invoiceDocumentStatusList = invoiceDocumentStatusService.getInvoiceDocumentStatus();
		return new ResponseEntity<>(invoiceDocumentStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Get a InvoiceDocumentStatus") // label for swagger
	@GetMapping("/{invoiceDocumentStatusId}")
	public ResponseEntity<?> getInvoiceDocumentStatus(@PathVariable String invoiceDocumentStatusId) {
		InvoiceDocumentStatus dbInvoiceDocumentStatus = invoiceDocumentStatusService.getInvoiceDocumentStatus(invoiceDocumentStatusId);
		log.info("InvoiceDocumentStatus : " + dbInvoiceDocumentStatus);
		return new ResponseEntity<>(dbInvoiceDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Create InvoiceDocumentStatus") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postInvoiceDocumentStatus(@Valid @RequestBody AddInvoiceDocumentStatus newInvoiceDocumentStatus,
			@RequestParam String loginUserID) throws Exception {
		InvoiceDocumentStatus createdInvoiceDocumentStatus = invoiceDocumentStatusService.createInvoiceDocumentStatus(newInvoiceDocumentStatus, loginUserID);
		return new ResponseEntity<>(createdInvoiceDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Update InvoiceDocumentStatus") // label for swagger
	@PatchMapping("/{invoiceDocumentStatusId}")
	public ResponseEntity<?> patchInvoiceDocumentStatus(@PathVariable String invoiceDocumentStatusId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateInvoiceDocumentStatus updateInvoiceDocumentStatus)
			throws IllegalAccessException, InvocationTargetException {
		InvoiceDocumentStatus updatedInvoiceDocumentStatus = invoiceDocumentStatusService.updateInvoiceDocumentStatus(invoiceDocumentStatusId, loginUserID,
				updateInvoiceDocumentStatus);
		return new ResponseEntity<>(updatedInvoiceDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Delete InvoiceDocumentStatus") // label for swagger
	@DeleteMapping("/{invoiceDocumentStatusId}")
	public ResponseEntity<?> deleteInvoiceDocumentStatus(@PathVariable String invoiceDocumentStatusId, @RequestParam String loginUserID) {
		invoiceDocumentStatusService.deleteInvoiceDocumentStatus(invoiceDocumentStatusId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
