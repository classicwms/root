package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.report.*;

import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
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
public class ReportsController {

    @Autowired
    ReportsService reportsService;

    @Autowired
    DbConfigRepository dbConfigRepository;



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
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId,plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ReceiptConfimationReport receiptConfimationReport = reportsService.getReceiptConfimationReportV2(asnNumber, preInboundNo, companyCodeId, plantId, languageId, warehouseId);
            return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Counts") // label for swagger
    @GetMapping("/dashboard/get-count")
    public ResponseEntity<?> getDashboardCount(@RequestParam String warehouseId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName("1400", "2400", warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            Dashboard dashboard = reportsService.getDashboardCount(warehouseId);
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //=================================================Notification======================================================
    @ApiOperation(response = StorageBinDashBoardImpl.class, value = "Get Storage Bin Dashboard count - walkaroo")    // label for swagger
    @PostMapping("/storageBinDashboard")
    public ResponseEntity<?> getStorageBinDashBoard(@RequestBody StorageBinDashBoardInput storageBinDashBoardInput) throws Exception {
        try {
            log.info("storageBinDashboard Input -----------> {}", storageBinDashBoardInput);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinDashBoardInput.getCompanyCodeId(), storageBinDashBoardInput.getPlantId(), storageBinDashBoardInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StorageBinDashBoardImpl> storageBinDashBoardList = reportsService.getStorageBinDashBoardCount(storageBinDashBoardInput);
            return new ResponseEntity<>(storageBinDashBoardList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //---------------------------------------------Inbound Reversal------------------------------------------------------------

    @ApiOperation(response = PutAwayLineV2.class, value = "Inbound Reversal")    // label for swagger
    @PatchMapping("/inboundReversal")
    public ResponseEntity<?> inboundReversal(@RequestParam String companyCodeId,@RequestParam String plantId,
                                             @RequestParam String warehouseId,@RequestParam String refDocNumber,@RequestParam String preInboundNo){

        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCodeId,plantId,warehouseId);
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        reportsService.inboundReversal(companyCodeId,plantId,warehouseId,refDocNumber,preInboundNo);

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        reportsService.inboundOrderReversal(refDocNumber);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}