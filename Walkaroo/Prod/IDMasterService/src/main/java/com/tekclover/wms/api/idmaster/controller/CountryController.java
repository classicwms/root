package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.country.FindCountry;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
	private LanguageIdRepository languageIdRepository;

	@Autowired
	CountryService countryService;
	
    @ApiOperation(response = Country.class, value = "Get all Country details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Country> countryList = countryService.getCountrys();
		return new ResponseEntity<>(countryList, HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Get a Country") // label for swagger 
	@GetMapping("/{countryId}")
	public ResponseEntity<?> getCountry(@PathVariable String countryId,@RequestParam String languageId) {
    	Country country = countryService.getCountry(countryId,languageId);
    	log.info("Country : " + country);
		return new ResponseEntity<>(country, HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Create Country") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addCountry(@Valid @RequestBody AddCountry newCountry,@RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Country createdCountry = countryService.createCountry(newCountry,loginUserID);
		return new ResponseEntity<>(createdCountry , HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Update Country") // label for swagger
    @PatchMapping("/{countryId}")
	public ResponseEntity<?> patchCountry(@PathVariable String countryId,@RequestParam String languageId,@RequestParam String loginUserID,
			@Valid @RequestBody UpdateCountry updateCountry)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Country updatedCountry = countryService.updateCountry(countryId,languageId,loginUserID,updateCountry);
		return new ResponseEntity<>(updatedCountry , HttpStatus.OK);
	}
    
    @ApiOperation(response = Country.class, value = "Delete Country") // label for swagger
	@DeleteMapping("/{countryId}")
	public ResponseEntity<?> deleteCountry(@PathVariable String countryId, @RequestParam String languageId) {
    	countryService.deleteCountry(countryId,languageId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Country.class, value = "Find Country") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findCountry(@Valid @RequestBody FindCountry findCountry) throws Exception {
		List<Country> createdCountry = countryService.findCountry(findCountry);
		return new ResponseEntity<>(createdCountry, HttpStatus.OK);
	}
}