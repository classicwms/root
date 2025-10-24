package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.outbound.*;
import com.tekclover.wms.api.outbound.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.outbound.transaction.model.outbound.outboundreversal.v2.OutboundReversalV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.InboundReversalInput;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundLineOutput;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.SearchOutboundLineV2;
import com.tekclover.wms.api.outbound.transaction.model.report.StockMovementReport;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.OutboundLineService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"OutboundLine"}, value = "OutboundLine  Operations related to OutboundLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundLine ", description = "Operations related to OutboundLine ")})
@RequestMapping("/outboundline")
@RestController
public class OutboundLineController {

    @Autowired
    OutboundLineService outboundlineService;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = OutboundLine.class, value = "Get all OutboundLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<OutboundLine> outboundlineList = outboundlineService.getOutboundLines();
        return new ResponseEntity<>(outboundlineList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Get a OutboundLine") // label for swagger 
    @GetMapping("/delivery/line")
    public ResponseEntity<?> getOutboundLine(@RequestParam String warehouseId,
                                             @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode) {

        List<OutboundLine> outboundline =
                outboundlineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        log.info("OutboundLine : " + outboundline);
        return new ResponseEntity<>(outboundline, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/findOutboundLine")
    public List<OutboundLine> findOutboundLine(@RequestBody SearchOutboundLine searchOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return outboundlineService.findOutboundLine(searchOutboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/findOutboundLine-new")
    public List<OutboundLine> findOutboundLineNew(@RequestBody SearchOutboundLine searchOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return outboundlineService.findOutboundLineNew(searchOutboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine for Stock movement report")
    // label for swagger
    @PostMapping("/stock-movement-report/findOutboundLine")
    public List<StockMovementReport> findLinesForStockMovement(@RequestBody SearchOutboundLine searchOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return outboundlineService.findLinesForStockMovement(searchOutboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/findOutboundLineReport")
    public List<OutboundLine> findOutboundLineForReport(@RequestBody SearchOutboundLineReport searchOutboundLineReport)
            throws Exception {

        return outboundlineService.findOutboundLineReport(searchOutboundLineReport);
    }

    @ApiOperation(response = OutboundLine.class, value = "Create OutboundLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postOutboundLine(@Valid @RequestBody AddOutboundLine newOutboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        OutboundLine createdOutboundLine = outboundlineService.createOutboundLine(newOutboundLine, loginUserID);
        return new ResponseEntity<>(createdOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @GetMapping("/delivery/confirmation")
    public ResponseEntity<?> deliveryConfirmation(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        List<OutboundLine> createdOutboundLine =
                outboundlineService.deliveryConfirmation(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
        return new ResponseEntity<>(createdOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @PatchMapping("/{lineNumber}")
    public ResponseEntity<?> patchOutboundLine(@PathVariable Long lineNumber,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                               @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
                                               @Valid @RequestBody UpdateOutboundLine updateOutboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        OutboundLine updatedOutboundLine =
                outboundlineService.updateOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        lineNumber, itemCode, loginUserID, updateOutboundLine);
        return new ResponseEntity<>(updatedOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Delete OutboundLine") // label for swagger
    @DeleteMapping("/{lineNumber}")
    public ResponseEntity<?> deleteOutboundLine(@PathVariable Long lineNumber,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
                                                @RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID) {
        outboundlineService.deleteOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLine.class, value = "Outbound Reversal") // label for swagger
    @GetMapping("/reversal/new")
    public ResponseEntity<?> doReversal(@RequestParam String refDocNumber, @RequestParam String itemCode,
                                        @RequestParam String loginUserID, @RequestParam String companyCodeId,
                                        @RequestParam String plantId, @RequestParam String warehouseId) throws IllegalAccessException, InvocationTargetException {
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        List<OutboundReversal> deliveryLines = outboundlineService.doReversal(refDocNumber, itemCode, loginUserID);
        log.info("deliveryLines : " + deliveryLines);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    //=======================================================V2============================================================
    @ApiOperation(response = OutboundLineV2.class, value = "Get all OutboundLine details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllOutboundLineV2() {
        List<OutboundLineV2> outboundlineList = outboundlineService.getOutboundLinesV2();
        return new ResponseEntity<>(outboundlineList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Get a OutboundLine") // label for swagger
    @GetMapping("/v2/delivery/line")
    public ResponseEntity<?> getOutboundLineV2(@RequestParam String warehouseId, @RequestParam String companyCodeId,
                                               @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OutboundLineV2> outboundline =
                    outboundlineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
            log.info("OutboundLine : " + outboundline);
            return new ResponseEntity<>(outboundline, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/v2/findOutboundLine")
    public List<OutboundLineV2> findOutboundLineV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return outboundlineService.findOutboundLineV2(searchOutboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/v2/findOutboundLineStream")
    public Stream<OutboundLineV2> findOutboundLineNewStreamV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return outboundlineService.findOutboundLineNewStreamV2(searchOutboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//	@ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLines v2") // label for swagger
//	@PatchMapping("/v2/lineNumbers")
//	public ResponseEntity<?> patchOutboundLines(@Valid @RequestBody List<OutboundLineV2> updateOutboundLine, @RequestParam String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		List<OutboundLineV2> outboundLines = outboundlineService.updateOutboundLinesV2(loginUserID, updateOutboundLine);
//		return new ResponseEntity<>(outboundLines, HttpStatus.OK);
//	}

    @ApiOperation(response = OutboundLineOutput.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/v2/findOutboundLineNew")
    public List<OutboundLineOutput> findOutboundLineNewV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OutboundLineOutput> outboundLine = null;
            if (routingDb != null) {
                switch (routingDb) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        outboundLine = outboundlineService.findOutboundLineNewV2(searchOutboundLine);
                        break;
//                    case "REEFERON":
//                        outboundLine = outboundlineService.findOutboundLineNewV5(searchOutboundLine);
//                        break;
//                    case "NAMRATHA":
//                        outboundLine = outboundlineService.findOutboundLineNewV6(searchOutboundLine);
//                        break;
//                    case "KNOWELL":
//                        outboundLine = outboundlineService.findOutboundLineNewV7(searchOutboundLine);
//                        break;
                    default:
//                        outboundLine = outboundlineService.findOutboundLineNewV2(searchOutboundLine);
                        break;
                }
            }
            return outboundLine;
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = OutboundLineV2.class, value = "Create OutboundLine") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postOutboundLineV2(@Valid @RequestBody OutboundLineV2 newOutboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newOutboundLine.getCompanyCodeId(), newOutboundLine.getPlantId(), newOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OutboundLineV2 createdOutboundLine = outboundlineService.createOutboundLineV2(newOutboundLine, loginUserID);
            return new ResponseEntity<>(createdOutboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLine") // label for swagger
    @GetMapping("/v2/delivery/confirmation")
    public ResponseEntity<?> deliveryConfirmationV2(@RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                    @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
                                                    @RequestParam String partnerCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OutboundLineV2> createdOutboundLine = null;
            if (routingDb != null) {
                switch (routingDb) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        createdOutboundLine = outboundlineService.deliveryConfirmationV2(companyCodeId, plantId, languageId,
                                warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
                        break;
//                    case "REEFERON":
//                        createdOutboundLine = outboundlineService.deliveryConfirmationV5(companyCodeId, plantId, languageId,
//                                warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
//                        break;
                    default:
                        createdOutboundLine = outboundlineService.deliveryConfirmationV2(companyCodeId, plantId, languageId,
                                warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
                        break;
                }
            }
            return new ResponseEntity<>(createdOutboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLine") // label for swagger
    @PatchMapping("/v2/{lineNumber}")
    public ResponseEntity<?> patchOutboundLineV2(@PathVariable Long lineNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                 @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
                                                 @Valid @RequestBody OutboundLineV2 updateOutboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OutboundLineV2 updatedOutboundLine =
                    outboundlineService.updateOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            lineNumber, itemCode, loginUserID, updateOutboundLine);
            return new ResponseEntity<>(updatedOutboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Delete OutboundLine") // label for swagger
    @DeleteMapping("/v2/{lineNumber}")
    public ResponseEntity<?> deleteOutboundLine(@PathVariable Long lineNumber, @RequestParam String companyCodeId,
                                                @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
                                                @RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            outboundlineService.deleteOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                    itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine for Stock movement report")
    // label for swagger
    @PostMapping("/stock-movement-report/v2/findOutboundLine")
    public List<StockMovementReport> findLinesForStockMovementV2(@RequestBody SearchOutboundLineV2 searchOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return outboundlineService.findLinesForStockMovementV2(searchOutboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLineV2.class, value = "Get Delivery Lines") // label for swagger
    @GetMapping("/v2/reversal/new")
    public ResponseEntity<?> doReversalV2(@RequestParam String refDocNumber, @RequestParam String itemCode, @RequestParam String manufacturerName,
                                          @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        List<OutboundReversalV2> deliveryLines = outboundlineService.doReversalV2(refDocNumber, itemCode, manufacturerName, loginUserID);
        log.info("deliveryLines : " + deliveryLines);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Reversal Batch") // label for swagger
    @PostMapping("/v2/reversal/batch")
    public ResponseEntity<?> doReversalBatchV2(@RequestBody List<InboundReversalInput> outboundReversalInput,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for (InboundReversalInput inboundReversalInput1 : outboundReversalInput) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(inboundReversalInput1.getCompanyCodeId(), inboundReversalInput1.getPlantId(), inboundReversalInput1.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<OutboundReversalV2> deliveryLines = outboundlineService.batchOutboundReversal(outboundReversalInput, loginUserID);
            log.info("deliveryLines : " + deliveryLines);
            return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}