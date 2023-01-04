package com.ustorage.api.trans.controller;

import com.ustorage.api.trans.model.flrent.*;

import com.ustorage.api.trans.service.FlRentService;
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
@Api(tags = { "FlRent" }, value = "FlRent Operations related to FlRentController") 
@SwaggerDefinition(tags = { @Tag(name = "FlRent", description = "Operations related to FlRent") })
@RequestMapping("/flRent")
@RestController
public class FlRentController {

	@Autowired
	FlRentService flRentService;

	@ApiOperation(response = FlRent.class, value = "Get all FlRent details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<FlRent> flRentList = flRentService.getFlRent();
		return new ResponseEntity<>(flRentList, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Get a FlRent") // label for swagger
	@GetMapping("/{flRentId}")
	public ResponseEntity<?> getFlRent(@PathVariable String flRentId) {
		FlRent dbFlRent = flRentService.getFlRent(flRentId);
		log.info("FlRent : " + dbFlRent);
		return new ResponseEntity<>(dbFlRent, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Create FlRent") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postFlRent(@Valid @RequestBody AddFlRent newFlRent,
			@RequestParam String loginUserID) throws Exception {
		FlRent createdFlRent = flRentService.createFlRent(newFlRent, loginUserID);
		return new ResponseEntity<>(createdFlRent, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Update FlRent") // label for swagger
	@PatchMapping("/{flRent}")
	public ResponseEntity<?> patchFlRent(@PathVariable String flRent,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateFlRent updateFlRent)
			throws IllegalAccessException, InvocationTargetException {
		FlRent updatedFlRent = flRentService.updateFlRent(flRent, loginUserID,
				updateFlRent);
		return new ResponseEntity<>(updatedFlRent, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Delete FlRent") // label for swagger
	@DeleteMapping("/{flRent}")
	public ResponseEntity<?> deleteFlRent(@PathVariable String flRent, @RequestParam String loginUserID) {
		flRentService.deleteFlRent(flRent, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = FlRent.class, value = "Find FlRent") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findFlRent(@Valid @RequestBody FindFlRent findFlRent) throws Exception {
		List<FlRent> createdFlRent = flRentService.findFlRent(findFlRent);
		return new ResponseEntity<>(createdFlRent, HttpStatus.OK);
	}
}
