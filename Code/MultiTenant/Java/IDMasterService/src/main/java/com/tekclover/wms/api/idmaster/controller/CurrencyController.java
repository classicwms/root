package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.currency.FindCurrency;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<?> getCurrency(@PathVariable Long currencyId, @RequestParam String languageId) {
    	Currency currency = currencyService.getCurrency(currencyId,languageId);
    	log.info("Currency : " + currency);
		return new ResponseEntity<>(currency, HttpStatus.OK);
	}
    
    @ApiOperation(response = Currency.class, value = "Create Currency") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addCurrency(@Valid @RequestBody AddCurrency newCurrency,String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Currency createdCurrency = currencyService.createCurrency(newCurrency,loginUserID);
		return new ResponseEntity<>(createdCurrency , HttpStatus.OK);
	}
    
    @ApiOperation(response = Currency.class, value = "Update Currency") // label for swagger
    @PatchMapping("/{currencyId}")
	public ResponseEntity<?> patchCurrency(@PathVariable Long currencyId, @RequestParam String languageId,@RequestParam String loginUserID,
			@Valid @RequestBody UpdateCurrency updateCurrency)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Currency updatedCurrency = currencyService.updateCurrency(currencyId,languageId,loginUserID,updateCurrency);
		return new ResponseEntity<>(updatedCurrency , HttpStatus.OK);
	}
    
    @ApiOperation(response = Currency.class, value = "Delete Currency") // label for swagger
	@DeleteMapping("/{currencyId}")
	public ResponseEntity<?> deleteCurrency(@PathVariable Long currencyId,@RequestParam String languageId) {
    	currencyService.deleteCurrency(currencyId,languageId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Currency.class, value = "Find Currency") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findCurrency(@Valid @RequestBody FindCurrency findCurrency) throws Exception {
		List<Currency> createdCurrency = currencyService.findCurrency(findCurrency);
		return new ResponseEntity<>(createdCurrency, HttpStatus.OK);
	}
}