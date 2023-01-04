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

import com.ustorage.api.master.model.modeofpayment.AddModeOfPayment;
import com.ustorage.api.master.model.modeofpayment.ModeOfPayment;
import com.ustorage.api.master.model.modeofpayment.UpdateModeOfPayment;
import com.ustorage.api.master.service.ModeOfPaymentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "ModeOfPayment" }, value = "ModeOfPayment Operations related to ModeOfPaymentController") 
@SwaggerDefinition(tags = { @Tag(name = "ModeOfPayment", description = "Operations related to ModeOfPayment") })
@RequestMapping("/modeOfPayment")
@RestController
public class ModeOfPaymentController {

	@Autowired
	ModeOfPaymentService modeOfPaymentService;

	@ApiOperation(response = ModeOfPayment.class, value = "Get all ModeOfPayment details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ModeOfPayment> modeOfPaymentList = modeOfPaymentService.getModeOfPayment();
		return new ResponseEntity<>(modeOfPaymentList, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Get a ModeOfPayment") // label for swagger
	@GetMapping("/{modeOfPaymentId}")
	public ResponseEntity<?> getModeOfPayment(@PathVariable String modeOfPaymentId) {
		ModeOfPayment dbModeOfPayment = modeOfPaymentService.getModeOfPayment(modeOfPaymentId);
		log.info("ModeOfPayment : " + dbModeOfPayment);
		return new ResponseEntity<>(dbModeOfPayment, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Create ModeOfPayment") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postModeOfPayment(@Valid @RequestBody AddModeOfPayment newModeOfPayment,
			@RequestParam String loginUserID) throws Exception {
		ModeOfPayment createdModeOfPayment = modeOfPaymentService.createModeOfPayment(newModeOfPayment, loginUserID);
		return new ResponseEntity<>(createdModeOfPayment, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Update ModeOfPayment") // label for swagger
	@PatchMapping("/{modeOfPaymentId}")
	public ResponseEntity<?> patchModeOfPayment(@PathVariable String modeOfPaymentId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateModeOfPayment updateModeOfPayment)
			throws IllegalAccessException, InvocationTargetException {
		ModeOfPayment updatedModeOfPayment = modeOfPaymentService.updateModeOfPayment(modeOfPaymentId, loginUserID,
				updateModeOfPayment);
		return new ResponseEntity<>(updatedModeOfPayment, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Delete ModeOfPayment") // label for swagger
	@DeleteMapping("/{modeOfPaymentId}")
	public ResponseEntity<?> deleteModeOfPayment(@PathVariable String modeOfPaymentId, @RequestParam String loginUserID) {
		modeOfPaymentService.deleteModeOfPayment(modeOfPaymentId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
