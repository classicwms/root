package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeaderEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLineEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.RunPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.SearchPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.UpdatePerpetualHeader;
import com.tekclover.wms.api.transaction.service.PerpetualHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PerpetualHeader"}, value = "PerpetualHeader  Operations related to PerpetualHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PerpetualHeader ",description = "Operations related to PerpetualHeader ")})
@RequestMapping("/perpetualheader")
@RestController
public class PerpetualHeaderController {
	
	@Autowired
	PerpetualHeaderService perpetualheaderService;
	
    @ApiOperation(response = PerpetualHeader.class, value = "Get all PerpetualHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PerpetualHeaderEntity> perpetualheaderList = perpetualheaderService.getPerpetualHeaders();
		return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PerpetualHeader.class, value = "Get a PerpetualHeader") // label for swagger 
	@GetMapping("/{cycleCountNo}")
	public ResponseEntity<?> getPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
			@RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId) {
    	List<PerpetualHeaderEntity> perpetualheader = 
    			perpetualheaderService.getPerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, 
    					movementTypeId, subMovementTypeId);
		return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PerpetualHeaderEntity.class, value = "Search PerpetualHeader") // label for swagger
	@PostMapping("/findPerpetualHeader")
	public List<PerpetualHeaderEntity> findPerpetualHeader(@RequestBody SearchPerpetualHeader searchPerpetualHeader)
			throws Exception {
		return perpetualheaderService.findPerpetualHeader(searchPerpetualHeader);
	}
    
    @ApiOperation(response = PerpetualHeader.class, value = "Create PerpetualHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPerpetualHeader(@Valid @RequestBody AddPerpetualHeader newPerpetualHeader, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader createdPerpetualHeader = 
				perpetualheaderService.createPerpetualHeader(newPerpetualHeader, loginUserID);
		return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
	}
    
    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field 
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
    @ApiOperation(response = PerpetualLineEntity.class, value = "Create PerpetualHeader") // label for swagger
   	@PostMapping("/run")
   	public ResponseEntity<?> postRunPerpetualHeader(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader) 
   			throws IllegalAccessException, InvocationTargetException, ParseException {
   		List<PerpetualLineEntity> inventoryMovements = perpetualheaderService.runPerpetualHeader(runPerpetualHeader);
   		return new ResponseEntity<>(inventoryMovements , HttpStatus.OK);
   	}
    
    @ApiOperation(response = PerpetualHeader.class, value = "Update PerpetualHeader") // label for swagger
    @PatchMapping("/{cycleCountNo}")
	public ResponseEntity<?> patchPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId, 
			@RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
			@Valid @RequestBody UpdatePerpetualHeader updatePerpetualHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader createdPerpetualHeader = 
				perpetualheaderService.updatePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
						subMovementTypeId, loginUserID, updatePerpetualHeader);
		return new ResponseEntity<>(createdPerpetualHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PerpetualHeader.class, value = "Delete PerpetualHeader") // label for swagger
	@DeleteMapping("/{cycleCountNo}")
	public ResponseEntity<?> deletePerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId, 
			@RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId, 
			@RequestParam String loginUserID) {
    	perpetualheaderService.deletePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
    			subMovementTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}