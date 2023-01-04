package com.ustorage.api.trans.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import com.ustorage.api.trans.model.invoice.*;

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

import com.ustorage.api.trans.service.InvoiceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Invoice" }, value = "Invoice Operations related to InvoiceController") 
@SwaggerDefinition(tags = { @Tag(name = "Invoice", description = "Operations related to Invoice") })
@RequestMapping("/invoice")
@RestController
public class InvoiceController {

	@Autowired
	InvoiceService invoiceService;

	@ApiOperation(response = Invoice.class, value = "Get all Invoice details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Invoice> invoiceList = invoiceService.getInvoice();
		return new ResponseEntity<>(invoiceList, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Get a Invoice") // label for swagger
	@GetMapping("/{invoiceId}")
	public ResponseEntity<?> getInvoice(@PathVariable String invoiceId) {
		Invoice dbInvoice = invoiceService.getInvoice(invoiceId);
		log.info("Invoice : " + dbInvoice);
		return new ResponseEntity<>(dbInvoice, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Create Invoice") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postInvoice(@Valid @RequestBody AddInvoice newInvoice,
			@RequestParam String loginUserID) throws Exception {
		Invoice createdInvoice = invoiceService.createInvoice(newInvoice, loginUserID);
		return new ResponseEntity<>(createdInvoice, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Update Invoice") // label for swagger
	@PatchMapping("/{invoice}")
	public ResponseEntity<?> patchInvoice(@PathVariable String invoice,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateInvoice updateInvoice)
			throws IllegalAccessException, InvocationTargetException {
		Invoice updatedInvoice = invoiceService.updateInvoice(invoice, loginUserID,
				updateInvoice);
		return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Delete Invoice") // label for swagger
	@DeleteMapping("/{invoice}")
	public ResponseEntity<?> deleteInvoice(@PathVariable String invoice, @RequestParam String loginUserID) {
		invoiceService.deleteInvoice(invoice, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Invoice.class, value = "Find Invoice") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findInvoice(@Valid @RequestBody FindInvoice findInvoice) throws Exception {
		List<Invoice> createdInvoice = invoiceService.findInvoice(findInvoice);
		return new ResponseEntity<>(createdInvoice, HttpStatus.OK);
	}
}
