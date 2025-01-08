package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.city.FindCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.api.idmaster.model.city.AddCity;
import com.tekclover.wms.api.idmaster.model.city.City;
import com.tekclover.wms.api.idmaster.model.city.UpdateCity;
import com.tekclover.wms.api.idmaster.service.CityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"City"}, value = "City Operations related to CityController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "City",description = "Operations related to City")})
@RequestMapping("/city")
@RestController
public class CityController {
	
	@Autowired
	CityService cityService;

	// GET ALL
    @ApiOperation(response = City.class, value = "Get all City details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<City> cityList = cityService.getAllCity();
		return new ResponseEntity<>(cityList, HttpStatus.OK);
	}

	// GET
    @ApiOperation(response = City.class, value = "Get a City") // label for swagger 
	@GetMapping("/{cityId}")
	public ResponseEntity<?> getCity(@PathVariable String cityId, @RequestParam String stateId,@RequestParam String countryId,
									 @RequestParam String languageId) {
    	City city = cityService.getCity(cityId,stateId,countryId,languageId);
    	log.info("City : " + city);
		return new ResponseEntity<>(city, HttpStatus.OK);
	}

	// CREATE
    @ApiOperation(response = City.class, value = "Create City") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addCity(@Valid @RequestBody AddCity newCity,@RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		City createdCity = cityService.createCity(newCity,loginUserID);
		return new ResponseEntity<>(createdCity , HttpStatus.OK);
	}

	// UPDATE
    @ApiOperation(response = City.class, value = "Update City") // label for swagger
    @PatchMapping("/{cityId}")
	public ResponseEntity<?> patchCity(@PathVariable String cityId, @RequestParam String stateId,@RequestParam String countryId,
									   @RequestParam String languageId,@RequestParam String loginUserID,
			@RequestBody UpdateCity updateCity) throws IllegalAccessException, InvocationTargetException, ParseException {

		City updatedCity = cityService.updateCity(cityId,stateId,countryId,languageId,loginUserID,updateCity);
		return new ResponseEntity<>(updatedCity , HttpStatus.OK);
	}

	// DELETE
    @ApiOperation(response = City.class, value = "Delete City") // label for swagger
	@DeleteMapping("/{cityId}")
	public ResponseEntity<?> deleteCity(@PathVariable String cityId,@RequestParam String stateId,
										@RequestParam String countryId,@RequestParam String languageId) {
    	cityService.deleteCity(cityId,stateId,countryId,languageId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// SEARCH
	@ApiOperation(response = City.class, value = "Find City") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findCity(@Valid @RequestBody FindCity findCity) throws Exception {
		List<City> createdCity= cityService.findCity(findCity);
		return new ResponseEntity<>(createdCity, HttpStatus.OK);
	}
}