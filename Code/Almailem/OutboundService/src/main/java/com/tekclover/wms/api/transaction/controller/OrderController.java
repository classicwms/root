package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationLog;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ShipmentOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.FindOutboundOrderLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.FindOutboundOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.transaction.service.OrderService;
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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"Orders"}, value = "Orders  Operations related to OrdersController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Orders ",description = "Operations related to Orders ")})
@RequestMapping("/orders")
@RestController
public class OrderController {
	
	@Autowired
	OrderService orderService;
	
    //--------------------------------Outbound--------------------------------------------
    @ApiOperation(response = OutboundOrder.class, value = "Get all InboundOrder details") // label for swagger
   	@GetMapping("/outbound")
   	public ResponseEntity<?> getOBAllOrders() {
   		List<OutboundOrder> outboundOrderList = orderService.getOBOrders();
   		return new ResponseEntity<>(outboundOrderList, HttpStatus.OK);
   	}
       
    @ApiOperation(response = OutboundOrder.class, value = "Get a Orders") // label for swagger 
   	@GetMapping("/outbound/orders/{orderId}")
   	public ResponseEntity<?> getOBOrdersById(@PathVariable String orderId) {
       	OutboundOrder orders = orderService.getOBOrderById(orderId);
   		return new ResponseEntity<>(orders, HttpStatus.OK);
   	}
       
    @ApiOperation(response = OutboundOrder.class, value = "Get a Orders") // label for swagger 
   	@GetMapping("/outbound/orders/byDate")
   	public ResponseEntity<?> getOBOrdersByDate(@RequestParam String orderStartDate, @RequestParam String orderEndDate) throws ParseException {
       	List<OutboundOrder> orders = orderService.getOBOrderByDate(orderStartDate, orderEndDate);
   		return new ResponseEntity<>(orders, HttpStatus.OK);
   	}
    
    @ApiOperation(response = OutboundIntegrationLog.class, value = "Get Failed Orders") // label for swagger 
   	@GetMapping("/outbound/orders/failed")
   	public ResponseEntity<?> getFailedOutbounOrders() throws Exception {
       	List<OutboundIntegrationLog> orders = orderService.getFailedOutboundOrders();
   		return new ResponseEntity<>(orders, HttpStatus.OK);
   	}
       
    @ApiOperation(response = OutboundOrder.class, value = "Create OutboundOrder") // label for swagger
   	@PostMapping("/outbound")
   	public ResponseEntity<?> postOrders(@RequestBody OutboundOrder newOutboundOrder) 
   			throws IllegalAccessException, InvocationTargetException {
       	OutboundOrder createdOutboundOrder = orderService.createOutboundOrders(newOutboundOrder);
   		return new ResponseEntity<>(createdOutboundOrder , HttpStatus.OK);
   	}
    
    @ApiOperation(response = GrHeader.class, value = "Delete GrHeader") // label for swagger
   	@DeleteMapping("/outbound/so/{orderId}")
   	public ResponseEntity<?> deleteGrHeader(@PathVariable String orderId) {
    	orderService.deleteObOrder(orderId);
   		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
   	}
    
    /*-----------------------Reprocess------------------------------------------------------*/
    @ApiOperation(response = OutboundOrder.class, value = "Create OutboundOrder") // label for swagger
   	@GetMapping("/outbound/requeue/{orderId}")
   	public ResponseEntity<?> pushOrders(@PathVariable String orderId) 
   			throws IllegalAccessException, InvocationTargetException {
       	ShipmentOrder createdOutboundOrder = orderService.pushOrder(orderId);
   		return new ResponseEntity<>(createdOutboundOrder , HttpStatus.OK);
   	}
    
    /*-----------------------Order Confirmation----------------------------------------------*/
    @ApiOperation(response = IntegrationApiResponse.class, value = "IntegrationApiResponse") // label for swagger
   	@GetMapping("/confirmation/{orderId}")
   	public ResponseEntity<?> getOrders(@PathVariable String orderId) 
   			throws IllegalAccessException, InvocationTargetException {
    	List<IntegrationApiResponse> integrationApiResponseList = orderService.getConfirmationOrder(orderId);
   		return new ResponseEntity<>(integrationApiResponseList , HttpStatus.OK);
   	}

	@ApiOperation(response = OutboundOrderV2.class, value = "Find OutboundOrderV2 details")
	// label for swagger
	@PostMapping("/findOutboundOrderV2")
	public ResponseEntity<?> findOutboundOrderV2(@RequestBody FindOutboundOrderV2 findOutboundOrderV2) throws ParseException {
		List<OutboundOrderV2> outboundOrderV2List = orderService.findOutboundOrderV2(findOutboundOrderV2);
		return new ResponseEntity<>(outboundOrderV2List, HttpStatus.OK);
	}

	@ApiOperation(response = OutboundOrderLineV2.class, value = "Find OutboundOrderLineV2 details")
	// label for swagger
	@PostMapping("/findOutboundOrderLineV2")
	public ResponseEntity<?> findOutboundOrderLineV2(@RequestBody FindOutboundOrderLineV2 findOutboundOrderLineV2) throws ParseException {
		List<OutboundOrderLineV2> outboundOrderLineV2List = orderService.findOutboundOrderLineV2(findOutboundOrderLineV2);
		return new ResponseEntity<>(outboundOrderLineV2List, HttpStatus.OK);
	}

}