package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.TransactionReport;
import com.tekclover.wms.api.inbound.transaction.model.TransactionReportRes;
import com.tekclover.wms.api.inbound.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.report.*;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.ReportsService;
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
public class    ReportsController {

    @Autowired
    ReportsService reportsService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;


    @ApiOperation(response = MobileDashboard.class, value = "Get Dashboard Report") // label for swagger
    @GetMapping("/dashboard/mobile")
    public ResponseEntity<?> getMobileDashboard(@RequestParam String warehouseId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID) throws Exception {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            MobileDashboard dashboard = reportsService.getMobileDashboard(companyCode, plantId, warehouseId, languageId, loginUserID);
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // Search DeliveryLine
    @ApiOperation(response = MobileDashboard.class, value = "Find MobileDashBoard") // label for swagger
    @PostMapping("/dashboard/mobile/find")
    public ResponseEntity<?> findMobileDashBoard(@Valid @RequestBody FindMobileDashBoard findMobileDashBoard) throws Exception {

        try {
            String currentDB = baseService.getDataBase(findMobileDashBoard.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            MobileDashboard dashboard = reportsService.findMobileDashBoard(findMobileDashBoard);
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
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
        try {
            String currentDB = baseService.getDataBase(plantId.get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<StockReport> stockReportList = reportsService.getAllStockReport(languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, itemText, stockTypeText);
            return new ResponseEntity<>(stockReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Inventory.class, value = "Get All Stock Reports New") // label for swagger
    @PostMapping("/v2/stockReport-all")
    public ResponseEntity<?> getAllStockReportV2(@Valid @RequestBody SearchStockReport searchStockReport) {
        try {
            String currentDB = baseService.getDataBase(searchStockReport.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<StockReportImpl> stockReportList = reportsService.stockReport(searchStockReport);
            return new ResponseEntity<>(stockReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StockReportOutput.class, value = "Get All Stock Reports StoredProcedure") // label for swagger
    @PostMapping("/v2/stockReportSP")
    public ResponseEntity<?> getAllStockReportV2SP(@Valid @RequestBody SearchStockReportInput searchStockReport) {
        try {
            String currentDB = baseService.getDataBase(searchStockReport.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Stream<StockReportOutput> stockReportList = reportsService.stockReportUsingStoredProcedure(searchStockReport);
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
     * Receipt Confirmation
     */
    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report")    // label for swagger
    @GetMapping("/receiptConfirmation")
    public ResponseEntity<?> getReceiptConfimationReport(@RequestParam String asnNumber)
            throws Exception {
        ReceiptConfimationReport receiptConfimationReport = reportsService.getReceiptConfimationReport(asnNumber);
        return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
    }

    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report")    // label for swagger
    @GetMapping("/v2/receiptConfirmation")
    public ResponseEntity<?> getReceiptConfimationReportV2(@RequestParam String asnNumber, @RequestParam String preInboundNo, @RequestParam String companyCodeId,
                                                           @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ReceiptConfimationReport receiptConfimationReport = reportsService.getReceiptConfimationReportV2(asnNumber, preInboundNo, companyCodeId, plantId, languageId, warehouseId);
            return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /*
     * Transaction History Report renamed from open/inventory stock report
     */
    @ApiOperation(response = TransactionHistoryReport.class, value = "Get Transaction History Report")    // label for swagger
    @PostMapping("/transactionHistoryReport")
    public ResponseEntity<?> getTransactionHistoryReport(@RequestBody FindImBasicData1 searchImBasicData1) throws java.text.ParseException {
        try {
            String currentDB = baseService.getDataBase(searchImBasicData1.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Stream<TransactionHistoryReport> transactionHistoryReportList = reportsService.getTransactionHistoryReport(searchImBasicData1);
            return new ResponseEntity<>(transactionHistoryReportList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
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
    //=======================================================Walkaroo-V3==========================================================
    @ApiOperation(response = StorageBinDashBoardImpl.class, value = "Get Storage Bin Dashboard count - walkaroo")    // label for swagger
    @PostMapping("/storageBinDashboard")
    public ResponseEntity<?> getStorageBinDashBoard(@RequestBody StorageBinDashBoardInput storageBinDashBoardInput) throws Exception {
        try {
            String currentDB = baseService.getDataBase(storageBinDashBoardInput.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<StorageBinDashBoardImpl> storageBinDashBoardList = reportsService.getStorageBinDashBoardCount(storageBinDashBoardInput);
            return new ResponseEntity<>(storageBinDashBoardList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
    @ApiOperation(response = BinningProductivityReport.class, value = "Binning Productivity Report")    // label for swagger
    @PostMapping("/binningProductivityReport")
    public ResponseEntity<?> getBinningProductivityReport(@RequestBody SearchBinningProductivityReport searchBinningProductivityReport) throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchBinningProductivityReport.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<BinningProductivityReport> reportResult = reportsService.binningProductivityReport(searchBinningProductivityReport);
            return new ResponseEntity<>(reportResult, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = CharData.class, value = "Picking Report v2")    // label for swagger
    @PostMapping("/putaway/report/v2")
    public ResponseEntity<?> getPutAwayReport(@RequestBody FindReport findReport) throws Exception {
        List<CharData> reportResult = reportsService.findPutAwayReport(findReport);
        return new ResponseEntity<>(reportResult, HttpStatus.OK);
    }

    //=========================================TRANSACTION REPORT=====================================================
    @ApiOperation(response = TransactionReportRes.class, value = "Transaction Report")    // label for swagger
    @PostMapping("/transaction/report")
    public ResponseEntity<?> getNewReport(@RequestBody TransactionReport findReport) throws Exception {
        try {
            String currentDB = baseService.getDataBase(findReport.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<TransactionReportRes> reportResult = reportsService.findTransactionReport(findReport);
            return new ResponseEntity<>(reportResult, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


}