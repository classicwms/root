package com.ustorage.api.master.controller;

import com.ustorage.api.master.model.sbu.*;

import com.ustorage.api.master.service.SbuService;
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
@Api(tags = { "Sbu" }, value = "Sbu Operations related to SbuController") 
@SwaggerDefinition(tags = { @Tag(name = "Sbu", description = "Operations related to Sbu") })
@RequestMapping("/sbu")
@RestController
public class SbuController {

	@Autowired
	SbuService sbuService;

	@ApiOperation(response = Sbu.class, value = "Get all Sbu details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Sbu> sbuList = sbuService.getSbu();
		return new ResponseEntity<>(sbuList, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Get a Sbu") // label for swagger
	@GetMapping("/{sbuId}")
	public ResponseEntity<?> getSbu(@PathVariable String sbuId) {
		Sbu dbSbu = sbuService.getSbu(sbuId);
		log.info("Sbu : " + dbSbu);
		return new ResponseEntity<>(dbSbu, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Create Sbu") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postSbu(@Valid @RequestBody AddSbu newSbu,
			@RequestParam String loginUserID) throws Exception {
		Sbu createdSbu = sbuService.createSbu(newSbu, loginUserID);
		return new ResponseEntity<>(createdSbu, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Update Sbu") // label for swagger
	@PatchMapping("/{sbuId}")
	public ResponseEntity<?> patchSbu(@PathVariable String sbuId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateSbu updateSbu)
			throws IllegalAccessException, InvocationTargetException {
		Sbu updatedSbu = sbuService.updateSbu(sbuId, loginUserID,
				updateSbu);
		return new ResponseEntity<>(updatedSbu, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Delete Sbu") // label for swagger
	@DeleteMapping("/{sbuId}")
	public ResponseEntity<?> deleteSbu(@PathVariable String sbuId, @RequestParam String loginUserID) {
		sbuService.deleteSbu(sbuId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
