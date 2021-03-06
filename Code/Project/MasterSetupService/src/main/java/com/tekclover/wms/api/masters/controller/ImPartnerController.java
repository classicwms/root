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

import com.tekclover.wms.api.masters.model.impartner.AddImPartner;
import com.tekclover.wms.api.masters.model.impartner.ImPartner;
import com.tekclover.wms.api.masters.model.impartner.UpdateImPartner;
import com.tekclover.wms.api.masters.service.ImPartnerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ImPartner"}, value = "ImPartner  Operations related to ImPartnerController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImPartner ",description = "Operations related to ImPartner ")})
@RequestMapping("/impartner")
@RestController
public class ImPartnerController {
	
	@Autowired
	ImPartnerService impartnerService;
	
    @ApiOperation(response = ImPartner.class, value = "Get all ImPartner details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ImPartner> impartnerList = impartnerService.getImPartners();
		return new ResponseEntity<>(impartnerList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImPartner.class, value = "Get a ImPartner") // label for swagger 
	@GetMapping("/{businessPartnerCode}")
	public ResponseEntity<?> getImPartner(@PathVariable String businessPartnerCode) {
    	ImPartner impartner = impartnerService.getImPartner(businessPartnerCode);
    	log.info("ImPartner : " + impartner);
		return new ResponseEntity<>(impartner, HttpStatus.OK);
	}
    
//	@ApiOperation(response = ImPartner.class, value = "Search ImPartner") // label for swagger
//	@PostMapping("/findImPartner")
//	public List<ImPartner> findImPartner(@RequestBody SearchImPartner searchImPartner)
//			throws ParseException {
//		return impartnerService.findImPartner(searchImPartner);
//	}
	
    @ApiOperation(response = ImPartner.class, value = "Create ImPartner") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postImPartner(@Valid @RequestBody AddImPartner newImPartner, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImPartner createdImPartner = impartnerService.createImPartner(newImPartner, loginUserID);
		return new ResponseEntity<>(createdImPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImPartner.class, value = "Update ImPartner") // label for swagger
    @PatchMapping("/{businessPartnerCode}")
	public ResponseEntity<?> patchImPartner(@PathVariable String businessPartnerCode, 
			@Valid @RequestBody UpdateImPartner updateImPartner, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImPartner createdImPartner = impartnerService.updateImPartner(businessPartnerCode, updateImPartner, loginUserID);
		return new ResponseEntity<>(createdImPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImPartner.class, value = "Delete ImPartner") // label for swagger
	@DeleteMapping("/{businessPartnerCode}")
	public ResponseEntity<?> deleteImPartner(@PathVariable String businessPartnerCode, @RequestParam String loginUserID) {
    	impartnerService.deleteImPartner(businessPartnerCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}