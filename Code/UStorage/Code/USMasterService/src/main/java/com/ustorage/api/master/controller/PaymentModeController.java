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

import com.ustorage.api.master.model.paymentmode.AddPaymentMode;
import com.ustorage.api.master.model.paymentmode.PaymentMode;
import com.ustorage.api.master.model.paymentmode.UpdatePaymentMode;
import com.ustorage.api.master.service.PaymentModeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "PaymentMode" }, value = "PaymentMode Operations related to PaymentModeController") 
@SwaggerDefinition(tags = { @Tag(name = "PaymentMode", description = "Operations related to PaymentMode") })
@RequestMapping("/paymentMode")
@RestController
public class PaymentModeController {

	@Autowired
	PaymentModeService paymentModeService;

	@ApiOperation(response = PaymentMode.class, value = "Get all PaymentMode details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PaymentMode> paymentModeList = paymentModeService.getPaymentMode();
		return new ResponseEntity<>(paymentModeList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Get a PaymentMode") // label for swagger
	@GetMapping("/{paymentModeId}")
	public ResponseEntity<?> getPaymentMode(@PathVariable String paymentModeId) {
		PaymentMode dbPaymentMode = paymentModeService.getPaymentMode(paymentModeId);
		log.info("PaymentMode : " + dbPaymentMode);
		return new ResponseEntity<>(dbPaymentMode, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Create PaymentMode") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPaymentMode(@Valid @RequestBody AddPaymentMode newPaymentMode,
			@RequestParam String loginUserID) throws Exception {
		PaymentMode createdPaymentMode = paymentModeService.createPaymentMode(newPaymentMode, loginUserID);
		return new ResponseEntity<>(createdPaymentMode, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Update PaymentMode") // label for swagger
	@PatchMapping("/{paymentModeId}")
	public ResponseEntity<?> patchPaymentMode(@PathVariable String paymentModeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdatePaymentMode updatePaymentMode)
			throws IllegalAccessException, InvocationTargetException {
		PaymentMode updatedPaymentMode = paymentModeService.updatePaymentMode(paymentModeId, loginUserID,
				updatePaymentMode);
		return new ResponseEntity<>(updatedPaymentMode, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Delete PaymentMode") // label for swagger
	@DeleteMapping("/{paymentModeId}")
	public ResponseEntity<?> deletePaymentMode(@PathVariable String paymentModeId, @RequestParam String loginUserID) {
		paymentModeService.deletePaymentMode(paymentModeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
