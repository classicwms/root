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

import com.ustorage.api.master.model.paymenttype.AddPaymentType;
import com.ustorage.api.master.model.paymenttype.PaymentType;
import com.ustorage.api.master.model.paymenttype.UpdatePaymentType;
import com.ustorage.api.master.service.PaymentTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "PaymentType" }, value = "PaymentType Operations related to PaymentTypeController") 
@SwaggerDefinition(tags = { @Tag(name = "PaymentType", description = "Operations related to PaymentType") })
@RequestMapping("/paymentType")
@RestController
public class PaymentTypeController {

	@Autowired
	PaymentTypeService paymentTypeService;

	@ApiOperation(response = PaymentType.class, value = "Get all PaymentType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PaymentType> paymentTypeList = paymentTypeService.getPaymentType();
		return new ResponseEntity<>(paymentTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Get a PaymentType") // label for swagger
	@GetMapping("/{paymentTypeId}")
	public ResponseEntity<?> getPaymentType(@PathVariable String paymentTypeId) {
		PaymentType dbPaymentType = paymentTypeService.getPaymentType(paymentTypeId);
		log.info("PaymentType : " + dbPaymentType);
		return new ResponseEntity<>(dbPaymentType, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Create PaymentType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPaymentType(@Valid @RequestBody AddPaymentType newPaymentType,
			@RequestParam String loginUserID) throws Exception {
		PaymentType createdPaymentType = paymentTypeService.createPaymentType(newPaymentType, loginUserID);
		return new ResponseEntity<>(createdPaymentType, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Update PaymentType") // label for swagger
	@PatchMapping("/{paymentTypeId}")
	public ResponseEntity<?> patchPaymentType(@PathVariable String paymentTypeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdatePaymentType updatePaymentType)
			throws IllegalAccessException, InvocationTargetException {
		PaymentType updatedPaymentType = paymentTypeService.updatePaymentType(paymentTypeId, loginUserID,
				updatePaymentType);
		return new ResponseEntity<>(updatedPaymentType, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Delete PaymentType") // label for swagger
	@DeleteMapping("/{paymentTypeId}")
	public ResponseEntity<?> deletePaymentType(@PathVariable String paymentTypeId, @RequestParam String loginUserID) {
		paymentTypeService.deletePaymentType(paymentTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
