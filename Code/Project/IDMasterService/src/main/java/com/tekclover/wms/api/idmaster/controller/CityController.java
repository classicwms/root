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
	
    @ApiOperation(response = City.class, value = "Get all City details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<City> cityList = cityService.getCompanies();
		return new ResponseEntity<>(cityList, HttpStatus.OK);
	}
    
    @ApiOperation(response = City.class, value = "Get a City") // label for swagger 
	@GetMapping("/{cityId}")
	public ResponseEntity<?> getCity(@PathVariable String cityId) {
    	City city = cityService.getCity(cityId);
    	log.info("City : " + city);
		return new ResponseEntity<>(city, HttpStatus.OK);
	}
    
    @ApiOperation(response = City.class, value = "Create City") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addCity(@Valid @RequestBody AddCity newCity) 
			throws IllegalAccessException, InvocationTargetException {
		City createdCity = cityService.createCity(newCity);
		return new ResponseEntity<>(createdCity , HttpStatus.OK);
	}
    
    @ApiOperation(response = City.class, value = "Update City") // label for swagger
    @PatchMapping("/{cityId}")
	public ResponseEntity<?> patchCity(@PathVariable String cityId, 
			@RequestBody UpdateCity updateCity) throws IllegalAccessException, InvocationTargetException {
		City updatedCity = cityService.updateCity(cityId, updateCity);
		return new ResponseEntity<>(updatedCity , HttpStatus.OK);
	}
    
    @ApiOperation(response = City.class, value = "Delete City") // label for swagger
	@DeleteMapping("/{cityId}")
	public ResponseEntity<?> deleteCity(@PathVariable String cityId) {
    	cityService.deleteCity(cityId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}