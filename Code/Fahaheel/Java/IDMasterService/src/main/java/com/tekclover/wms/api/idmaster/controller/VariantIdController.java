package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.varientid.FindVariantId;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
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

import com.tekclover.wms.api.idmaster.model.varientid.AddVariantId;
import com.tekclover.wms.api.idmaster.model.varientid.UpdateVariantId;
import com.tekclover.wms.api.idmaster.model.varientid.VariantId;
import com.tekclover.wms.api.idmaster.service.VariantIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"VariantId"}, value = "VariantId  Operations related to VariantIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "VariantId ",description = "Operations related to VariantId ")})
@RequestMapping("/variantid")
@RestController
public class VariantIdController {
	@Autowired
	private PlantIdRepository plantIdRepository;

	@Autowired
	VariantIdService variantidService;
	
    @ApiOperation(response = VariantId.class, value = "Get all VariantId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<VariantId> variantidList = variantidService.getVariantIds();
		return new ResponseEntity<>(variantidList, HttpStatus.OK);
	}
    
    @ApiOperation(response = VariantId.class, value = "Get a VariantId") // label for swagger 
	@GetMapping("/{variantCode}")
	public ResponseEntity<?> getVariantId(@PathVariable String variantCode, 
			@RequestParam String warehouseId, @RequestParam String variantType, @RequestParam String variantSubCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId) {
    	VariantId variantid = 
    			variantidService.getVariantId(warehouseId, variantCode,variantType, variantSubCode,companyCodeId,plantId,languageId);
    	log.info("VariantId : " + variantid);
		return new ResponseEntity<>(variantid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = VariantId.class, value = "Search VariantId") // label for swagger
//	@PostMapping("/findVariantId")
//	public List<VariantId> findVariantId(@RequestBody SearchVariantId searchVariantId)
//			throws Exception {
//		return variantidService.findVariantId(searchVariantId);
//	}
    
    @ApiOperation(response = VariantId.class, value = "Create VariantId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postVariantId(@Valid @RequestBody AddVariantId newVariantId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		VariantId createdVariantId = variantidService.createVariantId(newVariantId, loginUserID);
		return new ResponseEntity<>(createdVariantId , HttpStatus.OK);
	}
    
    @ApiOperation(response = VariantId.class, value = "Update VariantId") // label for swagger
    @PatchMapping("/{variantCode}")
	public ResponseEntity<?> patchVariantId(@PathVariable String variantCode, 
			@RequestParam String warehouseId, @RequestParam String variantType, @RequestParam String variantSubCode, @RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,
			@Valid @RequestBody UpdateVariantId updateVariantId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		VariantId createdVariantId = 
				variantidService.updateVariantId(warehouseId, variantCode, variantType, variantSubCode,companyCodeId,plantId,languageId,loginUserID, updateVariantId);
		return new ResponseEntity<>(createdVariantId , HttpStatus.OK);
	}
    
    @ApiOperation(response = VariantId.class, value = "Delete VariantId") // label for swagger
	@DeleteMapping("/{variantCode}")
	public ResponseEntity<?> deleteVariantId(@PathVariable String variantCode, 
			@RequestParam String warehouseId, @RequestParam String variantType, @RequestParam String variantSubCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,@RequestParam String loginUserID) {
    	variantidService.deleteVariantId(warehouseId, variantCode, variantType, variantSubCode,companyCodeId,plantId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = VariantId.class, value = "Find VariantId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findVariantId(@Valid @RequestBody FindVariantId findVariantId) throws Exception {
		List<VariantId> createdVariantId = variantidService.findVariantId(findVariantId);
		return new ResponseEntity<>(createdVariantId, HttpStatus.OK);
	}
}