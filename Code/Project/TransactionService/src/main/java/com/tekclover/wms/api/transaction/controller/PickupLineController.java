package com.tekclover.wms.api.transaction.controller;

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

import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupLine;
import com.tekclover.wms.api.transaction.service.PickupLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PickupLine"}, value = "PickupLine  Operations related to PickupLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PickupLine ",description = "Operations related to PickupLine ")})
@RequestMapping("/pickupline")
@RestController
public class PickupLineController {
	
	@Autowired
	PickupLineService pickuplineService;
	
    @ApiOperation(response = PickupLine.class, value = "Get all PickupLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PickupLine> pickuplineList = pickuplineService.getPickupLines();
		return new ResponseEntity<>(pickuplineList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PickupLine.class, value = "Get a PickupLine") // label for swagger 
	@GetMapping("/{actualHeNo}")
	public ResponseEntity<?> getPickupLine(@PathVariable String actualHeNo, @RequestParam String warehouseId, 
			@RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode) {
    	PickupLine pickupline = pickuplineService.getPickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
    			lineNumber, itemCode);
    	log.info("PickupLine : " + pickupline);
		return new ResponseEntity<>(pickupline, HttpStatus.OK);
	}
    
    @ApiOperation(response = PickupLine.class, value = "Get a PickupLine") // label for swagger 
	@GetMapping("/additionalBins")
	public ResponseEntity<?> getAdditionalBins(@RequestParam String warehouseId, @RequestParam String itemCode, 
			@RequestParam Long obOrdertypeId, @RequestParam String proposedPackBarCode, @RequestParam String proposedStorageBin) {
    	List<Inventory> additionalBins = 
    			pickuplineService.getAdditionalBins(warehouseId, itemCode, obOrdertypeId, proposedPackBarCode, proposedStorageBin);
    	log.info("additionalBins : " + additionalBins);
		return new ResponseEntity<>(additionalBins, HttpStatus.OK);
	}
    
	@ApiOperation(response = PickupLine.class, value = "Search PickupLine") // label for swagger
	@PostMapping("/findPickupLine")
	public List<PickupLine> findPickupLine(@RequestBody SearchPickupLine searchPickupLine)
			throws Exception {
		return pickuplineService.findPickupLine(searchPickupLine);
	}
    
    @ApiOperation(response = PickupLine.class, value = "Create PickupLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPickupLine(@Valid @RequestBody List<AddPickupLine> newPickupLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<PickupLine> createdPickupLine = pickuplineService.createPickupLine(newPickupLine, loginUserID);
		return new ResponseEntity<>(createdPickupLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = PickupLine.class, value = "Update PickupLine") // label for swagger
    @PatchMapping("/{actualHeNo}")
	public ResponseEntity<?> patchPickupLine(@PathVariable String actualHeNo, @RequestParam String warehouseId, 
			@RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode, 
			@RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
			@Valid @RequestBody UpdatePickupLine updatePickupLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PickupLine createdPickupLine = 
				pickuplineService.updatePickupLine(actualHeNo, warehouseId, preOutboundNo, refDocNumber, 
						partnerCode, lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode,
						loginUserID, updatePickupLine);
		return new ResponseEntity<>(createdPickupLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = PickupLine.class, value = "Delete PickupLine") // label for swagger
	@DeleteMapping("/{actualHeNo}")
	public ResponseEntity<?> deletePickupLine(@PathVariable String actualHeNo, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode, 
			@RequestParam String pickedStorageBin, @RequestParam String pickedPackCode, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
    	pickuplineService.deletePickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
    			pickupNumber, itemCode, actualHeNo, pickedStorageBin, pickedPackCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}