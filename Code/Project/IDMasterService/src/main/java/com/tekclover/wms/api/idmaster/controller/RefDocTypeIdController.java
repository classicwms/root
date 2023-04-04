package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.refdoctypeid.*;
import com.tekclover.wms.api.idmaster.service.RefDocTypeIdService;
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
@Api(tags = {"RefDocTypeId"}, value = "RefDocTypeId  Operations related to RefDocTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "RefDocTypeId ",description = "Operations related to RefDocTypeId")})
@RequestMapping("/refdoctypeid")
@RestController
public class RefDocTypeIdController {
	
	@Autowired
	RefDocTypeIdService refdoctypeidService;
	
    @ApiOperation(response = RefDocTypeId.class, value = "Get all ReferenceDocumentTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<RefDocTypeId> refdoctypeidList = refdoctypeidService.getRefDocTypeIds();
		return new ResponseEntity<>(refdoctypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = RefDocTypeId.class, value = "Get a ReferenceDocumentTypeId") // label for swagger
	@GetMapping("/{referenceDocumentTypeId}")
	public ResponseEntity<?> getRefDocTypeId(@PathVariable String referenceDocumentTypeId,
			@RequestParam String warehouseId) {
    	RefDocTypeId refdoctypeid = 
    			refdoctypeidService.getRefDocTypeId(warehouseId, referenceDocumentTypeId);
    	log.info("RefDocTypeId : " + refdoctypeid);
		return new ResponseEntity<>(refdoctypeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = RefDocTypeId.class, value = "Create ReferenceDocumentTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRefDocTypeId(@Valid @RequestBody AddRefDocTypeId newRefDocTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		RefDocTypeId createdRefDocTypeId = refdoctypeidService.createRefDocTypeId(newRefDocTypeId, loginUserID);
		return new ResponseEntity<>(createdRefDocTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = RefDocTypeId.class, value = "Update ReferenceDocumentTypeId") // label for swagger
    @PatchMapping("/{referenceDocumentTypeId}")
	public ResponseEntity<?> patchRefDocTypeId(@PathVariable String referenceDocumentTypeId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateRefDocTypeId updateRefDocTypeId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		RefDocTypeId createdRefDocTypeId = 
				refdoctypeidService.updateRefDocTypeId(warehouseId, referenceDocumentTypeId, loginUserID, updateRefDocTypeId);
		return new ResponseEntity<>(createdRefDocTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = RefDocTypeId.class, value = "Delete ReferenceDocumentTypeId") // label for swagger
	@DeleteMapping("/{referenceDocumentTypeId}")
	public ResponseEntity<?> deleteRefDocTypeId(@PathVariable String referenceDocumentTypeId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	refdoctypeidService.deleteRefDocTypeId(warehouseId, referenceDocumentTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}