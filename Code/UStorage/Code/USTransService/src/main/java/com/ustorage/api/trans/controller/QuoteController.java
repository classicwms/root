package com.ustorage.api.trans.controller;

import com.ustorage.api.trans.model.quote.*;

import com.ustorage.api.trans.service.QuoteService;

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
@Api(tags = { "Quote" }, value = "Quote Operations related to QuoteController") 
@SwaggerDefinition(tags = { @Tag(name = "Quote", description = "Operations related to Quote") })
@RequestMapping("/quote")
@RestController
public class QuoteController {

	@Autowired
	QuoteService quoteService;

	@ApiOperation(response = Quote.class, value = "Get all Quote details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Quote> quoteList = quoteService.getQuote();
		return new ResponseEntity<>(quoteList, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Get a Quote") // label for swagger
	@GetMapping("/{quoteId}")
	public ResponseEntity<?> getQuote(@PathVariable String quoteId) {
		Quote dbQuote = quoteService.getQuote(quoteId);
		log.info("Quote : " + dbQuote);
		return new ResponseEntity<>(dbQuote, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Create Quote") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postQuote(@Valid @RequestBody AddQuote newQuote,
			@RequestParam String loginUserID) throws Exception {
		Quote createdQuote = quoteService.createQuote(newQuote, loginUserID);
		return new ResponseEntity<>(createdQuote, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Update Quote") // label for swagger
	@PatchMapping("/{quote}")
	public ResponseEntity<?> patchQuote(@PathVariable String quote,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateQuote updateQuote)
			throws IllegalAccessException, InvocationTargetException {
		Quote updatedQuote = quoteService.updateQuote(quote, loginUserID,
				updateQuote);
		return new ResponseEntity<>(updatedQuote, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Delete Quote") // label for swagger
	@DeleteMapping("/{quote}")
	public ResponseEntity<?> deleteQuote(@PathVariable String quote, @RequestParam String loginUserID) {
		quoteService.deleteQuote(quote, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Quote.class, value = "Find Quote") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findQuote(@Valid @RequestBody FindQuote findQuote) throws Exception {
		List<Quote> createdQuote = quoteService.findQuote(findQuote);
		return new ResponseEntity<>(createdQuote, HttpStatus.OK);
	}
}
