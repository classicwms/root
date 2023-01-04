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

import com.ustorage.api.master.model.rack.AddRack;
import com.ustorage.api.master.model.rack.Rack;
import com.ustorage.api.master.model.rack.UpdateRack;
import com.ustorage.api.master.service.RackService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Rack" }, value = "Rack Operations related to RackController") 
@SwaggerDefinition(tags = { @Tag(name = "Rack", description = "Operations related to Rack") })
@RequestMapping("/rack")
@RestController
public class RackController {

	@Autowired
	RackService rackService;

	@ApiOperation(response = Rack.class, value = "Get all Rack details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Rack> rackList = rackService.getRack();
		return new ResponseEntity<>(rackList, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Get a Rack") // label for swagger
	@GetMapping("/{rackId}")
	public ResponseEntity<?> getRack(@PathVariable String rackId) {
		Rack dbRack = rackService.getRack(rackId);
		log.info("Rack : " + dbRack);
		return new ResponseEntity<>(dbRack, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Create Rack") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRack(@Valid @RequestBody AddRack newRack,
			@RequestParam String loginUserID) throws Exception {
		Rack createdRack = rackService.createRack(newRack, loginUserID);
		return new ResponseEntity<>(createdRack, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Update Rack") // label for swagger
	@PatchMapping("/{rackId}")
	public ResponseEntity<?> patchRack(@PathVariable String rackId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateRack updateRack)
			throws IllegalAccessException, InvocationTargetException {
		Rack updatedRack = rackService.updateRack(rackId, loginUserID,
				updateRack);
		return new ResponseEntity<>(updatedRack, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Delete Rack") // label for swagger
	@DeleteMapping("/{rackId}")
	public ResponseEntity<?> deleteRack(@PathVariable String rackId, @RequestParam String loginUserID) {
		rackService.deleteRack(rackId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
