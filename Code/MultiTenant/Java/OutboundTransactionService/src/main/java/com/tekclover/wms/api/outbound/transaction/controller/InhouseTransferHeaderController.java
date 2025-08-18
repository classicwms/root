package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.mnc.*;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.AsyncService;
import com.tekclover.wms.api.outbound.transaction.service.InhouseTransferHeaderService;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
@Api(tags = {"InHouseTransferHeader"}, value = "InHouseTransferHeader  Operations related to InHouseTransferHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InHouseTransferHeader ", description = "Operations related to InHouseTransferHeader ")})
@RequestMapping("/inhousetransferheader")
@RestController
public class InhouseTransferHeaderController {

    @Autowired
    InhouseTransferHeaderService inHouseTransferHeaderService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    AsyncService asyncService;

    @ApiOperation(response = InhouseTransferHeader.class, value = "Get all InHouseTransferHeader details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InhouseTransferHeader> inhousetransferheaderList =
                inHouseTransferHeaderService.getInHouseTransferHeaders();
        return new ResponseEntity<>(inhousetransferheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Get a InHouseTransferHeader") // label for swagger 
    @GetMapping("/{transferNumber}")
    public ResponseEntity<?> getInHouseTransferHeader(@PathVariable String transferNumber, @RequestParam String warehouseId, @RequestParam Long transferTypeId) {

        InhouseTransferHeader inhousetransferheader = inHouseTransferHeaderService.getInHouseTransferHeader(warehouseId, transferNumber, transferTypeId);
        return new ResponseEntity<>(inhousetransferheader, HttpStatus.OK);
    }


    @ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader") // label for swagger
    @PostMapping("/findInHouseTransferHeader")
    public ResponseEntity<?> findInHouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader)
            throws Exception {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            List<InhouseTransferHeader> inhouseTransferHeader = null;
            String routingDb = dbConfigRepository.getDbList(searchInHouseTransferHeader.getCompanyCodeId(), searchInHouseTransferHeader.getPlantId(), searchInHouseTransferHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            inhouseTransferHeader = inHouseTransferHeaderService.findInHouseTransferHeader(searchInHouseTransferHeader);
            return new ResponseEntity<>(inhouseTransferHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Stream
    @ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader New")
    // label for swagger
    @PostMapping("/findInHouseTransferHeaderNew")
    public ResponseEntity<?> findInHouseTransferHeaderNew(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader)
            throws Exception {

        Stream<InhouseTransferHeader> results = inHouseTransferHeaderService.findInHouseTransferHeaderNew(searchInHouseTransferHeader);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInHouseTransferHeader(@Valid @RequestBody AddInhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newInHouseTransferHeader.getCompanyCodeId(), newInHouseTransferHeader.getPlantId(), newInHouseTransferHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            InhouseTransferHeaderEntity createdInHouseTransferHeader =
                    inHouseTransferHeaderService.createInHouseTransferHeader(newInHouseTransferHeader, loginUserID);
            return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //===================================================================V2==================================================================================

    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postInHouseTransferHeaderV2(@RequestBody AddInhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            InhouseTransferHeaderEntity transferHeaderEntity = null;
            String routingDb = dbConfigRepository.getDbName(newInHouseTransferHeader.getCompanyCodeId(), newInHouseTransferHeader.getPlantId(), newInHouseTransferHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            if (routingDb != null){
                switch (routingDb){
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        transferHeaderEntity = inHouseTransferHeaderService.createInHouseTransferHeaderFahaheel(newInHouseTransferHeader, loginUserID);
                        break;
                    case "REEFERON":
                        transferHeaderEntity = inHouseTransferHeaderService.createInHouseTransferHeaderV5(newInHouseTransferHeader, loginUserID);
                        break;
                    default:
                        transferHeaderEntity= inHouseTransferHeaderService.createInHouseTransferHeaderV2(newInHouseTransferHeader, loginUserID);
                }
            }
            return new ResponseEntity<>(transferHeaderEntity, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        try {
//            InhouseTransferHeaderEntity createdInHouseTransferHeader = new InhouseTransferHeaderEntity();
//
//            BeanUtils.copyProperties(newInHouseTransferHeader, createdInHouseTransferHeader, CommonUtils.getNullPropertyNames(newInHouseTransferHeader));
//
//            // Return early response
//            ResponseEntity<?> response = new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.ACCEPTED);
//
//            asyncService.processInhouseTransferHeaderAsync(newInHouseTransferHeader, loginUserID);
//
//            return response;
//        } catch (Exception e) {
//            log.error("Error processing InhouseTransferHeader async", e);
//            return new ResponseEntity<>("Failed to start InhouseTransferHeader process", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader Upload")
    // label for swagger
    @PostMapping("/v2/upload")
    public ResponseEntity<?> postInHouseTransferHeaderUploadV2(@Valid @RequestBody List<InhouseTransferUpload> inhouseTransferUploadList, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        inHouseTransferHeaderService.createInHouseTransferHeaderUploadV2(inhouseTransferUploadList, loginUserID);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}