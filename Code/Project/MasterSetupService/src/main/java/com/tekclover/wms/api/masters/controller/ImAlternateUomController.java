package com.tekclover.wms.api.masters.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.masters.model.imalternateuom.AddImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.UpdateImAlternateUom;
import com.tekclover.wms.api.masters.service.ImAlternateUomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ImAlternateUom"}, value = "ImAlternateUom  Operations related to ImAlternateUomController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImAlternateUom ",description = "Operations related to ImAlternateUom ")})
@RequestMapping("/imalternateuom")
@RestController
public class ImAlternateUomController {
	
	@Autowired
	ImAlternateUomService imalternateuomService;
	
    @ApiOperation(response = ImAlternateUom.class, value = "Get all ImAlternateUom details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ImAlternateUom> imalternateuomList = imalternateuomService.getImAlternateUoms();
		return new ResponseEntity<>(imalternateuomList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Get a ImAlternateUom") // label for swagger 
	@GetMapping("/{alternateUom}")
	public ResponseEntity<?> getImAlternateUom(@PathVariable String alternateUom) {
    	ImAlternateUom imalternateuom = imalternateuomService.getImAlternateUom(alternateUom);
    	log.info("ImAlternateUom : " + imalternateuom);
		return new ResponseEntity<>(imalternateuom, HttpStatus.OK);
	}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Create ImAlternateUom") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postImAlternateUom(@Valid @RequestBody AddImAlternateUom newImAlternateUom, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImAlternateUom createdImAlternateUom = imalternateuomService.createImAlternateUom(newImAlternateUom, loginUserID);
		return new ResponseEntity<>(createdImAlternateUom , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Update ImAlternateUom") // label for swagger
    @PatchMapping("/{alternateUom}")
	public ResponseEntity<?> patchImAlternateUom(@PathVariable String alternateUom, 
			@Valid @RequestBody UpdateImAlternateUom updateImAlternateUom, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImAlternateUom createdImAlternateUom = imalternateuomService.updateImAlternateUom(alternateUom, updateImAlternateUom, loginUserID);
		return new ResponseEntity<>(createdImAlternateUom , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Delete ImAlternateUom") // label for swagger
	@DeleteMapping("/{alternateUom}")
	public ResponseEntity<?> deleteImAlternateUom(@PathVariable String alternateUom, @RequestParam String loginUserID) {
    	imalternateuomService.deleteImAlternateUom(alternateUom, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}