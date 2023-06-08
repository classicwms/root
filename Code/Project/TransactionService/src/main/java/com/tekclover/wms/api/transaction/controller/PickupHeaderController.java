package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

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

import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupHeader;
import com.tekclover.wms.api.transaction.service.PickupHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PickupHeader"}, value = "PickupHeader  Operations related to PickupHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PickupHeader ",description = "Operations related to PickupHeader ")})
@RequestMapping("/pickupheader")
@RestController
public class PickupHeaderController {
	
	@Autowired
	PickupHeaderService pickupheaderService;
	
    @ApiOperation(response = PickupHeader.class, value = "Get all PickupHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PickupHeader> pickupheaderList = pickupheaderService.getPickupHeaders();
		return new ResponseEntity<>(pickupheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PickupHeader.class, value = "Get a PickupHeader") // label for swagger 
	@GetMapping("/{pickupNumber}")
	public ResponseEntity<?> getPickupHeader(@PathVariable String pickupNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, 
			@RequestParam String partnerCode, @RequestParam Long lineNumber, @RequestParam String itemCode) {
    	PickupHeader pickupheader = 
    			pickupheaderService.getPickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, 
    					lineNumber, itemCode);
    	log.info("PickupHeader : " + pickupheader);
		return new ResponseEntity<>(pickupheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PickupHeader.class, value = "Search PickupHeader") // label for swagger
	@PostMapping("/findPickupHeader")
	public List<PickupHeader> findPickupHeader(@RequestBody SearchPickupHeader searchPickupHeader)
			throws Exception {
		return pickupheaderService.findPickupHeader(searchPickupHeader);
	}

	@ApiOperation(response = PickupHeader.class, value = "Search PickupHeader New") // label for swagger
	@PostMapping("/findPickupHeaderNew")
	public Stream<PickupHeader> findPickupHeaderNew(@RequestBody SearchPickupHeader searchPickupHeader)
			throws Exception {
		return pickupheaderService.findPickupHeaderNew(searchPickupHeader);
	}

    @ApiOperation(response = PickupHeader.class, value = "Create PickupHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPickupHeader(@Valid @RequestBody AddPickupHeader newPickupHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PickupHeader createdPickupHeader = pickupheaderService.createPickupHeader(newPickupHeader, loginUserID);
		return new ResponseEntity<>(createdPickupHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PickupHeader.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/{pickupNumber}")
	public ResponseEntity<?> patchPickupHeader(@PathVariable String pickupNumber, @RequestParam String warehouseId, 
			@RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody UpdatePickupHeader updatePickupHeader, 
			@RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PickupHeader createdPickupHeader = 
				pickupheaderService.updatePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
						pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader);
		return new ResponseEntity<>(createdPickupHeader , HttpStatus.OK);
	}

	@ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader") // label for swagger
	@PatchMapping("/update-assigned-picker")
	public ResponseEntity<?> patchAssignedPickerIdInPickupHeader(@Valid @RequestBody List<UpdatePickupHeader> updatePickupHeaderList,
											   @RequestParam("loginUserID") String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<PickupHeader> createdPickupHeader =
				pickupheaderService.patchAssignedPickerIdInPickupHeader( loginUserID, updatePickupHeaderList);
		return new ResponseEntity<>(createdPickupHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PickupHeader.class, value = "Delete PickupHeader") // label for swagger
	@DeleteMapping("/{pickupNumber}")
	public ResponseEntity<?> deletePickupHeader(@PathVariable String pickupNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String itemCode, @RequestParam String proposedStorageBin, 
			@RequestParam String proposedPackCode, @RequestParam String loginUserID) 
					throws IllegalAccessException, InvocationTargetException {
    	pickupheaderService.deletePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, 
    			lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}