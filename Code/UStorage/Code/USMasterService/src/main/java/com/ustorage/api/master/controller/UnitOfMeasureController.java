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

import com.ustorage.api.master.model.unitofmeasure.AddUnitOfMeasure;
import com.ustorage.api.master.model.unitofmeasure.UnitOfMeasure;
import com.ustorage.api.master.model.unitofmeasure.UpdateUnitOfMeasure;
import com.ustorage.api.master.service.UnitOfMeasureService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "UnitOfMeasure" }, value = "UnitOfMeasure Operations related to UnitOfMeasureController") 
@SwaggerDefinition(tags = { @Tag(name = "UnitOfMeasure", description = "Operations related to UnitOfMeasure") })
@RequestMapping("/unitOfMeasure")
@RestController
public class UnitOfMeasureController {

	@Autowired
	UnitOfMeasureService unitOfMeasureService;

	@ApiOperation(response = UnitOfMeasure.class, value = "Get all UnitOfMeasure details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<UnitOfMeasure> unitOfMeasureList = unitOfMeasureService.getUnitOfMeasure();
		return new ResponseEntity<>(unitOfMeasureList, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Get a UnitOfMeasure") // label for swagger
	@GetMapping("/{unitOfMeasureId}")
	public ResponseEntity<?> getUnitOfMeasure(@PathVariable String unitOfMeasureId) {
		UnitOfMeasure dbUnitOfMeasure = unitOfMeasureService.getUnitOfMeasure(unitOfMeasureId);
		log.info("UnitOfMeasure : " + dbUnitOfMeasure);
		return new ResponseEntity<>(dbUnitOfMeasure, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Create UnitOfMeasure") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postUnitOfMeasure(@Valid @RequestBody AddUnitOfMeasure newUnitOfMeasure,
			@RequestParam String loginUserID) throws Exception {
		UnitOfMeasure createdUnitOfMeasure = unitOfMeasureService.createUnitOfMeasure(newUnitOfMeasure, loginUserID);
		return new ResponseEntity<>(createdUnitOfMeasure, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Update UnitOfMeasure") // label for swagger
	@PatchMapping("/{unitOfMeasureId}")
	public ResponseEntity<?> patchUnitOfMeasure(@PathVariable String unitOfMeasureId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateUnitOfMeasure updateUnitOfMeasure)
			throws IllegalAccessException, InvocationTargetException {
		UnitOfMeasure updatedUnitOfMeasure = unitOfMeasureService.updateUnitOfMeasure(unitOfMeasureId, loginUserID,
				updateUnitOfMeasure);
		return new ResponseEntity<>(updatedUnitOfMeasure, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Delete UnitOfMeasure") // label for swagger
	@DeleteMapping("/{unitOfMeasureId}")
	public ResponseEntity<?> deleteUnitOfMeasure(@PathVariable String unitOfMeasureId, @RequestParam String loginUserID) {
		unitOfMeasureService.deleteUnitOfMeasure(unitOfMeasureId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
