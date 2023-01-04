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

import com.ustorage.api.master.model.invoicecurrency.AddInvoiceCurrency;
import com.ustorage.api.master.model.invoicecurrency.InvoiceCurrency;
import com.ustorage.api.master.model.invoicecurrency.UpdateInvoiceCurrency;
import com.ustorage.api.master.service.InvoiceCurrencyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "InvoiceCurrency" }, value = "InvoiceCurrency Operations related to InvoiceCurrencyController") 
@SwaggerDefinition(tags = { @Tag(name = "InvoiceCurrency", description = "Operations related to InvoiceCurrency") })
@RequestMapping("/invoiceCurrency")
@RestController
public class InvoiceCurrencyController {

	@Autowired
	InvoiceCurrencyService invoiceCurrencyService;

	@ApiOperation(response = InvoiceCurrency.class, value = "Get all InvoiceCurrency details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<InvoiceCurrency> invoiceCurrencyList = invoiceCurrencyService.getInvoiceCurrency();
		return new ResponseEntity<>(invoiceCurrencyList, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Get a InvoiceCurrency") // label for swagger
	@GetMapping("/{invoiceCurrencyId}")
	public ResponseEntity<?> getInvoiceCurrency(@PathVariable String invoiceCurrencyId) {
		InvoiceCurrency dbInvoiceCurrency = invoiceCurrencyService.getInvoiceCurrency(invoiceCurrencyId);
		log.info("InvoiceCurrency : " + dbInvoiceCurrency);
		return new ResponseEntity<>(dbInvoiceCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Create InvoiceCurrency") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postInvoiceCurrency(@Valid @RequestBody AddInvoiceCurrency newInvoiceCurrency,
			@RequestParam String loginUserID) throws Exception {
		InvoiceCurrency createdInvoiceCurrency = invoiceCurrencyService.createInvoiceCurrency(newInvoiceCurrency, loginUserID);
		return new ResponseEntity<>(createdInvoiceCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Update InvoiceCurrency") // label for swagger
	@PatchMapping("/{invoiceCurrencyId}")
	public ResponseEntity<?> patchInvoiceCurrency(@PathVariable String invoiceCurrencyId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateInvoiceCurrency updateInvoiceCurrency)
			throws IllegalAccessException, InvocationTargetException {
		InvoiceCurrency updatedInvoiceCurrency = invoiceCurrencyService.updateInvoiceCurrency(invoiceCurrencyId, loginUserID,
				updateInvoiceCurrency);
		return new ResponseEntity<>(updatedInvoiceCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Delete InvoiceCurrency") // label for swagger
	@DeleteMapping("/{invoiceCurrencyId}")
	public ResponseEntity<?> deleteInvoiceCurrency(@PathVariable String invoiceCurrencyId, @RequestParam String loginUserID) {
		invoiceCurrencyService.deleteInvoiceCurrency(invoiceCurrencyId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
