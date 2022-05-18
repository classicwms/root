package com.tekclover.wms.api.enterprise.controller;

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

import com.tekclover.wms.api.enterprise.model.variant.AddVariant;
import com.tekclover.wms.api.enterprise.model.variant.SearchVariant;
import com.tekclover.wms.api.enterprise.model.variant.UpdateVariant;
import com.tekclover.wms.api.enterprise.model.variant.Variant;
import com.tekclover.wms.api.enterprise.service.VariantService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Variant "}, value = "Variant  Operations related to VariantController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Variant ",description = "Operations related to Variant ")})
@RequestMapping("/variant")
@RestController
public class VariantController {
	
	@Autowired
	VariantService variantService;
	
    @ApiOperation(response = Variant.class, value = "Get all Variant details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Variant> variantList = variantService.getVariants();
		return new ResponseEntity<>(variantList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = Variant.class, value = "Get a Variant") // label for swagger 
	@GetMapping("/{variantCode}")
	public ResponseEntity<?> getVariant(@PathVariable String variantCode) {
    	Variant variant = variantService.getVariant(variantCode);
    	log.info("Variant : " + variant);
		return new ResponseEntity<>(variant, HttpStatus.OK);
	}
    
    @ApiOperation(response = Variant.class, value = "Search Variant") // label for swagger
   	@PostMapping("/findVariant")
   	public List<Variant> findVariant(@RequestBody SearchVariant searchVariant)
   			throws Exception {
   		return variantService.findVariant(searchVariant);
   	}
    
    @ApiOperation(response = Variant.class, value = "Create Variant") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postVariant(@Valid @RequestBody AddVariant newVariant, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Variant createdVariant = variantService.createVariant(newVariant, loginUserID);
		return new ResponseEntity<>(createdVariant , HttpStatus.OK);
	}
    
    @ApiOperation(response = Variant.class, value = "Update Variant") // label for swagger
    @PatchMapping("/{variantCode}")
	public ResponseEntity<?> patchVariant(@PathVariable String variantCode, 
			@Valid @RequestBody UpdateVariant updateVariant, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Variant createdVariant = variantService.updateVariant(variantCode, updateVariant, loginUserID);
		return new ResponseEntity<>(createdVariant , HttpStatus.OK);
	}
    
    @ApiOperation(response = Variant.class, value = "Delete Variant") // label for swagger
	@DeleteMapping("/{variantCode}")
	public ResponseEntity<?> deleteVariant(@PathVariable String variantCode, @RequestParam String loginUserID) {
    	variantService.deleteVariant(variantCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}