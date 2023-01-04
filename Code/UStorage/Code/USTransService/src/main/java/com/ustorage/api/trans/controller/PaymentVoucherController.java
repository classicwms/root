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

import com.ustorage.api.trans.model.paymentvoucher.*;

import com.ustorage.api.trans.service.PaymentVoucherService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "PaymentVoucher" }, value = "PaymentVoucher Operations related to PaymentVoucherController") 
@SwaggerDefinition(tags = { @Tag(name = "PaymentVoucher", description = "Operations related to PaymentVoucher") })
@RequestMapping("/paymentVoucher")
@RestController
public class PaymentVoucherController {

	@Autowired
	PaymentVoucherService paymentVoucherService;

	@ApiOperation(response = PaymentVoucher.class, value = "Get all PaymentVoucher details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PaymentVoucher> paymentVoucherList = paymentVoucherService.getPaymentVoucher();
		return new ResponseEntity<>(paymentVoucherList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Get a PaymentVoucher") // label for swagger
	@GetMapping("/{paymentVoucherId}")
	public ResponseEntity<?> getPaymentVoucher(@PathVariable String paymentVoucherId) {
		PaymentVoucher dbPaymentVoucher = paymentVoucherService.getPaymentVoucher(paymentVoucherId);
		log.info("PaymentVoucher : " + dbPaymentVoucher);
		return new ResponseEntity<>(dbPaymentVoucher, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Create PaymentVoucher") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPaymentVoucher(@Valid @RequestBody AddPaymentVoucher newPaymentVoucher,
			@RequestParam String loginUserID) throws Exception {
		PaymentVoucher createdPaymentVoucher = paymentVoucherService.createPaymentVoucher(newPaymentVoucher, loginUserID);
		return new ResponseEntity<>(createdPaymentVoucher, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Update PaymentVoucher") // label for swagger
	@PatchMapping("/{paymentVoucher}")
	public ResponseEntity<?> patchPaymentVoucher(@PathVariable String paymentVoucher,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdatePaymentVoucher updatePaymentVoucher)
			throws IllegalAccessException, InvocationTargetException {
		PaymentVoucher updatedPaymentVoucher = paymentVoucherService.updatePaymentVoucher(paymentVoucher, loginUserID,
				updatePaymentVoucher);
		return new ResponseEntity<>(updatedPaymentVoucher, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Delete PaymentVoucher") // label for swagger
	@DeleteMapping("/{paymentVoucher}")
	public ResponseEntity<?> deletePaymentVoucher(@PathVariable String paymentVoucher, @RequestParam String loginUserID) {
		paymentVoucherService.deletePaymentVoucher(paymentVoucher, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = PaymentVoucher.class, value = "Find PaymentVoucher") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findPaymentVoucher(@Valid @RequestBody FindPaymentVoucher findPaymentVoucher) throws Exception {
		List<PaymentVoucher> createdPaymentVoucher = paymentVoucherService.findPaymentVoucher(findPaymentVoucher);
		return new ResponseEntity<>(createdPaymentVoucher, HttpStatus.OK);
	}
}
