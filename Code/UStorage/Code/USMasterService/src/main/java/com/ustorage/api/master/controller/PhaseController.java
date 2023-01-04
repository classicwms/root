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

import com.ustorage.api.master.model.phase.AddPhase;
import com.ustorage.api.master.model.phase.Phase;
import com.ustorage.api.master.model.phase.UpdatePhase;
import com.ustorage.api.master.service.PhaseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Phase" }, value = "Phase Operations related to PhaseController") 
@SwaggerDefinition(tags = { @Tag(name = "Phase", description = "Operations related to Phase") })
@RequestMapping("/phase")
@RestController
public class PhaseController {

	@Autowired
	PhaseService phaseService;

	@ApiOperation(response = Phase.class, value = "Get all Phase details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Phase> phaseList = phaseService.getPhase();
		return new ResponseEntity<>(phaseList, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Get a Phase") // label for swagger
	@GetMapping("/{phaseId}")
	public ResponseEntity<?> getPhase(@PathVariable String phaseId) {
		Phase dbPhase = phaseService.getPhase(phaseId);
		log.info("Phase : " + dbPhase);
		return new ResponseEntity<>(dbPhase, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Create Phase") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPhase(@Valid @RequestBody AddPhase newPhase,
			@RequestParam String loginUserID) throws Exception {
		Phase createdPhase = phaseService.createPhase(newPhase, loginUserID);
		return new ResponseEntity<>(createdPhase, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Update Phase") // label for swagger
	@PatchMapping("/{phaseId}")
	public ResponseEntity<?> patchPhase(@PathVariable String phaseId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdatePhase updatePhase)
			throws IllegalAccessException, InvocationTargetException {
		Phase updatedPhase = phaseService.updatePhase(phaseId, loginUserID,
				updatePhase);
		return new ResponseEntity<>(updatedPhase, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Delete Phase") // label for swagger
	@DeleteMapping("/{phaseId}")
	public ResponseEntity<?> deletePhase(@PathVariable String phaseId, @RequestParam String loginUserID) {
		phaseService.deletePhase(phaseId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
