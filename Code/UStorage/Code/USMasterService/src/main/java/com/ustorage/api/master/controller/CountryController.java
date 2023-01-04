package com.ustorage.api.master.controller;

import com.ustorage.api.master.model.country.*;

import com.ustorage.api.master.service.CountryService;
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
@Api(tags = { "Country" }, value = "Country Operations related to CountryController") 
@SwaggerDefinition(tags = { @Tag(name = "Country", description = "Operations related to Country") })
@RequestMapping("/country")
@RestController
public class CountryController {

	@Autowired
	CountryService countryService;

	@ApiOperation(response = Country.class, value = "Get all Country details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Country> countryList = countryService.getCountry();
		return new ResponseEntity<>(countryList, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Get a Country") // label for swagger
	@GetMapping("/{countryId}")
	public ResponseEntity<?> getCountry(@PathVariable String countryId) {
		Country dbCountry = countryService.getCountry(countryId);
		log.info("Country : " + dbCountry);
		return new ResponseEntity<>(dbCountry, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Create Country") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postCountry(@Valid @RequestBody AddCountry newCountry,
			@RequestParam String loginUserID) throws Exception {
		Country createdCountry = countryService.createCountry(newCountry, loginUserID);
		return new ResponseEntity<>(createdCountry, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Update Country") // label for swagger
	@PatchMapping("/{countryId}")
	public ResponseEntity<?> patchCountry(@PathVariable String countryId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateCountry updateCountry)
			throws IllegalAccessException, InvocationTargetException {
		Country updatedCountry = countryService.updateCountry(countryId, loginUserID,
				updateCountry);
		return new ResponseEntity<>(updatedCountry, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Delete Country") // label for swagger
	@DeleteMapping("/{countryId}")
	public ResponseEntity<?> deleteCountry(@PathVariable String countryId, @RequestParam String loginUserID) {
		countryService.deleteCountry(countryId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
