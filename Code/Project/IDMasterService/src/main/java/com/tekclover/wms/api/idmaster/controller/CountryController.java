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

import com.tekclover.wms.api.idmaster.model.country.AddCountry;
import com.tekclover.wms.api.idmaster.model.country.Country;
import com.tekclover.wms.api.idmaster.model.country.UpdateCountry;
import com.tekclover.wms.api.idmaster.service.CountryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Country"}, value = "Country Operations related to CountryController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Country",description = "Operations related to Country")})
@RequestMapping("/country")
@RestController
public class CountryController {
	
	@Autowired
	CountryService countryService;
	
    @ApiOperation(response = Country.class, value = "Get all Country details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Country> countryList = countryService.getCompanies();
		return new ResponseEntity<>(countryList, HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Get a Country") // label for swagger 
	@GetMapping("/{countryId}")
	public ResponseEntity<?> getCountry(@PathVariable String countryId) {
    	Country country = countryService.getCountry(countryId);
    	log.info("Country : " + country);
		return new ResponseEntity<>(country, HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Create Country") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addCountry(@Valid @RequestBody AddCountry newCountry) 
			throws IllegalAccessException, InvocationTargetException {
		Country createdCountry = countryService.createCountry(newCountry);
		return new ResponseEntity<>(createdCountry , HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Update Country") // label for swagger
    @PatchMapping("/{countryId}")
	public ResponseEntity<?> patchCountry(@PathVariable String countryId, 
			@Valid @RequestBody UpdateCountry updateCountry) 
			throws IllegalAccessException, InvocationTargetException {
		Country updatedCountry = countryService.updateCountry(countryId, updateCountry);
		return new ResponseEntity<>(updatedCountry , HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Delete Country") // label for swagger
	@DeleteMapping("/{countryId}")
	public ResponseEntity<?> deleteCountry(@PathVariable String countryId) {
    	countryService.deleteCountry(countryId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}