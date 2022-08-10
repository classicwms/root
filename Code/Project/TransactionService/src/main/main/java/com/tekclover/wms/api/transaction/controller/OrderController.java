package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrder;
import com.tekclover.wms.api.transaction.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Orders"}, value = "Orders  Operations related to OrdersController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Orders ",description = "Operations related to Orders ")})
@RequestMapping("/orders")
@RestController
public class OrderController {
	
	@Autowired
	OrderService orderService;
	
    @ApiOperation(response = InboundOrder.class, value = "Get all InboundOrder details") // label for swagger
	@GetMapping("/inbound")
	public ResponseEntity<?> getAll() {
		List<InboundOrder> inboundOrderList = orderService.getInboundOrders();
		return new ResponseEntity<>(inboundOrderList, HttpStatus.OK);
	}
    
    @ApiOperation(response = InboundOrder.class, value = "Get a Orders") // label for swagger 
	@GetMapping("/inbound/orders/{orderId}")
	public ResponseEntity<?> getOrdersById(@PathVariable String orderId) {
    	InboundOrder orders = orderService.getOrderById(orderId);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
    
    @ApiOperation(response = InboundOrder.class, value = "Get a Orders") // label for swagger 
	@GetMapping("/inbound/orders/{orderDate}/date")
	public ResponseEntity<?> getOrdersByDate(@PathVariable String orderDate) throws ParseException {
    	List<InboundOrder> orders = orderService.getOrderByDate(orderDate);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
    
    @ApiOperation(response = InboundOrder.class, value = "Create InboundOrder") // label for swagger
	@PostMapping("/inbound")
	public ResponseEntity<?> postOrders(@RequestBody InboundOrder newInboundOrder) 
			throws IllegalAccessException, InvocationTargetException {
    	InboundOrder createdInboundOrder = orderService.createInboundOrders(newInboundOrder);
		return new ResponseEntity<>(createdInboundOrder , HttpStatus.OK);
	}
    
    //--------------------------------Outbound--------------------------------------------
    @ApiOperation(response = InboundOrder.class, value = "Get all InboundOrder details") // label for swagger
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
   	@GetMapping("/outbound/orders/{orderDate}/date")
   	public ResponseEntity<?> getOBOrdersByDate(@PathVariable String orderDate) throws ParseException {
       	List<OutboundOrder> orders = orderService.getOBOrderByDate(orderDate);
   		return new ResponseEntity<>(orders, HttpStatus.OK);
   	}
       
       @ApiOperation(response = OutboundOrder.class, value = "Create OutboundOrder") // label for swagger
   	@PostMapping("/outbound")
   	public ResponseEntity<?> postOrders(@RequestBody OutboundOrder newOutboundOrder) 
   			throws IllegalAccessException, InvocationTargetException {
       	OutboundOrder createdOutboundOrder = orderService.createOutboundOrders(newOutboundOrder);
   		return new ResponseEntity<>(createdOutboundOrder , HttpStatus.OK);
   	}
}