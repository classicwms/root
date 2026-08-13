package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.state.FindState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
		List<State> stateList = stateService.getStates();
		return new ResponseEntity<>(stateList, HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Get a State") // label for swagger 
	@GetMapping("/{stateId}")
	public ResponseEntity<?> getState(@PathVariable String stateId,@RequestParam String countryId,@RequestParam String languageId) {
    	State state = stateService.getState(stateId,countryId,languageId);
    	log.info("State : " + state);
		return new ResponseEntity<>(state, HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Create State") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addState(@Valid @RequestBody AddState newState,@RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		State createdState = stateService.createState(newState,loginUserID);
		return new ResponseEntity<>(createdState , HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Update State") // label for swagger
    @PatchMapping("/{stateId}")
	public ResponseEntity<?> patchState(@PathVariable String stateId, @RequestParam String countryId,@RequestParam String languageId,@RequestParam String loginUserID,
			@Valid @RequestBody UpdateState updateState)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		State updatedState = stateService.updateState(stateId,countryId,languageId,loginUserID,updateState);
		return new ResponseEntity<>(updatedState , HttpStatus.OK);
	}
    
    @ApiOperation(response = State.class, value = "Delete State") // label for swagger
	@DeleteMapping("/{stateId}")
	public ResponseEntity<?> deleteState(@PathVariable String stateId, @RequestParam String countryId,@RequestParam String languageId) {
    	stateService.deleteState(stateId,countryId,languageId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = State.class, value = "Find State") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findState(@Valid @RequestBody FindState findState) throws Exception {
		List<State> createdState = stateService.findState(findState);
		return new ResponseEntity<>(createdState, HttpStatus.OK);
	}
}