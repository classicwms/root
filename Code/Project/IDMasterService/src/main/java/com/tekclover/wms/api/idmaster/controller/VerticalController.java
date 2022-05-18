package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.idmaster.model.vertical.AddVertical;
import com.tekclover.wms.api.idmaster.model.vertical.UpdateVertical;
import com.tekclover.wms.api.idmaster.model.vertical.Vertical;
import com.tekclover.wms.api.idmaster.service.VerticalService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = {"Vertical"}, value = "Vertical Operations related to VerticalController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Vertical",description = "Operations related to Vertical")})
@RequestMapping("/vertical")
@RestController
public class VerticalController {
	
	@Autowired
	VerticalService verticalService;
	
    @ApiOperation(response = Vertical.class, value = "Get all Vertical details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Vertical> verticalList = verticalService.getCompanies();
		return new ResponseEntity<>(verticalList, HttpStatus.OK);
	}
    
    @ApiOperation(response = Vertical.class, value = "Get a Vertical") // label for swagger 
	@GetMapping("/{verticalId}")
	public ResponseEntity<?> getVertical(@PathVariable Long verticalId) {
    	Vertical vertical = verticalService.getVertical(verticalId);
    	log.info("Vertical : " + vertical);
		return new ResponseEntity<>(vertical, HttpStatus.OK);
	}
    
    @ApiOperation(response = Vertical.class, value = "Create Vertical") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addVertical(@Valid @RequestBody AddVertical newVertical) 
			throws IllegalAccessException, InvocationTargetException {
		Vertical createdVertical = verticalService.createVertical(newVertical);
		return new ResponseEntity<>(createdVertical , HttpStatus.OK);
	}
    
    @ApiOperation(response = Vertical.class, value = "Update Vertical") // label for swagger
    @PatchMapping("/{verticalId}")
	public ResponseEntity<?> patchVertical(@PathVariable Long verticalId,
			@RequestBody UpdateVertical updateVertical) 
			throws IllegalAccessException, InvocationTargetException {
		Vertical updatedVertical = verticalService.updateVertical(verticalId, updateVertical);
		return new ResponseEntity<>(updatedVertical , HttpStatus.OK);
	}
    
    @ApiOperation(response = Vertical.class, value = "Delete Vertical") // label for swagger
	@DeleteMapping("/{verticalId}")
	public ResponseEntity<?> deleteVertical(@PathVariable Long verticalId) {
    	verticalService.deleteVertical(verticalId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}