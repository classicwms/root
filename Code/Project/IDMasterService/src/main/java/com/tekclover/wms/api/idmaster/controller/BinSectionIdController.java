package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.binsectionid.AddBinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.BinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.UpdateBinSectionId;
import com.tekclover.wms.api.idmaster.service.BinSectionIdService;
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
@Api(tags = {"BinSectionId"}, value = "BinSectionId  Operations related to BinSectionIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BinSectionId ",description = "Operations related to BinSectionId ")})
@RequestMapping("/binsectionid")
@RestController
public class BinSectionIdController {
	
	@Autowired
	BinSectionIdService binsectionidService;
	
    @ApiOperation(response = BinSectionId.class, value = "Get all BinSectionId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BinSectionId> binsectionidList = binsectionidService.getBinSectionIds();
		return new ResponseEntity<>(binsectionidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BinSectionId.class, value = "Get a BinSectionId") // label for swagger 
	@GetMapping("/{binSectionId}")
	public ResponseEntity<?> getBinSectionId(@PathVariable String binSectionId,
			@RequestParam String warehouseId) {
    	BinSectionId binsectionid = 
    			binsectionidService.getBinSectionId(warehouseId, binSectionId);
    	log.info("BinSectionId : " + binsectionid);
		return new ResponseEntity<>(binsectionid, HttpStatus.OK);
	}
    
    @ApiOperation(response = BinSectionId.class, value = "Create BinSectionId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBinSectionId(@Valid @RequestBody AddBinSectionId newBinSectionId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		BinSectionId createdBinSectionId = binsectionidService.createBinSectionId(newBinSectionId, loginUserID);
		return new ResponseEntity<>(createdBinSectionId , HttpStatus.OK);
	}
    
    @ApiOperation(response = BinSectionId.class, value = "Update BinSectionId") // label for swagger
    @PatchMapping("/{binSectionId}")
	public ResponseEntity<?> patchBinSectionId(@PathVariable String binSectionId,
			@RequestParam String warehouseId, 
			@Valid @RequestBody UpdateBinSectionId updateBinSectionId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BinSectionId createdBinSectionId = 
				binsectionidService.updateBinSectionId(warehouseId, binSectionId, loginUserID, updateBinSectionId);
		return new ResponseEntity<>(createdBinSectionId , HttpStatus.OK);
	}
    
    @ApiOperation(response = BinSectionId.class, value = "Delete BinSectionId") // label for swagger
	@DeleteMapping("/{binSectionId}")
	public ResponseEntity<?> deleteBinSectionId(@PathVariable String binSectionId,
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	binsectionidService.deleteBinSectionId(warehouseId, binSectionId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}