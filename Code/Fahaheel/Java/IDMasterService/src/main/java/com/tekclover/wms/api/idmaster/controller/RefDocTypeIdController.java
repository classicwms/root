package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.refdoctypeid.*;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"RefDocTypeId"}, value = "RefDocTypeId  Operations related to RefDocTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "RefDocTypeId ",description = "Operations related to RefDocTypeId")})
@RequestMapping("/refdoctypeid")
@RestController
public class RefDocTypeIdController {
	@Autowired
	private CompanyIdRepository companyIdRepository;

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
	public ResponseEntity<?> getRefDocTypeId(@RequestParam String warehouseId,@PathVariable String referenceDocumentTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
    	RefDocTypeId refdoctypeid = 
    			refdoctypeidService.getRefDocTypeId(warehouseId, referenceDocumentTypeId,companyCodeId,languageId,plantId);
    	log.info("RefDocTypeId : " + refdoctypeid);
		return new ResponseEntity<>(refdoctypeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = RefDocTypeId.class, value = "Create ReferenceDocumentTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRefDocTypeId(@Valid @RequestBody AddRefDocTypeId newRefDocTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		RefDocTypeId createdRefDocTypeId = refdoctypeidService.createRefDocTypeId(newRefDocTypeId, loginUserID);
		return new ResponseEntity<>(createdRefDocTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = RefDocTypeId.class, value = "Update ReferenceDocumentTypeId") // label for swagger
    @PatchMapping("/{referenceDocumentTypeId}")
	public ResponseEntity<?> patchRefDocTypeId(@PathVariable String referenceDocumentTypeId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateRefDocTypeId updateRefDocTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		RefDocTypeId createdRefDocTypeId = 
				refdoctypeidService.updateRefDocTypeId(warehouseId, referenceDocumentTypeId,companyCodeId,languageId,plantId,loginUserID, updateRefDocTypeId);
		return new ResponseEntity<>(createdRefDocTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = RefDocTypeId.class, value = "Delete ReferenceDocumentTypeId") // label for swagger
	@DeleteMapping("/{referenceDocumentTypeId}")
	public ResponseEntity<?> deleteRefDocTypeId(@PathVariable String referenceDocumentTypeId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
    	refdoctypeidService.deleteRefDocTypeId(warehouseId, referenceDocumentTypeId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = RefDocTypeId.class, value = "Find RefDocTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findRefDocTypeId(@Valid @RequestBody FindRefDocTypeId findRefDocTypeId) throws Exception {
		List<RefDocTypeId> createdRefDocTypeId = refdoctypeidService.findRefDocTypeId(findRefDocTypeId);
		return new ResponseEntity<>(createdRefDocTypeId, HttpStatus.OK);
	}
}