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

import com.ustorage.api.master.model.rentperiod.AddRentPeriod;
import com.ustorage.api.master.model.rentperiod.RentPeriod;
import com.ustorage.api.master.model.rentperiod.UpdateRentPeriod;
import com.ustorage.api.master.service.RentPeriodService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "RentPeriod" }, value = "RentPeriod Operations related to RentPeriodController") 
@SwaggerDefinition(tags = { @Tag(name = "RentPeriod", description = "Operations related to RentPeriod") })
@RequestMapping("/rentPeriod")
@RestController
public class RentPeriodController {

	@Autowired
	RentPeriodService rentPeriodService;

	@ApiOperation(response = RentPeriod.class, value = "Get all RentPeriod details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<RentPeriod> rentPeriodList = rentPeriodService.getRentPeriod();
		return new ResponseEntity<>(rentPeriodList, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Get a RentPeriod") // label for swagger
	@GetMapping("/{rentPeriodId}")
	public ResponseEntity<?> getRentPeriod(@PathVariable String rentPeriodId) {
		RentPeriod dbRentPeriod = rentPeriodService.getRentPeriod(rentPeriodId);
		log.info("RentPeriod : " + dbRentPeriod);
		return new ResponseEntity<>(dbRentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Create RentPeriod") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRentPeriod(@Valid @RequestBody AddRentPeriod newRentPeriod,
			@RequestParam String loginUserID) throws Exception {
		RentPeriod createdRentPeriod = rentPeriodService.createRentPeriod(newRentPeriod, loginUserID);
		return new ResponseEntity<>(createdRentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Update RentPeriod") // label for swagger
	@PatchMapping("/{rentPeriodId}")
	public ResponseEntity<?> patchRentPeriod(@PathVariable String rentPeriodId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateRentPeriod updateRentPeriod)
			throws IllegalAccessException, InvocationTargetException {
		RentPeriod updatedRentPeriod = rentPeriodService.updateRentPeriod(rentPeriodId, loginUserID,
				updateRentPeriod);
		return new ResponseEntity<>(updatedRentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Delete RentPeriod") // label for swagger
	@DeleteMapping("/{rentPeriodId}")
	public ResponseEntity<?> deleteRentPeriod(@PathVariable String rentPeriodId, @RequestParam String loginUserID) {
		rentPeriodService.deleteRentPeriod(rentPeriodId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
