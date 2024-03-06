package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.report.*;
import com.tekclover.wms.api.transaction.service.ReportsService;
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

    @ApiOperation(response = MobileDashboard.class, value = "Get Dashboard Report") // label for swagger
    @GetMapping("/dashboard/mobile")
    public ResponseEntity<?> getMobileDashboard(@RequestParam String warehouseId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID) throws Exception {
        MobileDashboard dashboard = reportsService.getMobileDashboard(companyCode, plantId, warehouseId, languageId, loginUserID);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    // Search DeliveryLine
    @ApiOperation(response = MobileDashboard.class, value = "Find MobileDashBoard") // label for swagger
    @PostMapping("/dashboard/mobile/find")
    public ResponseEntity<?> findMobileDashBoard(@Valid @RequestBody FindMobileDashBoard findMobileDashBoard) throws Exception {

        MobileDashboard dashboard = reportsService.findMobileDashBoard(findMobileDashBoard);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    //@ApiOperation(response = MobileDashboard.class, value = "Get Dashboard Report") // label for swagger
    //   	@GetMapping("/dashboard/mobile")
    //   	public ResponseEntity<?> getMobileDashboard(@RequestParam String warehouseId) throws Exception {
    //       	MobileDashboard dashboard = reportsService.getMobileDashboard(warehouseId);
    //   		return new ResponseEntity<>(dashboard, HttpStatus.OK);
    //   	}

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Counts") // label for swagger
    @GetMapping("/dashboard/get-count")
    public ResponseEntity<?> getDashboardCount(@RequestParam String warehouseId) throws Exception {
        Dashboard dashboard = reportsService.getDashboardCount(warehouseId);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Fast Slow moving Dashboard") // label for swagger
    @PostMapping("/dashboard/get-fast-slow-moving")
    public ResponseEntity<?> getFastSlowMovingDashboard(@RequestBody FastSlowMovingDashboardRequest fastSlowMovingDashboardRequest) throws Exception {
        List<FastSlowMovingDashboard> dashboard = reportsService.getFastSlowMovingDashboard(fastSlowMovingDashboardRequest);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    /*
     * Stock Report
     */
//    @ApiOperation(response = Inventory.class, value = "Get Stock Report") // label for swagger 
//	@GetMapping("/stockReport")
//	public ResponseEntity<?> getStockReport(@RequestParam List<String> warehouseId, 
//			@RequestParam(required = false) List<String> itemCode, 
//			@RequestParam(required = false) String itemText, 
//			@RequestParam String stockTypeText,
//			@RequestParam(defaultValue = "0") Integer pageNo,
//			@RequestParam(defaultValue = "10") Integer pageSize,
//			@RequestParam(defaultValue = "itemCode") String sortBy) {
//    	Page<StockReport> stockReportList = reportsService.getStockReport(warehouseId, itemCode, itemText, stockTypeText,
//    					pageNo, pageSize, sortBy);
//		return new ResponseEntity<>(stockReportList, HttpStatus.OK);
//	}

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

    @ApiOperation(response = StockReportOutput.class, value = "Get All Stock Reports StoredProcedure") // label for swagger
    @PostMapping("/v2/stockReportSP")
    public ResponseEntity<?> getAllStockReportV2SP(@Valid @RequestBody SearchStockReportInput searchStockReport) {
        Stream<StockReportOutput> stockReportList = reportsService.stockReportUsingStoredProcedure(searchStockReport);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
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
     * Stock movement report - ################-----------NOT USED-------------#########################
     */
//    @ApiOperation(response = StockMovementReport.class, value = "Get StockMovement Report") // label for swagger 
//	@GetMapping("/stockMovementReport")
//	public ResponseEntity<?> getStockMovementReport(@RequestParam String warehouseId, 
//			@RequestParam String itemCode, @RequestParam String fromCreatedOn, 
//			@RequestParam String toCreatedOn) throws java.text.ParseException {
//    	List<StockMovementReport> inventoryReportList = 
//    			reportsService.getStockMovementReport(warehouseId, itemCode, fromCreatedOn, toCreatedOn);
//		return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
//	}

    /*
     * Order status report
     */
    @ApiOperation(response = OrderStatusReport.class, value = "Get StockMovement Report") // label for swagger 
    @PostMapping("/orderStatusReport")
    public ResponseEntity<?> getOrderStatusReport(@RequestBody SearchOrderStatusReport request)
            throws ParseException, java.text.ParseException {
        List<OrderStatusReport> orderStatusReportList = reportsService.getOrderStatusReport(request);
        return new ResponseEntity<>(orderStatusReportList, HttpStatus.OK);
    }

    /*
     * Shipment Delivery
     */
    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report") // label for swagger 
    @GetMapping("/shipmentDelivery")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber) throws ParseException, java.text.ParseException {
        List<ShipmentDeliveryReport> shipmentDeliveryList = reportsService.getShipmentDeliveryReport(warehouseId,
                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber);
        return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
    }

    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report v2") // label for swagger
    @GetMapping("/v2/shipmentDelivery")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String companyCodeId,@RequestParam String plantId,
                                                       @RequestParam String languageId,@RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber) throws ParseException, java.text.ParseException {
        List<ShipmentDeliveryReport> shipmentDeliveryList = reportsService.getShipmentDeliveryReportV2(companyCodeId, plantId, languageId, warehouseId,
                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber);
        return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
    }

    /*
     * Shipment Delivery Summary
     */
    @ApiOperation(response = ShipmentDeliverySummaryReport.class, value = "Get ShipmentDeliverySummary Report")    // label for swagger
    @GetMapping("/shipmentDeliverySummary")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String fromDeliveryDate,
                                                       @RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode,
                                                       @RequestParam(required = true) String warehouseId, @RequestParam String companyCodeId,
                                                       @RequestParam String plantId, @RequestParam String languageId)
            throws ParseException, java.text.ParseException {
        ShipmentDeliverySummaryReport shipmentDeliverySummaryReport =
                reportsService.getShipmentDeliverySummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId, companyCodeId, plantId, languageId);
        return new ResponseEntity<>(shipmentDeliverySummaryReport, HttpStatus.OK);
    }

    /*
     * Shipment Dispatch Summary
     */
    @ApiOperation(response = ShipmentDispatchSummaryReport.class, value = "Get ShipmentDispatchSummary Report")    // label for swagger
    @GetMapping("/shipmentDispatchSummary")
    public ResponseEntity<?> getShipmentDispatchSummaryReport(@RequestParam String fromDeliveryDate,
                                                              @RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode, @RequestParam(required = true) String warehouseId)
            throws Exception {
        ShipmentDispatchSummaryReport shipmentDeliverySummaryReport =
                reportsService.getShipmentDispatchSummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId);
        return new ResponseEntity<>(shipmentDeliverySummaryReport, HttpStatus.OK);
    }

    /*
     * Receipt Confirmation
     */
    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report")    // label for swagger
    @GetMapping("/receiptConfirmation")
    public ResponseEntity<?> getReceiptConfimationReport(@RequestParam String asnNumber)
            throws Exception {
        ReceiptConfimationReport receiptConfimationReport = reportsService.getReceiptConfimationReport(asnNumber);
        return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
    }

    /*
     * Transaction History Report renamed from open/inventory stock report
     */
    @ApiOperation(response = TransactionHistoryReport.class, value = "Get Transaction History Report")    // label for swagger
    @PostMapping("/transactionHistoryReport")
    public ResponseEntity<?> getTransactionHistoryReport(@RequestBody FindImBasicData1 searchImBasicData1) throws java.text.ParseException {
        Stream<TransactionHistoryReport> transactionHistoryReportList = reportsService.getTransactionHistoryReport(searchImBasicData1);
        return new ResponseEntity<>(transactionHistoryReportList, HttpStatus.OK);
    }

    //-------------------------------------------------Get all StockMovementReport---------------------------------

    /**
     * @StockMovementReport
     */
    @ApiOperation(response = StockMovementReport.class, value = "Get all StockMovementReport details")    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StockMovementReport> stockMovementReportList = reportsService.getStockMovementReports();
        return new ResponseEntity<>(stockMovementReportList, HttpStatus.OK);
    }

    @ApiOperation(response = StockMovementReport1.class, value = "Get all StockMovementReportNew details")    // label for swagger
    @GetMapping("/new")
    public ResponseEntity<?> getAllStockMovementReport1() throws Exception {
        Stream<StockMovementReport1> stockMovementReportList = reportsService.findStockMovementReportNew();
        return new ResponseEntity<>(stockMovementReportList, HttpStatus.OK);
    }

}