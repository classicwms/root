package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.dto.StagingLineUpdate;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.*;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.SearchStagingLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.inbound.transaction.service.StagingLineService;
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
@Api(tags = {"StagingLine"}, value = "StagingLine  Operations related to StagingLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StagingLine ", description = "Operations related to StagingLine ")})
@RequestMapping("/stagingline")
@RestController
public class StagingLineController {

    @Autowired
    StagingLineService staginglineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @ApiOperation(response = StagingLine.class, value = "Get all StagingLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StagingLineEntity> staginglineList = staginglineService.getStagingLines();
        return new ResponseEntity<>(staginglineList, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Get a StagingLine") // label for swagger 
    @GetMapping("/{lineNo}")
    public ResponseEntity<?> getStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                            @RequestParam String refDocNumber, @RequestParam String stagingNo, @RequestParam String palletCode,
                                            @RequestParam String caseCode, @RequestParam String preInboundNo, @RequestParam String itemCode) {
        StagingLineEntity stagingline =
                staginglineService.getStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, lineNo,
                        itemCode);
        log.info("StagingLine : " + stagingline);
        return new ResponseEntity<>(stagingline, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Get a StagingLine") // label for swagger 
    @GetMapping("/{lineNo}/inboundline")
    public ResponseEntity<?> getStagingLineForInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                                          @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode) {
        List<StagingLineEntity> stagingline =
                staginglineService.getStagingLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        log.info("StagingLine : " + stagingline);
        return new ResponseEntity<>(stagingline, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Search StagingLine") // label for swagger
    @PostMapping("/findStagingLine")
    public List<StagingLineEntityV2> findStagingLine(@RequestBody SearchStagingLineV2 searchStagingLine) throws Exception {
        log.info("searchStagingline ------> {}", searchStagingLine);
        String routingDb = null;
        DataBaseContextHolder.setCurrentDb("MT");
        Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(searchStagingLine.getWarehouseId().get(0), 0L);
        routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        return staginglineService.findStagingLine(searchStagingLine);
    }

    @ApiOperation(response = StagingLineUpdate.class,value = "Update ExpiryMfr")
    @PostMapping("/update/ExpiryMfr")
    public ResponseEntity<?>updateExpiryMfr(@RequestBody List<StagingLineUpdate> input) {
       try{
//           for (StagingLineUpdate stagingLine : input){
               String routingDb = dbConfigRepository.getDbName(input.get(0).getCompanyCode(), input.get(0).getPlantId(), input.get(0).getWarehouseId());
               log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
               DataBaseContextHolder.clear();
               DataBaseContextHolder.setCurrentDb(routingDb);
//           }
           List<StagingLineUpdate> updatedStagingLine =
                   staginglineService.updateExpiryMfr(input);
           return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
       }finally {
           DataBaseContextHolder.clear();
       }
    }


    @ApiOperation(response = StagingLine.class, value = "Create StagingLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStagingLine(@Valid @RequestBody List<AddStagingLine> newStagingLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            for(AddStagingLine stagingLine : newStagingLine) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(stagingLine.getCompanyCode(), stagingLine.getPlantId(), stagingLine.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<StagingLine> createdStagingLine = staginglineService.createStagingLine(newStagingLine, loginUserID);
            return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLine.class, value = "Update StagingLine") // label for swagger
    @PatchMapping("/{lineNo}")
    public ResponseEntity<?> patchStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                              @RequestParam String refDocNumber, @RequestParam String stagingNo, @RequestParam String palletCode,
                                              @RequestParam String caseCode, @RequestParam String preInboundNo, @RequestParam String itemCode,
                                              @Valid @RequestBody UpdateStagingLine updateStagingLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(updateStagingLine.getCompanyCode(), updateStagingLine.getPlantId(), updateStagingLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StagingLineEntity createdStagingLine =
                    staginglineService.updateStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
                            caseCode, lineNo, itemCode, loginUserID, updateStagingLine);
            return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLine.class, value = "AssignHHTUser StagingLine") // label for swagger
    @PatchMapping("/assignHHTUser")
    public ResponseEntity<?> assignHHTUser(@RequestBody List<AssignHHTUser> assignHHTUsers, @RequestParam String assignedUserId,
                                           @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        List<StagingLineEntity> updatedStagingLine =
                staginglineService.assignHHTUser(assignHHTUsers, assignedUserId, loginUserID);
        return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Update StagingLine") // label for swagger
    @PatchMapping("/caseConfirmation")
    public ResponseEntity<?> patchStagingLineForCaseConfirmation(@RequestBody List<CaseConfirmation> caseConfirmations,
                                                                 @RequestParam String caseCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<StagingLineEntity> createdStagingLine =
                staginglineService.caseConfirmation(caseConfirmations, caseCode, loginUserID);
        return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Delete StagingLine") // label for swagger
    @DeleteMapping("/{lineNo}")
    public ResponseEntity<?> deleteStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                               @RequestParam String refDocNumber, @RequestParam String stagingNo, @RequestParam String palletCode,
                                               @RequestParam String caseCode, @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID) {
        staginglineService.deleteStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
                caseCode, lineNo, itemCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = StagingLine.class, value = "Delete StagingLine") // label for swagger
    @DeleteMapping("/{lineNo}/cases")
    public ResponseEntity<?> deleteCases(@PathVariable Long lineNo, @RequestParam String preInboundNo, @RequestParam String caseCode,
                                         @RequestParam String itemCode, @RequestParam String loginUserID) {
        staginglineService.deleteCases(preInboundNo, lineNo, itemCode, caseCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//====================================================V2======================================================//

//	@ApiOperation(response = StagingLineV2.class, value = "Create StagingLine V2") // label for swagger
//	@PostMapping("/v2")
//	public ResponseEntity<?> postStagingLineV2(@Valid @RequestBody List<AddPreInboundLineV2> newStagingLine,
//											   @RequestParam String warehouseId, @RequestParam String companyCodeId,
//											   @RequestParam String plantId, @RequestParam String languageId,
//											   @RequestParam String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		List<StagingLineEntityV2> createdStagingLine =
//				stagingLineV2Service.createStagingLine(newStagingLine, warehouseId, companyCodeId, plantId, languageId, loginUserID);
//		return new ResponseEntity<>(createdStagingLine , HttpStatus.OK);
//	}

    @ApiOperation(response = StagingLineEntityV2.class, value = "Get a StagingLine V2") // label for swagger
    @GetMapping("/{lineNo}/v2")
    public ResponseEntity<?> getStagingLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
                                              @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                              @RequestParam String refDocNumber, @RequestParam String stagingNo, @RequestParam String palletCode,
                                              @RequestParam String caseCode, @RequestParam String preInboundNo, @RequestParam String itemCode) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StagingLineEntityV2 stagingline =
                    staginglineService.getStagingLineV2(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber, stagingNo, palletCode,
                            caseCode, lineNo, itemCode);
            log.info("StagingLine : " + stagingline);
            return new ResponseEntity<>(stagingline, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLineEntityV2.class, value = "Get a StagingLine V2") // label for swagger
    @GetMapping("/{lineNo}/inboundline/v2")
    public ResponseEntity<?> getStagingLineForInboundLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
                                                            @RequestParam String plantId, @RequestParam String languageId,
                                                            @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                            @RequestParam String preInboundNo, @RequestParam String itemCode) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StagingLineEntityV2> stagingline =
                    staginglineService.getStagingLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
            log.info("StagingLine : " + stagingline);
            return new ResponseEntity<>(stagingline, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLineEntityV2.class, value = "Search StagingLine V2") // label for swagger
    @PostMapping("/findStagingLine/v2")
    public Stream<StagingLineEntityV2> findStagingLineV2(@RequestBody SearchStagingLineV2 searchStagingLine) throws Exception {
        return staginglineService.findStagingLineV2(searchStagingLine);
    }


    @ApiOperation(response = StagingLineEntityV2.class, value = "Update StagingLine V2") // label for swagger
    @PatchMapping("/v2/{lineNo}")
    public ResponseEntity<?> patchStagingLineV2(@PathVariable Long lineNo, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId, @RequestParam String refDocNumber,
                                                @RequestParam String stagingNo, @RequestParam String palletCode, @RequestParam String caseCode,
                                                @RequestParam String preInboundNo, @RequestParam String itemCode,
                                                @Valid @RequestBody StagingLineEntityV2 updateStagingLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            log.info("StagingLine update inputs ----->  " + updateStagingLine);
            log.info("StagingLine update params inputs: companyCode -----> " + companyCode + ", plantId ----> " + plantId + ", warehouseId -----> " + warehouseId +
                    ", refDocNumber ----> " + refDocNumber + ", stagingNo ----> " + stagingNo + ", palletCode -----> " + palletCode + ", caseCode -----> " + caseCode +
                    ", preInboundNo ------> " + preInboundNo + ", itemCode -----> " + itemCode + ", lineNo -----> " + lineNo + ", languageId ------> " + languageId);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(updateStagingLine.getCompanyCode(), updateStagingLine.getPlantId(), updateStagingLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StagingLineEntityV2 createdStagingLine =
                    staginglineService.updateStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
                            caseCode, lineNo, itemCode, loginUserID, updateStagingLine);
            return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLineEntityV2.class, value = "Update StagingLine BarcodeId V2") // label for swagger
    @PatchMapping("/v2/barcodeId")
    public ResponseEntity<?> patchStagingLineBarcodeIdV2(@RequestParam String warehouseId, @RequestParam String companyCode, @RequestParam String plantId,
                                                         @RequestParam String languageId, @RequestParam String manufacturerName, @RequestParam String barcodeId,
                                                         @RequestParam String itemCode, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StagingLineEntityV2> updateBarCodeId =
                    staginglineService.updateStagingLineForBarcodeV2(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, loginUserID);
            return new ResponseEntity<>(updateBarCodeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLineEntityV2.class, value = "AssignHHTUser StagingLine") // label for swagger
    @PatchMapping("/assignHHTUser/v2")
    public ResponseEntity<?> assignHHTUserV2(@RequestBody List<AssignHHTUser> assignHHTUsers, @RequestParam String companyCode,
                                             @RequestParam String plantId, @RequestParam String languageId,
                                             @RequestParam String assignedUserId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        log.info("Assign HHtUSer Requested: " + assignHHTUsers);
        List<StagingLineEntityV2> updatedStagingLine =
                staginglineService.assignHHTUserV2(assignHHTUsers, companyCode, plantId, languageId, assignedUserId, loginUserID);
        return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLineEntityV2.class, value = "Update StagingLine") // label for swagger
    @PatchMapping("/caseConfirmation/v2")
    public ResponseEntity<?> patchStagingLineForCaseConfirmationV2(@RequestBody List<CaseConfirmation> caseConfirmations, @RequestParam String companyCode,
                                                                   @RequestParam String plantId, @RequestParam String languageId,
                                                                   @RequestParam String caseCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for(CaseConfirmation caseConf : caseConfirmations) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(companyCode, plantId, caseConf.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<StagingLineEntityV2> createdStagingLine =
                    staginglineService.caseConfirmationV2(caseConfirmations, caseCode, companyCode, plantId, languageId, loginUserID);
            return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLineEntityV2.class, value = "Delete StagingLine V2") // label for swagger
    @DeleteMapping("/v2/{lineNo}")
    public ResponseEntity<?> deleteStagingLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
                                                 @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                 @RequestParam String stagingNo, @RequestParam String palletCode,
                                                 @RequestParam String caseCode, @RequestParam String preInboundNo,
                                                 @RequestParam String itemCode, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            staginglineService.deleteStagingLineV2(companyCode, plantId, languageId, warehouseId,
                    preInboundNo, refDocNumber, stagingNo, palletCode,
                    caseCode, lineNo, itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StagingLineEntityV2.class, value = "Delete StagingLine") // label for swagger
    @DeleteMapping("/v2/{lineNo}/cases")
    public ResponseEntity<?> deleteCasesV2(@PathVariable Long lineNo, @RequestParam String preInboundNo,
                                           @RequestParam String caseCode, @RequestParam String companyCode,
                                           @RequestParam String plantId, @RequestParam String languageId,
                                           @RequestParam String itemCode, @RequestParam String loginUserID) {
        staginglineService.deleteCasesV2(companyCode, plantId, languageId, preInboundNo, lineNo, itemCode, caseCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

}