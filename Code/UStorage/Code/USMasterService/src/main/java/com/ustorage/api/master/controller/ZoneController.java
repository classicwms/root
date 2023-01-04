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

import com.ustorage.api.master.model.zone.AddZone;
import com.ustorage.api.master.model.zone.Zone;
import com.ustorage.api.master.model.zone.UpdateZone;
import com.ustorage.api.master.service.ZoneService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Zone" }, value = "Zone Operations related to ZoneController") 
@SwaggerDefinition(tags = { @Tag(name = "Zone", description = "Operations related to Zone") })
@RequestMapping("/zone")
@RestController
public class ZoneController {

	@Autowired
	ZoneService zoneService;

	@ApiOperation(response = Zone.class, value = "Get all Zone details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Zone> zoneList = zoneService.getZone();
		return new ResponseEntity<>(zoneList, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Get a Zone") // label for swagger
	@GetMapping("/{zoneId}")
	public ResponseEntity<?> getZone(@PathVariable String zoneId) {
		Zone dbZone = zoneService.getZone(zoneId);
		log.info("Zone : " + dbZone);
		return new ResponseEntity<>(dbZone, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Create Zone") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postZone(@Valid @RequestBody AddZone newZone,
			@RequestParam String loginUserID) throws Exception {
		Zone createdZone = zoneService.createZone(newZone, loginUserID);
		return new ResponseEntity<>(createdZone, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Update Zone") // label for swagger
	@PatchMapping("/{zoneId}")
	public ResponseEntity<?> patchZone(@PathVariable String zoneId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateZone updateZone)
			throws IllegalAccessException, InvocationTargetException {
		Zone updatedZone = zoneService.updateZone(zoneId, loginUserID,
				updateZone);
		return new ResponseEntity<>(updatedZone, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Delete Zone") // label for swagger
	@DeleteMapping("/{zoneId}")
	public ResponseEntity<?> deleteZone(@PathVariable String zoneId, @RequestParam String loginUserID) {
		zoneService.deleteZone(zoneId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
