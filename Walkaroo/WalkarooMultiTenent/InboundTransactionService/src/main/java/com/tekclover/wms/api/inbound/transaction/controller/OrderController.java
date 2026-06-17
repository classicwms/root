package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.FindInboundOrderLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.FindInboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.OrderService;
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
@SwaggerDefinition(tags = {@Tag(name = "Orders ", description = "Operations related to Orders ")})
@RequestMapping("/orders")
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

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
        try {
            String currentDB = baseService.getDataBase(newInboundOrder.getBranchCode());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundOrder createdInboundOrder = orderService.createInboundOrders(newInboundOrder);
            return new ResponseEntity<>(createdInboundOrder, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /*-----------------------Order Confirmation----------------------------------------------*/
    @ApiOperation(response = IntegrationApiResponse.class, value = "IntegrationApiResponse") // label for swagger
    @GetMapping("/confirmation/{orderId}")
    public ResponseEntity<?> getOrders(@PathVariable String orderId)
            throws IllegalAccessException, InvocationTargetException {
        List<IntegrationApiResponse> integrationApiResponseList = orderService.getConfirmationOrder(orderId);
        return new ResponseEntity<>(integrationApiResponseList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundOrderV2.class, value = "Find InboundOrderV2 details")
    // label for swagger
    @PostMapping("/findInboundOrderV2")
    public ResponseEntity<?> findInboundOrderV2(@RequestBody FindInboundOrderV2 findInboundOrderV2) throws ParseException {

        try {
            String currentDB = baseService.getDataBase(findInboundOrderV2.getBranchCode().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<InboundOrderV2> inboundOrderV2List = orderService.findInboundOrderV2(findInboundOrderV2);
            return new ResponseEntity<>(inboundOrderV2List, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundOrderLinesV2.class, value = "Find InboundOrderLinesV2 details")
    // label for swagger
    @PostMapping("/findInboundOrderLinesV2")
    public ResponseEntity<?> findInboundOrderLinesV2(@RequestBody FindInboundOrderLineV2 findInboundOrderLineV2) throws ParseException {
        List<InboundOrderLinesV2> inboundOrderLinesV2List = orderService.findInboundOrderLineV2(findInboundOrderLineV2);
        return new ResponseEntity<>(inboundOrderLinesV2List, HttpStatus.OK);
    }

    // ======================================== Grafana ================================= //

    @ApiOperation(response = Integer.class, value = "Get Inbound Order Count")
    @GetMapping("/grafana/orderCount")
    public ResponseEntity<?> getPreInboundOrderCount(@RequestParam String warehouseId,
                                                     @RequestParam String companyCode,
                                                     @RequestParam String plantId,
                                                     @RequestParam String languageId) throws ParseException {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        int count = orderService.getInboundOrderCount(warehouseId, companyCode, plantId, languageId);

        return new ResponseEntity<>(count, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Integer.class, value = "Get Inbound Order Line Count")
    @GetMapping("/grafana/orderLineCount")
    public ResponseEntity<?> getPreInboundLineOrderCount(@RequestParam String warehouseId,
                                                         @RequestParam String companyCode,
                                                         @RequestParam String plantId,
                                                         @RequestParam String languageId) throws ParseException {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        int count = orderService.getInboundOrderLineCount(warehouseId, companyCode, plantId, languageId);

        return new ResponseEntity<>(count, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }

    }

    @ApiOperation(response = Integer.class, value = "Get Putaway Line Count")
    @GetMapping("/grafana/putawayLineCount")
    public ResponseEntity<?> getPutawayLineOrderCount(@RequestParam String warehouseId,
                                                      @RequestParam String companyCode,
                                                      @RequestParam String plantId,
                                                      @RequestParam String languageId) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);


        int count = orderService.getPutawayLineCount(warehouseId, companyCode, plantId, languageId);

        return new ResponseEntity<>(count, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }

    }

    @ApiOperation(response = Integer.class, value = "Get Confirmed Inbound Order Count")
    @GetMapping("/grafana/inboundorderCount")
    public ResponseEntity<?> getPreInboundOrderConfirmedCount(@RequestParam String warehouseId,
                                                              @RequestParam String companyCode,
                                                              @RequestParam String plantId,
                                                              @RequestParam String languageId) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);

        int count = orderService.getInboundHeaderConfirmCount(warehouseId, companyCode, plantId, languageId);

        return new ResponseEntity<>(count, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }

    }

    @ApiOperation(response = Integer.class, value = "Get IB Queued Orders Count")
    @GetMapping("/grafana/ibqueuedOrdersCount")
    public ResponseEntity<?> getQueuedOrdersCount(@RequestParam String warehouseId,
                                                  @RequestParam String companyCode,
                                                  @RequestParam String plantId,
                                                  @RequestParam String languageId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB {}", currentDB);

            int count = orderService.getIbQueuedOrdersCount(
                    warehouseId,
                    companyCode,
                    plantId,
                    languageId);

            return new ResponseEntity<>(count, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Integer.class, value = "Get IB Failed Orders Count")
    @GetMapping("/grafana/ibfailedOrdersCount")
    public ResponseEntity<?> getFailedOrdersCount(@RequestParam String warehouseId,
                                                  @RequestParam String companyCode,
                                                  @RequestParam String plantId,
                                                  @RequestParam String languageId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB {}", currentDB);

            int count = orderService.getIbFailedOrdersCount(
                    warehouseId,
                    companyCode,
                    plantId,
                    languageId);

            return new ResponseEntity<>(count, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}