package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.*;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.SearchInboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.InboundLineService;
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
@Api(tags = {"InboundLine"}, value = "InboundLine  Operations related to InboundLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InboundLine ", description = "Operations related to InboundLine ")})
@RequestMapping("/inboundline")
@RestController
public class InboundLineController {

    @Autowired
    InboundLineService inboundlineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = InboundLine.class, value = "Get all InboundLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InboundLine> inboundlineList = inboundlineService.getInboundLines();
        return new ResponseEntity<>(inboundlineList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLine.class, value = "Get a InboundLine") // label for swagger 
    @GetMapping("/{lineNo}")
    public ResponseEntity<?> getInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                            @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode) {
        InboundLine inboundline = inboundlineService.getInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        log.info("InboundLine : " + inboundline);
        return new ResponseEntity<>(inboundline, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Search InboundHeader") // label for swagger
    @PostMapping("/findInboundLine")
    public List<InboundLine> findInboundLine(@RequestBody SearchInboundLine searchInboundLine)
            throws Exception {
        return inboundlineService.findInboundLine(searchInboundLine);
    }

    @ApiOperation(response = InboundLine.class, value = "Create InboundLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInboundLine(@Valid @RequestBody AddInboundLine newInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newInboundLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundLine createdInboundLine = inboundlineService.createInboundLine(newInboundLine, loginUserID);
            return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundLine.class, value = "Create InboundLine") // label for swagger
    @PostMapping("/confirm")
    public ResponseEntity<?> postInboundLine(@Valid @RequestBody List<AddInboundLine> newInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newInboundLine.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundLine createdInboundLine = inboundlineService.confirmInboundLine(newInboundLine, loginUserID);
            return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundLine.class, value = "Update InboundLine") // label for swagger
    @PatchMapping("/{lineNo}")
    public ResponseEntity<?> patchInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                              @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode,
                                              @Valid @RequestBody UpdateInboundLine updateInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updateInboundLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundLine createdInboundLine =
                    inboundlineService.updateInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine);
            return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundLine.class, value = "Delete InboundLine") // label for swagger
    @DeleteMapping("/{lineNo}")
    public ResponseEntity<?> deleteInboundLine(@PathVariable Long lineNo, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID) {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            inboundlineService.deleteInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//==================================================================V2=======================================================

    @ApiOperation(response = InboundLineV2.class, value = "Get all InboundLine details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllInboundLine() {
        List<InboundLineV2> inboundlineList = inboundlineService.getInboundLinesV2();
        return new ResponseEntity<>(inboundlineList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Get a InboundLine") // label for swagger
    @GetMapping("/v2/{lineNo}")
    public ResponseEntity<?> getInboundLineV2(@PathVariable Long lineNo, @RequestParam String companyCode, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId,
                                              @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundLineV2 inboundline = inboundlineService.getInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
            log.info("InboundLine : " + inboundline);
            return new ResponseEntity<>(inboundline, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Search InboundHeader") // label for swagger
    @PostMapping("/v2/findInboundLine")
    public Stream<InboundLineV2> findInboundLineV2(@RequestBody SearchInboundLineV2 searchInboundLine)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchInboundLine.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return inboundlineService.findInboundLineV2(searchInboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundLineV2.class, value = "Create InboundLine") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postInboundLineV2(@Valid @RequestBody InboundLineV2 newInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newInboundLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundLineV2 createdInboundLine = inboundlineService.createInboundLineV2(newInboundLine, loginUserID);
            return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundLine.class, value = "Create InboundLine") // label for swagger
    @PostMapping("/v2/confirm")
    public ResponseEntity<?> postInboundLineV2(@Valid @RequestBody List<InboundLineV2> newInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newInboundLine.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundLineV2 createdInboundLine = inboundlineService.confirmInboundLineV2(newInboundLine, loginUserID);
            return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundLineV2.class, value = "Update InboundLine") // label for swagger
    @PatchMapping("/v2/{lineNo}")
    public ResponseEntity<?> patchInboundLineV2(@PathVariable Long lineNo, @RequestParam String companyCode, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId,
                                                @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode,
                                                @Valid @RequestBody InboundLineV2 updateInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(updateInboundLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundLineV2 createdInboundLine =
                    inboundlineService.updateInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine);
            return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Batch Update InboundLine
    @ApiOperation(response = InboundLineV2.class, value = "Batch Update InboundLines V2") // label for swagger
    @PatchMapping("/v2/batchUpdateInboundLines")
    public ResponseEntity<?> batchInboundLineV2Update(@Valid @RequestBody List<InboundLineV2> updateInboundLine, @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(updateInboundLine.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<InboundLineV2> updatedInboundLines = inboundlineService.updateBatchInboundLineV2(updateInboundLine, loginUserID);
            return new ResponseEntity<>(updatedInboundLines, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundLineV2.class, value = "Delete InboundLine") // label for swagger
    @DeleteMapping("/v2/{lineNo}")
    public ResponseEntity<?> deleteInboundLineV2(@PathVariable Long lineNo, @RequestParam String languageId, @RequestParam String companyCode,
                                                 @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                 @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            inboundlineService.deleteInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}