package com.tekclover.wms.api.idmaster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.idmaster.model.numberrange.NumberRange;
import com.tekclover.wms.api.idmaster.service.NumberRangeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = {"NumberRange"}, value = "NumberRange Operations related to NumberRangeController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "NumberRange",description = "Operations related to NumberRange")})
@RequestMapping("/numberRange")
@RestController
public class NumberRangeController {
	
	@Autowired
	NumberRangeService numberRangeService;
	
    @ApiOperation(response = NumberRange.class, value = "Get Number Range Current") // label for swagger
	@GetMapping("/nextNumberRange/{numberRangeCode}")
	public ResponseEntity<?> getNextNumberRange(@PathVariable Long numberRangeCode, 
			@RequestParam Long fiscalYear, @RequestParam String warehouseId) {
		String nextRangeValue = numberRangeService.getNextNumberRange(numberRangeCode, fiscalYear, warehouseId);
		return new ResponseEntity<>(nextRangeValue , HttpStatus.OK);
	}
}