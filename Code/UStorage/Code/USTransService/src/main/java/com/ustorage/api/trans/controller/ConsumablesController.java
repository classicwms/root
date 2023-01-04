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

import com.ustorage.api.trans.model.consumables.*;

import com.ustorage.api.trans.service.ConsumablesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Consumables" }, value = "Consumables Operations related to ConsumablesController") 
@SwaggerDefinition(tags = { @Tag(name = "Consumables", description = "Operations related to Consumables") })
@RequestMapping("/consumables")
@RestController
public class ConsumablesController {

	@Autowired
	ConsumablesService consumablesService;

	@ApiOperation(response = Consumables.class, value = "Get all Consumables details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Consumables> consumablesList = consumablesService.getConsumables();
		return new ResponseEntity<>(consumablesList, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Get a Consumables") // label for swagger
	@GetMapping("/{consumablesId}")
	public ResponseEntity<?> getConsumables(@PathVariable String consumablesId) {
		Consumables dbConsumables = consumablesService.getConsumables(consumablesId);
		log.info("Consumables : " + dbConsumables);
		return new ResponseEntity<>(dbConsumables, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Create Consumables") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postConsumables(@Valid @RequestBody AddConsumables newConsumables,
			@RequestParam String loginUserID) throws Exception {
		Consumables createdConsumables = consumablesService.createConsumables(newConsumables, loginUserID);
		return new ResponseEntity<>(createdConsumables, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Update Consumables") // label for swagger
	@PatchMapping("/{consumables}")
	public ResponseEntity<?> patchConsumables(@PathVariable String consumables,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateConsumables updateConsumables)
			throws IllegalAccessException, InvocationTargetException {
		Consumables updatedConsumables = consumablesService.updateConsumables(consumables, loginUserID,
				updateConsumables);
		return new ResponseEntity<>(updatedConsumables, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Delete Consumables") // label for swagger
	@DeleteMapping("/{consumables}")
	public ResponseEntity<?> deleteConsumables(@PathVariable String consumables, @RequestParam String loginUserID) {
		consumablesService.deleteConsumables(consumables, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(response = Consumables.class, value = "Find Consumables") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findConsumables(@Valid @RequestBody FindConsumables findConsumables) throws Exception {
		List<Consumables> createdConsumables = consumablesService.findConsumables(findConsumables);
		return new ResponseEntity<>(createdConsumables, HttpStatus.OK);
	}
}
