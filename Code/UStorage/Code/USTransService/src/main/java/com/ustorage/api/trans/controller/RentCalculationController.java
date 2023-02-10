package com.ustorage.api.trans.controller;

import com.ustorage.api.trans.model.reports.RentCalculationInput;
import com.ustorage.api.trans.service.RentCalculationService;

import com.ustorage.api.trans.util.Rent;
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
@Api(tags = { "RentCalculation" }, value = "RentCalculation Operations related to RentCalculationController") 
@SwaggerDefinition(tags = { @Tag(name = "RentCalculation", description = "Operations related to RentCalculation") })
@RequestMapping("/rentCalculation")
@RestController
public class RentCalculationController {

	@Autowired
	RentCalculationService rentCalculationService;

	@ApiOperation(response = Rent.class, value = "Create RentCalculation") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRentCalculation(@Valid @RequestBody RentCalculationInput newRentCalculationInput) throws Exception {
		Rent createdRentCalculation = rentCalculationService.createRentCalculation(newRentCalculationInput);
		return new ResponseEntity<>(createdRentCalculation, HttpStatus.OK);
	}
}
