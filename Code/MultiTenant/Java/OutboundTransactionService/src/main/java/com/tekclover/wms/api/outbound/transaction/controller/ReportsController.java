package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.deliveryline.DeliveryLine;
import com.tekclover.wms.api.outbound.transaction.model.inventory.Inventory;
import com.tekclover.wms.api.outbound.transaction.model.outbound.OutboundReversalInput;
import com.tekclover.wms.api.outbound.transaction.model.outbound.OutboundReversalInputNew;
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
                    case "SPAREX":
                        stockReportList = reportsService.stockReportV10(searchStockReport);
                        break;
                    case "BF":
                        stockReportList = reportsService.stockReportUsingStoredProcedureV9(searchStockReport);
                        break;
                    case "KKF":
                        stockReportList = reportsService.stockReportUsingStoredProcedureV9(searchStockReport);
                        break;
                    case "KSP":
                        stockReportList = reportsService.stockReportUsingStoredProcedureV9(searchStockReport);
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
                    case "BF":
                        shipmentDeliveryList = reportsService.getShipmentDeliveryReportV9(companyCodeId, plantId, languageId, warehouseId,
                                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, preOutboundNo);
                        break;
                    case "KKF":
                        shipmentDeliveryList = reportsService.getShipmentDeliveryReportV9(companyCodeId, plantId, languageId, warehouseId,
                                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, preOutboundNo);
                    case "KSP":
                        shipmentDeliveryList = reportsService.getShipmentDeliveryReportV9(companyCodeId, plantId, languageId, warehouseId,
                                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, preOutboundNo);
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

//    // Search DeliveryLine
//    @ApiOperation(response = MobileDashboard.class, value = "Find MobileDashBoard") // label for swagger
//    @PostMapping("/dashboard/mobile/find")
//    public ResponseEntity<?> findMobileDashBoard(@Valid @RequestBody FindMobileDashBoard findMobileDashBoard) throws Exception {
//
//        try {
//            DataBaseContextHolder.setCurrentDb("MT");
//            String routingDb = null;
//            if (findMobileDashBoard.getCompanyCode() == null || findMobileDashBoard.getPlantId() == null) {
//                Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(findMobileDashBoard.getWarehouseId().get(0), 0L);
//                routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
//                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb(routingDb);
//            } else {
//                routingDb = dbConfigRepository.getDbList(findMobileDashBoard.getCompanyCode(), findMobileDashBoard.getPlantId(), findMobileDashBoard.getWarehouseId());
//                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb(routingDb);
//            }
//
//            MobileDashboard dashboard = reportsService.findMobileDashBoard(findMobileDashBoard);
//            return new ResponseEntity<>(dashboard, HttpStatus.OK);
//        } finally {
//            DataBaseContextHolder.clear();
//        }
//    }

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
            MobileDashboard dashboard;
            if (routingDb.equalsIgnoreCase("BP")){
                dashboard = reportsService.findMobileDashBoardCountV6(findMobileDashBoard);
            }else if (routingDb.equalsIgnoreCase("SPAREX")){
                dashboard = reportsService.findMobileDashBoardCountV10(findMobileDashBoard);
            }
            else {
                dashboard = reportsService.findMobileDashBoard(findMobileDashBoard);
            }
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
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(outboundReversalInput.getCompanyCodeId(),outboundReversalInput.getPlantId(),outboundReversalInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            if (routingDb.equals("BF")) {
                reportsService.outboundReversalV9(outboundReversalInput);
            } else if (routingDb.equals("KKF")) {
                reportsService.outboundReversalV9(outboundReversalInput);
            }
            else {
                reportsService.outboundReversal(outboundReversalInput);
            }
            response.setStatusCode("200");
            response.setMessage("Outbound Reversed Successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {
            response.setStatusCode("400");
            response.setMessage("Outbound Not Reversed " + e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(response = TransactionHistoryReport.class, value = "Find Transaction History Report")
    // label for swagger
    @PostMapping("/transactionHistoryReport/v2")
    public ResponseEntity<?> getTransactionHistoryReportV2(@RequestBody FindImBasicData1 searchImBasicData1) throws java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<HistoryReport> transactionHistoryReportList;
            if(routingDb.equalsIgnoreCase("BP")){
                transactionHistoryReportList = reportsService.getTransactionHistoryReportV6(searchImBasicData1);
            }else if(routingDb.equalsIgnoreCase("SPAREX")){
                transactionHistoryReportList = reportsService.getTransactionHistoryReportV10(searchImBasicData1);
            } else {
                transactionHistoryReportList = reportsService.getTransactionHistoryReportV2(searchImBasicData1);
            }            return new ResponseEntity<>(transactionHistoryReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // BF && KKF
    @ApiOperation(response = MobileDashboard.class, value = "Outbound Reversal By Pallet") // label for swagger
    @PostMapping("/outboundreversal/pallet")
    public ResponseEntity<?> outboundReversalByPalletV9(@RequestBody OutboundReversalInputNew outboundReversalInput) {
        WarehouseApiResponse response = new WarehouseApiResponse();
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(outboundReversalInput.getCompanyCodeId(),outboundReversalInput.getPlantId(),outboundReversalInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            reportsService.outboundReversalV9New(outboundReversalInput);

            response.setStatusCode("200");
            response.setMessage("Outbound Reversed Successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {
            response.setStatusCode("400");
            response.setMessage("Outbound Not Reversed " + e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }

    //=========BF Outbound Cancellation=================
    @ApiOperation(response = MobileDashboard.class, value = "Outbound Cancellation") // label for swagger
    @PostMapping("/outboundcancellation")
    public ResponseEntity<?> outboundReversalV9(@RequestBody OutboundReversalInput outboundReversalInput) {
        WarehouseApiResponse response = new WarehouseApiResponse();
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(outboundReversalInput.getCompanyCodeId(),outboundReversalInput.getPlantId(),outboundReversalInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            reportsService.outboundCancellation(outboundReversalInput);
            response.setStatusCode("200");
            response.setMessage("Outbound Reversed Successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {
            response.setStatusCode("400");
            response.setMessage("Outbound Not Reversed " + e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(response = ContainerReceiptOutboundLine.class, value = "Get ContainerReceiptOutboundLine Report")
    @PostMapping("/containerReceiptOutboundLine/v9")
    public ResponseEntity<?> getContainerReceiptOutboundLine(@RequestBody FindContainerReceiptInboundLine findContainerReceiptInboundLine)
            throws Exception {
        List<ContainerReceiptOutboundLine> containerReceiptOutboundLine = reportsService.getContainerReceiptOutboundLine(findContainerReceiptInboundLine);
        return new ResponseEntity<>(containerReceiptOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = TransactionHistoryReport.class, value = "Closing Stock Report")// label for swagger
    @PostMapping("/closingstock/v2")
    public ResponseEntity<?> closingStockReportV9(@RequestBody FindImBasicData1 searchImBasicData1) throws java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<HistoryReport> transactionHistoryReportList = reportsService.closingStockReportV9(searchImBasicData1);
            return new ResponseEntity<>(transactionHistoryReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ContainerReceiptOutboundLine.class, value = "Get ContainerReceiptOutboundLine Report")
    @PostMapping("/containerReceiptOutboundLine/report/v9")
    public ResponseEntity<?> getContainerReceiptOutboundLineReport(@RequestBody FindContainerReceiptInboundLine findContainerReceiptInboundLine)
            throws Exception {
        List<ContainerReceiptOutboundLine> containerReceiptOutboundLine = reportsService.getContainerReceiptOutboundLineReport(findContainerReceiptInboundLine);
        return new ResponseEntity<>(containerReceiptOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutwardReportResponse.class, value = "Outward Report")// label for swagger
    @PostMapping("/outward/report")
    public ResponseEntity<?> outWardReportV9(@RequestBody OutwardReportInput outwardReportInput) throws java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(outwardReportInput.getCompanyCodeId(),outwardReportInput.getPlantId(),outwardReportInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OutwardReportResponse> responses = reportsService.outwardReportV9(outwardReportInput);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutwardReportResponse.class, value = "Stock Movement Ledger Report")// label for swagger
    @PostMapping("/stockmovement/ledger")
    public ResponseEntity<?> stockMovementLedgerReportV9(@RequestBody LedgerReportInput input) throws java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(input.getCompanyId(),input.getPlantId(),input.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StockMovementLedgerReport> responses = reportsService.stockMovementLedgerReportV9(input);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = TransactionHistoryReport.class, value = "Find Transaction History Report V9")// label for swagger
    @PostMapping("/transactionHistoryReport/v9")
    public ResponseEntity<?> getTransactionHistoryReportV9(@RequestBody FindImBasicData1 searchImBasicData1) throws java.text.ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<HistoryReport> transactionHistoryReportList = reportsService.getTransactionHistoryReportV9(searchImBasicData1);
            return new ResponseEntity<>(transactionHistoryReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}