package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AddOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AssignPicker;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.SearchOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementLine;
import com.tekclover.wms.api.transaction.service.OrderManagementLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"OrderManagementLine"}, value = "OrderManagementLine  Operations related to OrderManagementLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OrderManagementLine ",description = "Operations related to OrderManagementLine ")})
@RequestMapping("/ordermanagementline")
@RestController
public class OrderManagementLineController {
	
	@Autowired
	OrderManagementLineService ordermangementlineService;
	
    @ApiOperation(response = OrderManagementLine.class, value = "Get all OrderManagementLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<OrderManagementLine> ordermangementlineList = ordermangementlineService.getOrderManagementLines();
		return new ResponseEntity<>(ordermangementlineList, HttpStatus.OK);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "Get a OrderManagementLine") // label for swagger 
	@GetMapping("/{refDocNumber}")
	public ResponseEntity<?> getOrderManagementLine(@PathVariable String refDocNumber, @RequestParam String warehouseId, 
			@RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode) {
    	OrderManagementLine ordermangementline = 
    			ordermangementlineService.getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
    					lineNumber, itemCode, proposedStorageBin, proposedPackCode);
    	log.info("OrderManagementLine : " + ordermangementline);
		return new ResponseEntity<>(ordermangementline, HttpStatus.OK);
	}
    
	@ApiOperation(response = OrderManagementLine.class, value = "Search OrderManagementLine") // label for swagger
	@PostMapping("/findOrderManagementLine")
	public List<OrderManagementLine> findOrderManagementLine(@RequestBody SearchOrderManagementLine searchOrderManagementLine)
			throws Exception {
		return ordermangementlineService.findOrderManagementLine(searchOrderManagementLine);
	}
	//Streaming
	@ApiOperation(response = OrderManagementLine.class, value = "Search OrderManagementLine New") // label for swagger
	@PostMapping("/findOrderManagementLineNew")
	public Stream<OrderManagementLine> findOrderManagementLineNew(@RequestBody SearchOrderManagementLine searchOrderManagementLine)
			throws Exception {
		return ordermangementlineService.findOrderManagementLineNew(searchOrderManagementLine);
	}

    @ApiOperation(response = OrderManagementLine.class, value = "Create OrderManagementLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postOrderManagementLine(@Valid @RequestBody AddOrderManagementLine newOrderManagementLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine createdOrderManagementLine = 
				ordermangementlineService.createOrderManagementLine(newOrderManagementLine, loginUserID);
		return new ResponseEntity<>(createdOrderManagementLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "UnAllocate") // label for swagger
    @PatchMapping("/unallocate")
	public ResponseEntity<?> unallocateOrderManagementLine(@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine updatedOrderManagementLine = 
				ordermangementlineService.doUnAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
						itemCode, proposedStorageBin, proposedPackBarCode, loginUserID);
		return new ResponseEntity<>(updatedOrderManagementLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
    @PatchMapping("/allocate")
	public ResponseEntity<?> allocateOrderManagementLine(@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String loginUserID) 
					throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine updatedOrderManagementLine = 
				ordermangementlineService.doAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
						itemCode, loginUserID);
		return new ResponseEntity<>(updatedOrderManagementLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
    @PatchMapping("/assignPicker")
	public ResponseEntity<?> assignPicker(@RequestBody List<AssignPicker> assignPicker, @RequestParam String assignedPickerId,
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<OrderManagementLine> updatedOrderManagementLine = 
				ordermangementlineService.doAssignPicker(assignPicker, assignedPickerId, loginUserID);
		return new ResponseEntity<>(updatedOrderManagementLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "Update OrderMangementLine") // label for swagger
    @PatchMapping("/{refDocNumber}")
	public ResponseEntity<?> patchOrderMangementLine(@PathVariable String refDocNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode,
			@Valid @RequestBody UpdateOrderManagementLine updateOrderMangementLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
    	OrderManagementLine createdOrderMangementLine = 
				ordermangementlineService.updateOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, 
						partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, updateOrderMangementLine);
		return new ResponseEntity<>(createdOrderMangementLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "Delete OrderManagementLine") // label for swagger
	@DeleteMapping("/{refDocNumber}")
	public ResponseEntity<?> deleteOrderManagementLine(@PathVariable String refDocNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, 
			@RequestParam String proposedPackCode, @RequestParam String loginUserID) {
    	ordermangementlineService.deleteOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
    			lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    //----------------------Update--Ref9&10------------------------------------------------------------
    @ApiOperation(response = OrderManagementLine.class, value = "Get a OrderManagementLine") // label for swagger 
   	@GetMapping("/updateRefFields")
   	public ResponseEntity<?> updateRefFields() {
       	ordermangementlineService.updateRef9ANDRef10();
   		return new ResponseEntity<>(HttpStatus.OK);
   	}

	//===================================================Allocation - UnAllocation=======================================================

	@ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
	@PatchMapping("/allocate/patch")
	public ResponseEntity<?> allocateOrderManagementLineV2(@Valid @RequestBody List<OrderManagementLine> orderManagementLineV2, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		List<OrderManagementLine> updatedOrderManagementLine =
				ordermangementlineService.doAllocation(orderManagementLineV2, loginUserID);
		return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
	}

	//Patch Update UnAllocation
	@ApiOperation(response = OrderManagementLine.class, value = "UnAllocate") // label for swagger
	@PatchMapping("/unallocate/patch")
	public ResponseEntity<?> unallocateOrderManagementLineV2(@Valid @RequestBody List<OrderManagementLine> orderManagementLineV2,
															 @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		List<OrderManagementLine> updatedOrderManagementLine =
				ordermangementlineService.doUnAllocation(orderManagementLineV2, loginUserID);
		return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
	}
}