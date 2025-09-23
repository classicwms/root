package com.tekclover.wms.api.inbound.orders.controller;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.v2.PerpetualHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.inbound.orders.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.orders.service.*;
import com.tekclover.wms.api.inbound.orders.service.namratha.SalesOrderServiceV4;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"OutboundOrder"}, value = "OutboundOrder Operations related to OutboundOrderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundOrder", description = "Operations related to OutboundOrder ")})
@RequestMapping("/outboundorder")
@RestController
public class OutboundOrderController {

    @Autowired
    SalesOrderService salesOrderService;
    @Autowired
    SalesInvoiceService salesInvoiceService;
    @Autowired
    PurchaseReturnService purchaseReturnService;
    @Autowired
    ShipmentOrderService shipmentOrderService;
    @Autowired
    InterWarehouseTransferOutService interWarehouseTransferOutService;

    @Autowired
    DbConfigRepository dbConfigRepository;
    @Autowired
    SalesOrderServiceV4 salesOrderServiceV4;

    @Autowired
    SalesOrderServiceV6 salesOrderServiceV6;


    //Pick_List
    @ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
    @PostMapping("/outbound/salesorderv2")
    public ResponseEntity<?> postSalesOrderV2List(@Valid @RequestBody List<SalesOrderV2> salesOrder)
            throws IllegalAccessException, InvocationTargetException {
        try {
            for (SalesOrderV2 salesOrderV2 : salesOrder) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(salesOrderV2.getSalesOrderHeader().getCompanyCode(), salesOrderV2.getSalesOrderHeader().getBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<SalesOrderV2> response = salesOrderService.createSalesOrderList(salesOrder);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Sales Invoicev2
    @ApiOperation(response = SalesInvoice.class, value = "Create SalesInvoicev2") //label for Swagger
    @PostMapping("/outbound/salesinvoicev2")
    public ResponseEntity<?> createSalesInvoicev2(@Valid @RequestBody List<SalesInvoice> salesInvoice)
            throws IllegalAccessException, InvocationTargetException {
        try {
            for (SalesInvoice salesInvoice1 : salesInvoice) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(salesInvoice1.getCompanyCode(), salesInvoice1.getBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<OutboundOrderV2> response = salesInvoiceService.createSalesInvoiceList(salesInvoice);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Purchase Return V2
    @ApiOperation(response = ReturnPOV2.class, value = "Return PO v2") // label for swagger
    @PostMapping("/outbound/returnpo/V2")
    public ResponseEntity<?> postReturnPOList(@Valid @RequestBody List<ReturnPOV2> returnPO)
            throws IllegalAccessException, InvocationTargetException {
        try {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(returnPO.get(0).getReturnPOHeader().getCompanyCode(), returnPO.get(0).getReturnPOHeader().getBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            List<ReturnPOV2> response = purchaseReturnService.createPurchaseRetrunList(returnPO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }

    }

    @ApiOperation(response = ShipmentOrderV2.class, value = "Create Shipment Order v2") // label for swagger
    @PostMapping("/outbound/shipmentOrder/v2")
    public ResponseEntity<?> postShipmenOrder(@Valid @RequestBody List<ShipmentOrderV2> shipmenOrder)
            throws IllegalAccessException, InvocationTargetException {
        try {
//            for (ShipmentOrderV2 shipmentOrderV2 : shipmenOrder) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(shipmenOrder.get(0).getSoHeader().getCompanyCode(), shipmenOrder.get(0).getSoHeader().getBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
//            }
            List<ShipmentOrderV2> response = shipmentOrderService.createShipmentOrderList(shipmenOrder);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    //Inter warehouse Transfer out v2
    @ApiOperation(response = InterWarehouseTransferOutV2.class, value = "Inter Warehouse Transfer Out v2")
    @PostMapping("/outbound/interwarehousetransferout/v2")
    public ResponseEntity<?> postInterWarehouseTransferOut(@Valid @RequestBody List<InterWarehouseTransferOutV2> itw)
            throws IllegalAccessException, InvocationTargetException {
        try {
            log.info("InterWarehouseTransferOutV2 --------> {}", itw);
//            for (InterWarehouseTransferOutV2 itw : interWarehouseTransfer) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(itw.get(0).getInterWarehouseTransferOutHeader().getFromCompanyCode(),
                        itw.get(0).getInterWarehouseTransferOutHeader().getFromBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
//            }
            List<InterWarehouseTransferOutV2> response = interWarehouseTransferOutService.createInterwarehouseList(itw);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Upload API
    @ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
    @PostMapping("/outbound/upload/salesorderv2")
    public ResponseEntity<?> postSalesOrderV2(@Valid @RequestBody List<SalesOrderV2> salesOrders) {
        try {
            log.info("------------salesOrders : " + salesOrders);
            WarehouseApiResponse response = new WarehouseApiResponse();
            for (SalesOrderV2 salesOrder : salesOrders) {
                DataBaseContextHolder.setCurrentDb("MT");
                SalesOrderV2 createdSalesOrder = salesOrderService.postSalesOrderV2(salesOrder);
                if (createdSalesOrder != null) {
                    response.setStatusCode("200");
                    response.setMessage("Success");
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("SalesOrder order Error: " + salesOrders);
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
    @PostMapping("/outbound/upload/salesorderv6")
    public ResponseEntity<?> postSalesOrderV6(@Valid @RequestBody List<SalesOrderV2> salesOrders,@RequestParam Long orderTypeId) {
        try {
            log.info("------------salesOrders : " + salesOrders);
            WarehouseApiResponse response = new WarehouseApiResponse();
            DataBaseContextHolder.setCurrentDb("MT");
//            for (SalesOrderV2 salesOrder : salesOrders) {
//                DataBaseContextHolder.clear();
//                SalesOrderV2 outboundOrder = salesOrderServiceV6.postSalesOrderV6(salesOrder,orderTypeId);
//                if (outboundOrder != null) {
//                    response.setStatusCode("200");
//                    response.setMessage("Success");
//                }
//            }
            String routingDb = dbConfigRepository.getDbName(salesOrders.get(0).getSalesOrderHeader().getCompanyCode(),
                    salesOrders.get(0).getSalesOrderHeader().getBranchCode(), salesOrders.get(0).getSalesOrderHeader().getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            salesOrders.forEach(salesOrder -> {
                try {
                    SalesOrderV2 outboundOrder = salesOrderServiceV6.postSalesOrderV6(salesOrder,orderTypeId);
                    if (outboundOrder != null) {
                        response.setStatusCode("200");
                        response.setMessage("Success");
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

            //RM
            if (orderTypeId.equals(3L)){
                salesOrderServiceV6.outboundProcess(salesOrders);
            }
            //FG / Transfer Order
            if (orderTypeId.equals(1L) || orderTypeId.equals(4L)){
                salesOrderServiceV6.outboundProcessV6(salesOrders,orderTypeId);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("SalesOrder order Error: " + salesOrders);
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }
    //Pick_List
    @ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
    @PostMapping("/outbound/salesorderv4")
    public ResponseEntity<?> postSalesOrderV4(@Valid @RequestBody SalesOrderV2 salesOrder)
            throws IllegalAccessException, InvocationTargetException {
              try {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(salesOrder.getSalesOrderHeader().getCompanyCode(),
                        salesOrder.getSalesOrderHeader().getBranchCode(), salesOrder.getSalesOrderHeader().getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            SalesOrderV2 response = salesOrderServiceV4.createSalesOrderList(salesOrder);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Pick_List
    @ApiOperation(response = SalesOrderV2.class, value = "stockcount perpetual") // label for swagger
    @PostMapping("/stockcount/perpetual")
    public ResponseEntity<?> postSalesOrderV4(@Valid @RequestBody Perpetual perpetual)
            throws IllegalAccessException, InvocationTargetException {
        try {
            log.info("Perpetual Input from Connector Fahaheel -----> {}", perpetual);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(perpetual.getPerpetualHeaderV1().getCompanyCode(), perpetual.getPerpetualHeaderV1().getBranchCode(), "300");
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            WarehouseApiResponse response = new WarehouseApiResponse();
            PerpetualHeaderEntityV2 perpetualHeaderEntityV2 = salesOrderService.processStockCountReceived(perpetual);
            if (perpetualHeaderEntityV2 != null) {
                response.setStatusCode("200");
                response.setMessage("Success");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("PerpetualHeaderEntityV2 order Error ");
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StockAdjustment.class, value = "Create StockAdjustment") //label for Swagger
    @PostMapping("/stockAdjustment")
    public ResponseEntity<?> createStockAdjustment(@Valid @RequestBody StockAdjustment stockAdjustment)
            throws IllegalAccessException, InvocationTargetException {
        try {
            log.info("stockAdjustment Input from Connector Fahaheel -----> {}", stockAdjustment);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(), "300");
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createdStockAdjustment = salesOrderService.postStockAdjustment(stockAdjustment);
            if (createdStockAdjustment != null) {
                WarehouseApiResponse response = new WarehouseApiResponse();
                response.setStatusCode("200");
                response.setMessage("Success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("StockAdjustment order Error: " + stockAdjustment);
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
        return null;
    }
    
    //------------------------------------------------FG----------------------------------------------------------------

    @ApiOperation(response = OrderManagementLineV2.class, value = "Process Outbound") // label for swagger
    @PostMapping("fg/order/process")
    public ResponseEntity<?> postOutbound(@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,@RequestParam String warehouseId,
                                          @RequestParam String preOutboundNo,@RequestParam String refDocNumber,@RequestParam String loginUserID) {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId,plantId,warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OrderManagementLineV2> orderManagementLineV2 = salesOrderServiceV6.createOrderManagementLineV6(companyCodeId,plantId,
                    languageId,warehouseId,preOutboundNo,refDocNumber,loginUserID);
            return new ResponseEntity<>(orderManagementLineV2, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Outbound order Error ");
            e.printStackTrace();
            return new ResponseEntity<>(e,HttpStatus.EXPECTATION_FAILED);
        } finally {
            DataBaseContextHolder.clear();
        }

    }

}