package com.tekclover.wms.api.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.CountBar;
import com.tekclover.wms.api.transaction.service.CountBarService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"CountBar"}, value = "CountBar  Operations related to CountBarController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "CountBar ",description = "Operations related to CountBar ")})
@RequestMapping("/countbar")
@RestController
public class CountBarController {
	
	@Autowired
	CountBarService countbarService;
    
    @ApiOperation(response = CountBar.class, value = "Get a PreInboundNo Count") // label for swagger 
	@GetMapping("/{warehouseId}/preInbound")
	public ResponseEntity<?> getPreInboundNoCount(@PathVariable String warehouseId) {
    	CountBar countbar = new CountBar();
    	countbar.setValueOfCount(countbarService.getPreInboundCount(warehouseId));
		return new ResponseEntity<>(countbar, HttpStatus.OK);
	}
}