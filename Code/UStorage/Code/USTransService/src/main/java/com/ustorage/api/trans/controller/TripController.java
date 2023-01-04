package com.ustorage.api.trans.controller;

import com.ustorage.api.trans.model.trip.*;

import com.ustorage.api.trans.service.TripService;
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
@Api(tags = { "Trip" }, value = "Trip Operations related to TripController") 
@SwaggerDefinition(tags = { @Tag(name = "Trip", description = "Operations related to Trip") })
@RequestMapping("/trip")
@RestController
public class TripController {

	@Autowired
	TripService tripService;

	@ApiOperation(response = Trip.class, value = "Get all Trip details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Trip> tripList = tripService.getTrip();
		return new ResponseEntity<>(tripList, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Get a Trip") // label for swagger
	@GetMapping("/{tripId}")
	public ResponseEntity<?> getTrip(@PathVariable String tripId) {
		Trip dbTrip = tripService.getTrip(tripId);
		log.info("Trip : " + dbTrip);
		return new ResponseEntity<>(dbTrip, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Create Trip") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postTrip(@Valid @RequestBody AddTrip newTrip,
			@RequestParam String loginUserID) throws Exception {
		Trip createdTrip = tripService.createTrip(newTrip, loginUserID);
		return new ResponseEntity<>(createdTrip, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Update Trip") // label for swagger
	@PatchMapping("/{trip}")
	public ResponseEntity<?> patchTrip(@PathVariable String trip,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateTrip updateTrip)
			throws IllegalAccessException, InvocationTargetException {
		Trip updatedTrip = tripService.updateTrip(trip, loginUserID,
				updateTrip);
		return new ResponseEntity<>(updatedTrip, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Delete Trip") // label for swagger
	@DeleteMapping("/{trip}")
	public ResponseEntity<?> deleteTrip(@PathVariable String trip, @RequestParam String loginUserID) {
		tripService.deleteTrip(trip, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Trip.class, value = "Find Trip") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findTrip(@Valid @RequestBody FindTrip findTrip) throws Exception {
		List<Trip> createdTrip = tripService.findTrip(findTrip);
		return new ResponseEntity<>(createdTrip, HttpStatus.OK);
	}
}
