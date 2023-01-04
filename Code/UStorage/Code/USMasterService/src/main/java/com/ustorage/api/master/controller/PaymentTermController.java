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

import com.ustorage.api.master.model.paymentterm.AddPaymentTerm;
import com.ustorage.api.master.model.paymentterm.PaymentTerm;
import com.ustorage.api.master.model.paymentterm.UpdatePaymentTerm;
import com.ustorage.api.master.service.PaymentTermService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "PaymentTerm" }, value = "PaymentTerm Operations related to PaymentTermController") 
@SwaggerDefinition(tags = { @Tag(name = "PaymentTerm", description = "Operations related to PaymentTerm") })
@RequestMapping("/paymentTerm")
@RestController
public class PaymentTermController {

	@Autowired
	PaymentTermService paymentTermService;

	@ApiOperation(response = PaymentTerm.class, value = "Get all PaymentTerm details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PaymentTerm> paymentTermList = paymentTermService.getPaymentTerm();
		return new ResponseEntity<>(paymentTermList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Get a PaymentTerm") // label for swagger
	@GetMapping("/{paymentTermId}")
	public ResponseEntity<?> getPaymentTerm(@PathVariable String paymentTermId) {
		PaymentTerm dbPaymentTerm = paymentTermService.getPaymentTerm(paymentTermId);
		log.info("PaymentTerm : " + dbPaymentTerm);
		return new ResponseEntity<>(dbPaymentTerm, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Create PaymentTerm") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPaymentTerm(@Valid @RequestBody AddPaymentTerm newPaymentTerm,
			@RequestParam String loginUserID) throws Exception {
		PaymentTerm createdPaymentTerm = paymentTermService.createPaymentTerm(newPaymentTerm, loginUserID);
		return new ResponseEntity<>(createdPaymentTerm, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Update PaymentTerm") // label for swagger
	@PatchMapping("/{paymentTermId}")
	public ResponseEntity<?> patchPaymentTerm(@PathVariable String paymentTermId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdatePaymentTerm updatePaymentTerm)
			throws IllegalAccessException, InvocationTargetException {
		PaymentTerm updatedPaymentTerm = paymentTermService.updatePaymentTerm(paymentTermId, loginUserID,
				updatePaymentTerm);
		return new ResponseEntity<>(updatedPaymentTerm, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Delete PaymentTerm") // label for swagger
	@DeleteMapping("/{paymentTermId}")
	public ResponseEntity<?> deletePaymentTerm(@PathVariable String paymentTermId, @RequestParam String loginUserID) {
		paymentTermService.deletePaymentTerm(paymentTermId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
