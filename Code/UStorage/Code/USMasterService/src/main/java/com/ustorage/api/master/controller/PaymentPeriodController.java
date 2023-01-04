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

import com.ustorage.api.master.model.paymentperiod.AddPaymentPeriod;
import com.ustorage.api.master.model.paymentperiod.PaymentPeriod;
import com.ustorage.api.master.model.paymentperiod.UpdatePaymentPeriod;
import com.ustorage.api.master.service.PaymentPeriodService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "PaymentPeriod" }, value = "PaymentPeriod Operations related to PaymentPeriodController") 
@SwaggerDefinition(tags = { @Tag(name = "PaymentPeriod", description = "Operations related to PaymentPeriod") })
@RequestMapping("/paymentPeriod")
@RestController
public class PaymentPeriodController {

	@Autowired
	PaymentPeriodService paymentPeriodService;

	@ApiOperation(response = PaymentPeriod.class, value = "Get all PaymentPeriod details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PaymentPeriod> paymentPeriodList = paymentPeriodService.getPaymentPeriod();
		return new ResponseEntity<>(paymentPeriodList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Get a PaymentPeriod") // label for swagger
	@GetMapping("/{paymentPeriodId}")
	public ResponseEntity<?> getPaymentPeriod(@PathVariable String paymentPeriodId) {
		PaymentPeriod dbPaymentPeriod = paymentPeriodService.getPaymentPeriod(paymentPeriodId);
		log.info("PaymentPeriod : " + dbPaymentPeriod);
		return new ResponseEntity<>(dbPaymentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Create PaymentPeriod") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPaymentPeriod(@Valid @RequestBody AddPaymentPeriod newPaymentPeriod,
			@RequestParam String loginUserID) throws Exception {
		PaymentPeriod createdPaymentPeriod = paymentPeriodService.createPaymentPeriod(newPaymentPeriod, loginUserID);
		return new ResponseEntity<>(createdPaymentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Update PaymentPeriod") // label for swagger
	@PatchMapping("/{paymentPeriodId}")
	public ResponseEntity<?> patchPaymentPeriod(@PathVariable String paymentPeriodId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdatePaymentPeriod updatePaymentPeriod)
			throws IllegalAccessException, InvocationTargetException {
		PaymentPeriod updatedPaymentPeriod = paymentPeriodService.updatePaymentPeriod(paymentPeriodId, loginUserID,
				updatePaymentPeriod);
		return new ResponseEntity<>(updatedPaymentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Delete PaymentPeriod") // label for swagger
	@DeleteMapping("/{paymentPeriodId}")
	public ResponseEntity<?> deletePaymentPeriod(@PathVariable String paymentPeriodId, @RequestParam String loginUserID) {
		paymentPeriodService.deletePaymentPeriod(paymentPeriodId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
