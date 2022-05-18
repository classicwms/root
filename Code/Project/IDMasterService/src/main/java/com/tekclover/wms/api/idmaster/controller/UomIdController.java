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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.idmaster.model.uomid.AddUomId;
import com.tekclover.wms.api.idmaster.model.uomid.UomId;
import com.tekclover.wms.api.idmaster.model.uomid.UpdateUomId;
import com.tekclover.wms.api.idmaster.service.UomIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"UomId"}, value = "UomId  Operations related to UomIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "UomId ",description = "Operations related to UomId ")})
@RequestMapping("/uomid")
@RestController
public class UomIdController {
	
	@Autowired
	UomIdService uomidService;
	
    @ApiOperation(response = UomId.class, value = "Get all UomId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<UomId> uomidList = uomidService.getUomIds();
		return new ResponseEntity<>(uomidList, HttpStatus.OK);
	}
    
    @ApiOperation(response = UomId.class, value = "Get a UomId") // label for swagger 
	@GetMapping("/{uomId}")
	public ResponseEntity<?> getUomId(@PathVariable String uomId) {
    	UomId uomid = uomidService.getUomId(uomId);
    	log.info("UomId : " + uomid);
		return new ResponseEntity<>(uomid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = UomId.class, value = "Search UomId") // label for swagger
//	@PostMapping("/findUomId")
//	public List<UomId> findUomId(@RequestBody SearchUomId searchUomId)
//			throws Exception {
//		return uomidService.findUomId(searchUomId);
//	}
    
    @ApiOperation(response = UomId.class, value = "Create UomId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postUomId(@Valid @RequestBody AddUomId newUomId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		UomId createdUomId = uomidService.createUomId(newUomId, loginUserID);
		return new ResponseEntity<>(createdUomId , HttpStatus.OK);
	}
    
    @ApiOperation(response = UomId.class, value = "Update UomId") // label for swagger
    @PatchMapping("/{uomId}")
	public ResponseEntity<?> patchUomId(@PathVariable String uomId, @Valid @RequestBody UpdateUomId updateUomId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		UomId createdUomId = uomidService.updateUomId(uomId, loginUserID, updateUomId);
		return new ResponseEntity<>(createdUomId , HttpStatus.OK);
	}
    
    @ApiOperation(response = UomId.class, value = "Delete UomId") // label for swagger
	@DeleteMapping("/{uomId}")
	public ResponseEntity<?> deleteUomId(@PathVariable String uomId, 
			@RequestParam String loginUserID) {
    	uomidService.deleteUomId(uomId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}