package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.outbound.quality.SearchQualityHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.quality.v2.SearchQualityHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.outbound.transaction.model.outbound.quality.AddQualityHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.outbound.transaction.service.QualityHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"QualityHeader"}, value = "QualityHeader  Operations related to QualityHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "QualityHeader ", description = "Operations related to QualityHeader ")})
@RequestMapping("/qualityheader")
@RestController
public class QualityHeaderController {

    @Autowired
    QualityHeaderService qualityheaderService;
    @Autowired
    DbConfigRepository  dbConfigRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @ApiOperation(response = QualityHeader.class, value = "Get all QualityHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<QualityHeader> qualityheaderList = qualityheaderService.getQualityHeaders();
        return new ResponseEntity<>(qualityheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeader.class, value = "Search QualityHeader") // label for swagger
    @PostMapping("/findQualityHeader")
    public List<QualityHeader> findQualityHeader(@RequestBody SearchQualityHeader searchQualityHeader)
            throws Exception {

        return qualityheaderService.findQualityHeader(searchQualityHeader);
    }


    //Stream
    @ApiOperation(response = QualityHeader.class, value = "Search QualityHeader New") // label for swagger
    @PostMapping("/findQualityHeaderNew")
    public Stream<QualityHeader> findQualityHeaderNew(@RequestBody SearchQualityHeader searchQualityHeader)
            throws Exception {
        return qualityheaderService.findQualityHeaderNew(searchQualityHeader);
    }

    @ApiOperation(response = QualityHeader.class, value = "Create QualityHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postQualityHeader(@Valid @RequestBody AddQualityHeader newQualityHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(newQualityHeader.getCompanyCodeId(), newQualityHeader.getPlantId(), newQualityHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        QualityHeader createdQualityHeader = qualityheaderService.createQualityHeader(newQualityHeader, loginUserID);
        return new ResponseEntity<>(createdQualityHeader, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = QualityHeader.class, value = "Update QualityHeader") // label for swagger
    @PatchMapping("/{qualityInspectionNo}")
    public ResponseEntity<?> patchQualityHeader(@PathVariable String qualityInspectionNo,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                @Valid @RequestBody UpdateQualityHeader updateQualityHeader,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(updateQualityHeader.getCompanyCodeId(), updateQualityHeader.getPlantId(), warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        QualityHeader updatedQualityHeader =
                qualityheaderService.updateQualityHeader(warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, updateQualityHeader);
        return new ResponseEntity<>(updatedQualityHeader, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = QualityHeader.class, value = "Delete QualityHeader") // label for swagger
    @DeleteMapping("/{qualityInspectionNo}")
    public ResponseEntity<?> deleteQualityHeader(@PathVariable String qualityInspectionNo,
                                                 @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                 @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                 @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        qualityheaderService.deleteQualityHeader(warehouseId, preOutboundNo, refDocNumber,
                partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=======================================================V2============================================================

    @ApiOperation(response = QualityHeaderV2.class, value = "Get all QualityHeader details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllQualityHeaderV2() {
        List<QualityHeaderV2> qualityheaderList = qualityheaderService.getQualityHeadersV2();
        return new ResponseEntity<>(qualityheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeaderV2.class, value = "Search QualityHeader") // label for swagger
    @PostMapping("/v2/findQualityHeader")
    public List<QualityHeaderV2> findQualityHeaderV2(@RequestBody SearchQualityHeaderV2 searchQualityHeader)
            throws Exception {
        return qualityheaderService.findQualityHeaderV2(searchQualityHeader);
    }

    //Stream
//    @ApiOperation(response = QualityHeaderV2.class, value = "Search QualityHeader New") // label for swagger
//    @PostMapping("/v2/findQualityHeaderNew")
//    public Stream<QualityHeaderV2> findQualityHeaderNewV2(@RequestBody SearchQualityHeaderV2 searchQualityHeader)
//            throws Exception {
//        return qualityheaderService.findQualityHeaderNewV2(searchQualityHeader);
//    }

    //Find -- Fahaheel Stream Api changed to List for MT
    @ApiOperation(response = QualityHeaderV2.class, value = "Search QualityHeader New") // label for swagger
    @PostMapping("/v2/findQualityHeaderNew")
    public List<QualityHeaderV2> findQualityHeaderNewV2(@RequestBody SearchQualityHeaderV2 searchQualityHeader)
            throws Exception {
        try {
            log.info("SearchQualityHeaderV2 ------> {}", searchQualityHeader);
            String routingDb = null;
            DataBaseContextHolder.setCurrentDb("IMF");
            Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(searchQualityHeader.getWarehouseId().get(0), 0L);
            routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return qualityheaderService.findQualityHeaderNewFahaheelV2(searchQualityHeader);
        }  finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = QualityHeaderV2.class, value = "Create QualityHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postQualityHeaderV2(@Valid @RequestBody QualityHeaderV2 newQualityHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(newQualityHeader.getCompanyCodeId(), newQualityHeader.getPlantId(), newQualityHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        QualityHeaderV2 createdQualityHeader = qualityheaderService.createQualityHeaderV2(newQualityHeader, loginUserID);
        return new ResponseEntity<>(createdQualityHeader, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = QualityHeaderV2.class, value = "Update QualityHeader") // label for swagger
    @PatchMapping("/v2/{qualityInspectionNo}")
    public ResponseEntity<?> patchQualityHeaderV2(@PathVariable String qualityInspectionNo, @RequestParam String companyCodeId,
                                                  @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                  @Valid @RequestBody QualityHeaderV2 updateQualityHeader,
                                                  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        QualityHeaderV2 updatedQualityHeader =
                qualityheaderService.updateQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, updateQualityHeader);
        return new ResponseEntity<>(updatedQualityHeader, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = QualityHeaderV2.class, value = "Delete QualityHeader") // label for swagger
    @DeleteMapping("/v2/{qualityInspectionNo}")
    public ResponseEntity<?> deleteQualityHeaderV2(@PathVariable String qualityInspectionNo,
                                                   @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String companyCodeId,
                                                   @RequestParam String plantId, @RequestParam String languageId,
                                                   @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                   @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                   @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        qualityheaderService.deleteQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}