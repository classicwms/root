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

import com.tekclover.wms.api.idmaster.model.currency.AddCurrency;
import com.tekclover.wms.api.idmaster.model.currency.Currency;
import com.tekclover.wms.api.idmaster.model.currency.UpdateCurrency;
import com.tekclover.wms.api.idmaster.service.CurrencyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Currency"}, value = "Currency Operations related to CurrencyController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Currency",description = "Operations related to Currency")})
@RequestMapping("/currency")
@RestController
public class CurrencyController {
	
	@Autowired
	CurrencyService currencyService;
	
    @ApiOperation(response = Currency.class, value = "Get all Currency details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Currency> currencyList = currencyService.getCompanies();
		return new ResponseEntity<>(currencyList, HttpStatus.OK);
	}
    
    @ApiOperation(response = Currency.class, value = "Get a Currency") // label for swagger 
	@GetMapping("/{currencyId}")
	public ResponseEntity<?> getCurrency(@PathVariable Long currencyId) {
    	Currency currency = currencyService.getCurrency(currencyId);
    	log.info("Currency : " + currency);
		return new ResponseEntity<>(currency, HttpStatus.OK);
	}
    
    @ApiOperation(response = Currency.class, value = "Create Currency") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addCurrency(@Valid @RequestBody AddCurrency newCurrency) 
			throws IllegalAccessException, InvocationTargetException {
		Currency createdCurrency = currencyService.createCurrency(newCurrency);
		return new ResponseEntity<>(createdCurrency , HttpStatus.OK);
	}
    
    @ApiOperation(response = Currency.class, value = "Update Currency") // label for swagger
    @PatchMapping("/{currencyId}")
	public ResponseEntity<?> patchCurrency(@PathVariable Long currencyId, 
			@Valid @RequestBody UpdateCurrency updateCurrency) 
			throws IllegalAccessException, InvocationTargetException {
		Currency updatedCurrency = currencyService.updateCurrency(currencyId, updateCurrency);
		return new ResponseEntity<>(updatedCurrency , HttpStatus.OK);
	}
    
    @ApiOperation(response = Currency.class, value = "Delete Currency") // label for swagger
	@DeleteMapping("/{currencyId}")
	public ResponseEntity<?> deleteCurrency(@PathVariable Long currencyId) {
    	currencyService.deleteCurrency(currencyId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}