package com.ustorage.api.trans.controller;

import com.ustorage.api.trans.model.consumablepurchase.*;
import com.ustorage.api.trans.service.ConsumablePurchaseService;
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
@Api(tags = { "ConsumablePurchase" }, value = "ConsumablePurchase Operations related to ConsumablePurchaseController") 
@SwaggerDefinition(tags = { @Tag(name = "ConsumablePurchase", description = "Operations related to ConsumablePurchase") })
@RequestMapping("/consumablePurchase")
@RestController
public class ConsumablePurchaseController {

	@Autowired
	ConsumablePurchaseService consumablePurchaseService;

	@ApiOperation(response = ConsumablePurchase.class, value = "Get all ConsumablePurchase details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ConsumablePurchase> consumablePurchaseList = consumablePurchaseService.getConsumablePurchase();
		return new ResponseEntity<>(consumablePurchaseList, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Get a ConsumablePurchase") // label for swagger
	@GetMapping("/{itemCode}")
	public ResponseEntity<?> getConsumablePurchase(@PathVariable String itemCode) {
		List<ConsumablePurchase> dbConsumablePurchase = consumablePurchaseService.getConsumablePurchase(itemCode);
		log.info("ConsumablePurchase : " + dbConsumablePurchase);
		return new ResponseEntity<>(dbConsumablePurchase, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Create ConsumablePurchase") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postConsumablePurchase(@Valid @RequestBody List<AddConsumablePurchase> newConsumablePurchase,
			@RequestParam String loginUserID) throws Exception {
		List<ConsumablePurchase> createdConsumablePurchase = consumablePurchaseService.createConsumablePurchase(newConsumablePurchase, loginUserID);
		return new ResponseEntity<>(createdConsumablePurchase, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Update ConsumablePurchase") // label for swagger
	@PatchMapping("")
	public ResponseEntity<?> patchConsumablePurchase(@RequestBody List<UpdateConsumablePurchase> updateConsumablePurchase,
													 @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<ConsumablePurchase> createdConsumablePurchase = consumablePurchaseService.updateConsumablePurchase(updateConsumablePurchase, loginUserID);
		return new ResponseEntity<>(createdConsumablePurchase, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Delete ConsumablePurchase") // label for swagger
	@DeleteMapping("/{consumablePurchase}")
	public ResponseEntity<?> deleteConsumablePurchase(@PathVariable String consumablePurchase, @RequestParam String loginUserID) {
		consumablePurchaseService.deleteConsumablePurchase(consumablePurchase, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
