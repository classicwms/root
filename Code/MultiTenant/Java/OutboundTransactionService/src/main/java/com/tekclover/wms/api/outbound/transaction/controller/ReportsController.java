package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.deliveryline.DeliveryLine;
import com.tekclover.wms.api.outbound.transaction.model.inventory.Inventory;
import com.tekclover.wms.api.outbound.transaction.model.outbound.OutboundReversalInput;
import com.tekclover.wms.api.outbound.transaction.model.report.*;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.outbound.transaction.service.ReportsService;
import com.tekclover.wms.api.outbound.transaction.model.impl.StockReportImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"Reports"}, value = "Reports  Operations related to ReportsController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Reports ", description = "Operations related to Reports ")})
@RequestMapping("/reports")
@RestController
public class ReportsController {

    @Autowired
    ReportsService reportsService;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    //Get All Stock Reports
    @ApiOperation(response = Inventory.class, value = "Get All Stock Reports") // label for swagger
    @GetMapping("/stockReport-all")
    public ResponseEntity<?> getAllStockReport(@RequestParam List<String> languageId,
                                               @RequestParam List<String> companyCodeId,
                                               @RequestParam List<String> plantId,
                                               @RequestParam List<String> warehouseId,
                                               @RequestParam(required = false) List<String> itemCode,
                                               @RequestParam(required = false) String itemText,
                                               @RequestParam(required = false) List<String> manufacturerName,
                                               @RequestParam(required = true) String stockTypeText) {

        List<StockReport> stockReportList = reportsService.getAllStockReport(languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, itemText, stockTypeText);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }


    @ApiOperation(response = Inventory.class, value = "Get All Stock Reports New") // label for swagger
    @PostMapping("/v2/stockReport-all")
    public ResponseEntity<?> getAllStockReportV2(@Valid @RequestBody SearchStockReport searchStockReport) {

        List<StockReportImpl> stockReportList = reportsService.stockReport(searchStockReport);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }

    /**
     * Api changes from Stream to list
     * 10-06-2025 Aakash Vinayak
     *
     * @param searchStockReport
     * @return
     */
    @ApiOperation(response = StockReportOutput.class, value = "Get All Stock Reports StoredProcedure")
    // label for swagger
    @PostMapping("/v2/stockReportSP")
    public ResponseEntity<?> getAllStockReportV2SP(@Valid @RequestBody SearchStockReportInput searchStockReport) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchStockReport.getCompanyCodeId(), searchStockReport.getPlantId(), searchStockReport.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StockReportOutput> stockReportList = null;
            if (routingDb != null) {
                switch(routingDb){
                    case "REEFERON":
                        stockReportList = reportsService.stockReportUsingStoredProcedureV5(searchStockReport);
                        break;
                    default:
                        stockReportList = reportsService.stockReportUsingStoredProcedure(searchStockReport);
                        break;
                }
            }
            return new ResponseEntity<>(stockReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    /*
     * Inventory Report
     */
    @ApiOperation(response = Inventory.class, value = "Get Stock Report") // label for swagger 
    @GetMapping("/inventoryReport")
    public ResponseEntity<?> getInventoryReport(@RequestParam List<String> warehouseId,
                                                @RequestParam(required = false) List<String> itemCode,
                                                @RequestParam(required = false) String storageBin,
                                                @RequestParam(required = false) String stockTypeText,
                                                @RequestParam(required = false) List<String> stSectionIds,
                                                @RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(defaultValue = "itemCode") String sortBy) {

        Page<InventoryReport> inventoryReportList =
                reportsService.getInventoryReport(warehouseId, itemCode, storageBin, stockTypeText, stSectionIds,
                        pageNo, pageSize, sortBy);
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }


//    @ApiOperation(response = Inventory.class, value = "Get Stock Report") // label for swagger 
//   	@GetMapping("/inventoryReport/schedule")
//   	public ResponseEntity<?> getInventoryReport() throws Exception {
//    	reportsService.exportXlsxFile();
//   		return new ResponseEntity<>(HttpStatus.OK);
//   	}

    @ApiOperation(response = InventoryReport[].class, value = "Get Stock Report") // label for swagger 
    @GetMapping("/inventoryReport/all")
    public ResponseEntity<?> getInventoryReportAll() throws Exception {
        List<InventoryReport> inventoryReportList = reportsService.generateInventoryReport();
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }

    /*
     * Order status report
     */
    @ApiOperation(response = OrderStatusReport.class, value = "Get StockMovement Report") // label for swagger 
    @PostMapping("/orderStatusReport")
    public ResponseEntity<?> getOrderStatusReport(@RequestBody SearchOrderStatusReport request)
            throws ParseException, java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(request.getCompanyCodeId(), request.getPlantId(), request.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OrderStatusReport> orderStatusReportList = reportsService.getOrderStatusReport(request);
            return new ResponseEntity<>(orderStatusReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /*
     * Shipment Delivery
     */
    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report") // label for swagger 
    @GetMapping("/shipmentDelivery")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber) throws ParseException, java.text.ParseException {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<ShipmentDeliveryReport> shipmentDeliveryList = reportsService.getShipmentDeliveryReport(warehouseId,
                    fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber);
            return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report v2")
    // label for swagger
    @GetMapping("/v2/shipmentDelivery")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String languageId, @RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber) throws ParseException, java.text.ParseException {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<ShipmentDeliveryReport> shipmentDeliveryList = reportsService.getShipmentDeliveryReportV2(companyCodeId, plantId, languageId, warehouseId,
                    fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber);
            return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report v2 preOutboundNo added")
    // label for swagger
    @GetMapping("/v2/shipmentDelivery/new")
    public ResponseEntity<?> getShipmentDeliveryReportV2(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                         @RequestParam String languageId, @RequestParam String warehouseId,
                                                         @RequestParam(required = false) String fromDeliveryDate,
                                                         @RequestParam(required = false) String toDeliveryDate,
                                                         @RequestParam(required = false) String storeCode,
                                                         @RequestParam(required = false) List<String> soType,
                                                         @RequestParam String orderNumber, @RequestParam String preOutboundNo) throws ParseException, java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<ShipmentDeliveryReport> shipmentDeliveryList = null;
            if(routingDb != null) {
                switch (routingDb) {
                    case "NAMRATHA":
                        shipmentDeliveryList = reportsService.getShipmentDeliveryReportV4(companyCodeId, plantId, languageId, warehouseId,
                                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, preOutboundNo);
                        break;
                    default:
                        shipmentDeliveryList = reportsService.getShipmentDeliveryReportV2(companyCodeId, plantId, languageId, warehouseId,
                                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, preOutboundNo);

                }
            }
            return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /*
     * Shipment Delivery Summary
     */
    @ApiOperation(response = ShipmentDeliverySummaryReport.class, value = "Get ShipmentDeliverySummary Report")
    // label for swagger
    @GetMapping("/shipmentDeliverySummary")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String fromDeliveryDate,
                                                       @RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode,
                                                       @RequestParam(required = true) String warehouseId, @RequestParam String companyCodeId,
                                                       @RequestParam String plantId, @RequestParam String languageId)
            throws ParseException, java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ShipmentDeliverySummaryReport shipmentDeliverySummaryReport =
                    reportsService.getShipmentDeliverySummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId, companyCodeId, plantId, languageId);
            return new ResponseEntity<>(shipmentDeliverySummaryReport, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /*
     * Shipment Dispatch Summary
     */
    @ApiOperation(response = ShipmentDispatchSummaryReport.class, value = "Get ShipmentDispatchSummary Report")
    // label for swagger
    @GetMapping("/shipmentDispatchSummary")
    public ResponseEntity<?> getShipmentDispatchSummaryReport(@RequestParam String fromDeliveryDate,
                                                              @RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode, @RequestParam(required = true) String warehouseId)
            throws Exception {

        ShipmentDispatchSummaryReport shipmentDeliverySummaryReport =
                reportsService.getShipmentDispatchSummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId);
        return new ResponseEntity<>(shipmentDeliverySummaryReport, HttpStatus.OK);
    }


    /*
     * Transaction History Report renamed from open/inventory stock report
     */
    @ApiOperation(response = TransactionHistoryReport.class, value = "Get Transaction History Report")
    // label for swagger
    @PostMapping("/transactionHistoryReport")
    public ResponseEntity<?> getTransactionHistoryReport(@RequestBody FindImBasicData1 searchImBasicData1) throws java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<TransactionHistoryReport> transactionHistoryReportList = reportsService.getTransactionHistoryReport(searchImBasicData1);
            return new ResponseEntity<>(transactionHistoryReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //-------------------------------------------------Get all StockMovementReport---------------------------------

    /**
     * @StockMovementReport
     */
    @ApiOperation(response = StockMovementReport.class, value = "Get all StockMovementReport details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StockMovementReport> stockMovementReportList = reportsService.getStockMovementReports();
        return new ResponseEntity<>(stockMovementReportList, HttpStatus.OK);
    }

    @ApiOperation(response = StockMovementReport1.class, value = "Get all StockMovementReportNew details")
    // label for swagger
    @GetMapping("/new")
    public ResponseEntity<?> getAllStockMovementReport1() throws Exception {
        Stream<StockMovementReport1> stockMovementReportList = reportsService.findStockMovementReportNew();
        return new ResponseEntity<>(stockMovementReportList, HttpStatus.OK);
    }

    // Search DeliveryLine
    @ApiOperation(response = MobileDashboard.class, value = "Find MobileDashBoard") // label for swagger
    @PostMapping("/dashboard/mobile/find")
    public ResponseEntity<?> findMobileDashBoard(@Valid @RequestBody FindMobileDashBoard findMobileDashBoard) throws Exception {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = null;
            if (findMobileDashBoard.getCompanyCode() == null || findMobileDashBoard.getPlantId() == null) {
                Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(findMobileDashBoard.getWarehouseId().get(0), 0L);
                routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            } else {
                routingDb = dbConfigRepository.getDbList(findMobileDashBoard.getCompanyCode(), findMobileDashBoard.getPlantId(), findMobileDashBoard.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }

            MobileDashboard dashboard = reportsService.findMobileDashBoard(findMobileDashBoard);
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//--------------------------------------------Outbound Reversal--------------------------------------------


    @ApiOperation(response = MobileDashboard.class, value = "Outbound Reversal") // label for swagger
    @PostMapping("/outboundreversal")
    public ResponseEntity<?> outboundReversal(@RequestBody OutboundReversalInput outboundReversalInput) {
        WarehouseApiResponse response = new WarehouseApiResponse();
        try {
            String routingDb = dbConfigRepository.getDbName(outboundReversalInput.getCompanyCodeId(),outboundReversalInput.getPlantId(),outboundReversalInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            reportsService.outboundReversal(outboundReversalInput);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            reportsService.obOrderReversal(outboundReversalInput.getRefDocNumber());

            response.setStatusCode("200");
            response.setMessage("Outbound Reversed Successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {
            response.setStatusCode("400");
            response.setMessage("Outbound Not Reversed " + e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }

}