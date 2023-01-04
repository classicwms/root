package com.ustorage.api.trans.controller;

import com.ustorage.api.trans.model.handlingcharge.*;

import com.ustorage.api.trans.service.HandlingChargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "HandlingCharge" }, value = "HandlingCharge Operations related to HandlingChargeController") 
@SwaggerDefinition(tags = { @Tag(name = "HandlingCharge", description = "Operations related to HandlingCharge") })
@RequestMapping("/handlingCharge")
@RestController
public class HandlingChargeController {

	@Autowired
	HandlingChargeService handlingChargeService;

	@ApiOperation(response = HandlingCharge.class, value = "Get all HandlingCharge details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HandlingCharge> handlingChargeList = handlingChargeService.getHandlingCharge();
		return new ResponseEntity<>(handlingChargeList, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Get a HandlingCharge") // label for swagger
	@GetMapping("/{handlingChargeId}")
	public ResponseEntity<?> getHandlingCharge(@PathVariable String handlingChargeId) {
		HandlingCharge dbHandlingCharge = handlingChargeService.getHandlingCharge(handlingChargeId);
		log.info("HandlingCharge : " + dbHandlingCharge);
		return new ResponseEntity<>(dbHandlingCharge, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Create HandlingCharge") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHandlingCharge(@Valid @RequestBody AddHandlingCharge newHandlingCharge,
			@RequestParam String loginUserID) throws Exception {
		HandlingCharge createdHandlingCharge = handlingChargeService.createHandlingCharge(newHandlingCharge, loginUserID);
		return new ResponseEntity<>(createdHandlingCharge, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Update HandlingCharge") // label for swagger
	@PatchMapping("/{handlingCharge}")
	public ResponseEntity<?> patchHandlingCharge(@PathVariable String handlingCharge,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateHandlingCharge updateHandlingCharge)
			throws IllegalAccessException, InvocationTargetException {
		HandlingCharge updatedHandlingCharge = handlingChargeService.updateHandlingCharge(handlingCharge, loginUserID,
				updateHandlingCharge);
		return new ResponseEntity<>(updatedHandlingCharge, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Delete HandlingCharge") // label for swagger
	@DeleteMapping("/{handlingCharge}")
	public ResponseEntity<?> deleteHandlingCharge(@PathVariable String handlingCharge, @RequestParam String loginUserID) {
		handlingChargeService.deleteHandlingCharge(handlingCharge, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = HandlingCharge.class, value = "Find HandlingCharge") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findHandlingCharge(@Valid @RequestBody FindHandlingCharge findHandlingCharge) throws Exception {
		List<HandlingCharge> createdHandlingCharge = handlingChargeService.findHandlingCharge(findHandlingCharge);
		return new ResponseEntity<>(createdHandlingCharge, HttpStatus.OK);
	}
}
