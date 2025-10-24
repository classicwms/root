package com.tekclover.wms.api.inbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.UpdateInboundOrder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.inbound.transaction.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Orders"}, value = "Orders  Operations related to OrdersController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Orders ", description = "Operations related to Orders ")})
@RequestMapping("/orders")
@RestController
public class OrderController {

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    OrderService orderService;

//    @ApiOperation(response = InboundOrder.class, value = "Get all InboundOrder details") // label for swagger
//    @GetMapping("/inbound")
//    public ResponseEntity<?> getAll() {
//        List<InboundOrder> inboundOrderList = orderService.getInboundOrders();
//        return new ResponseEntity<>(inboundOrderList, HttpStatus.OK);
//    }

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

    @ApiOperation(response = InboundIntegrationLog.class, value = "Get Failed Orders") // label for swagger 
    @GetMapping("/inbound/orders/failed")
    public ResponseEntity<?> getFailedInbounOrders() throws Exception {
        List<InboundIntegrationLog> orders = orderService.getFailedInboundOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @ApiOperation(response = InboundOrder.class, value = "Create InboundOrder") // label for swagger
    @PostMapping("/inbound")
    public ResponseEntity<?> postOrders(@RequestBody InboundOrder newInboundOrder)
            throws IllegalAccessException, InvocationTargetException {
        InboundOrder createdInboundOrder = orderService.createInboundOrders(newInboundOrder);
        return new ResponseEntity<>(createdInboundOrder, HttpStatus.OK);
    }


    /*-----------------------Order Confirmation----------------------------------------------*/
    @ApiOperation(response = InboundOrderV2.class, value = "Find InboundOrderV2 details")
    // label for swagger
    @PostMapping("/findInboundOrderV2")
    public ResponseEntity<?> findInboundOrderV2(@RequestBody FindInboundOrderV2 findInboundOrderV2) throws ParseException {
        log.info("FindInboundOrderV2 details ------> {}", findInboundOrderV2);
        List<InboundOrderV2> inboundOrderV2List = null;
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName1(findInboundOrderV2.getCompanyCode(),
                findInboundOrderV2.getPlantId(),findInboundOrderV2.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//        DataBaseContextHolder.setCurrentDb("MT");
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        inboundOrderV2List = orderService.findInboundOrderV2(findInboundOrderV2);
        return new ResponseEntity<>(inboundOrderV2List, HttpStatus.OK);
    }


    @ApiOperation(response = InboundOrderLinesV2.class, value = "Find InboundOrderLinesV2 details")
    // label for swagger
    @PostMapping("/findInboundOrderLinesV2")
    public ResponseEntity<?> findInboundOrderLinesV2(@RequestBody FindInboundOrderLineV2 findInboundOrderLineV2) throws
            ParseException {
        List<InboundOrderLinesV2> inboundOrderLinesV2List = orderService.findInboundOrderLineV2(findInboundOrderLineV2);
        return new ResponseEntity<>(inboundOrderLinesV2List, HttpStatus.OK);
    }


    //=============================================Update===========================================================
    @ApiOperation(response = InboundOrder.class, value = "Update InboundOrder details")
    @PatchMapping("update/inbound")
    public ResponseEntity<?>updateInboundOrder(@RequestParam String orderId,@RequestBody UpdateInboundOrder updateInboundOrder){

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        InboundOrder inboundOrders = orderService.updateInboundOrder(orderId, updateInboundOrder);
        return new ResponseEntity<>(inboundOrders,HttpStatus.OK);
    }


    //=======================================Delete=====================================================

    @ApiOperation(response = InboundOrder.class, value = "Delete InboundOrder details")
    @DeleteMapping("delete/inbound")
    public ResponseEntity<?> deleteInboundOrder(@RequestParam String orderId){

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        orderService.deleteInboundOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}