package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.idmaster.model.state.AddState;
import com.tekclover.wms.api.idmaster.model.state.State;
import com.tekclover.wms.api.idmaster.model.state.UpdateState;
import com.tekclover.wms.api.idmaster.service.StateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"State"}, value = "State Operations related to StateController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "State",description = "Operations related to State")})
@RequestMapping("/state")
@RestController
public class StateController {
	
	@Autowired
	StateService stateService;
	
    @ApiOperation(response = State.class, value = "Get all State details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<State> stateList = stateService.getCompanies();
		return new ResponseEntity<>(stateList, HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Get a State") // label for swagger 
	@GetMapping("/{stateId}")
	public ResponseEntity<?> getState(@PathVariable String stateId) {
    	State state = stateService.getState(stateId);
    	log.info("State : " + state);
		return new ResponseEntity<>(state, HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Create State") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addState(@Valid @RequestBody AddState newState) 
			throws IllegalAccessException, InvocationTargetException {
		State createdState = stateService.createState(newState);
		return new ResponseEntity<>(createdState , HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Update State") // label for swagger
    @PatchMapping("/{stateId}")
	public ResponseEntity<?> patchState(@PathVariable String stateId, 
			@Valid @RequestBody UpdateState updateState) 
			throws IllegalAccessException, InvocationTargetException {
		State updatedState = stateService.updateState(stateId, updateState);
		return new ResponseEntity<>(updatedState , HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Delete State") // label for swagger
	@DeleteMapping("/{stateId}")
	public ResponseEntity<?> deleteState(@PathVariable String stateId) {
    	stateService.deleteState(stateId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}