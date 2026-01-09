package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.AddGrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.SearchGrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.UpdateGrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.SearchGrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.GrHeaderService;
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
@Api(tags = {"GrHeader"}, value = "GrHeader  Operations related to GrHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "GrHeader ", description = "Operations related to GrHeader ")})
@RequestMapping("/grheader")
@RestController
public class GrHeaderController {

    @Autowired
    GrHeaderService grheaderService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = GrHeader.class, value = "Get all GrHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<GrHeader> grheaderList = grheaderService.getGrHeaders();
        return new ResponseEntity<>(grheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = GrHeader.class, value = "Get a GrHeader") // label for swagger 
    @GetMapping("/{goodsReceiptNo}")
    public ResponseEntity<?> getGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId,
                                         @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                         @RequestParam String palletCode, @RequestParam String caseCode) {

        GrHeader grheader = grheaderService.getGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                palletCode, caseCode);
        log.info("GrHeader : " + grheader);
        return new ResponseEntity<>(grheader, HttpStatus.OK);
    }

    @ApiOperation(response = GrHeader.class, value = "Search GrHeader") // label for swagger
    @PostMapping("/findGrHeader")
    public List<GrHeader> findGrHeader(@RequestBody SearchGrHeader searchGrHeader)
            throws Exception {
        return grheaderService.findGrHeader(searchGrHeader);
    }

    //Stream
    @ApiOperation(response = GrHeader.class, value = "Search GrHeader New") // label for swagger
    @PostMapping("/findGrHeaderNew")
    public Stream<GrHeader> findGrHeaderNew(@RequestBody SearchGrHeader searchGrHeader)
            throws Exception {
        return grheaderService.findGrHeaderNew(searchGrHeader);
    }

    @ApiOperation(response = GrHeader.class, value = "Create GrHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postGrHeader(@Valid @RequestBody AddGrHeader newGrHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newGrHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            GrHeader createdGrHeader = grheaderService.createGrHeader(newGrHeader, loginUserID);
            return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = GrHeader.class, value = "Update GrHeader") // label for swagger
    @PatchMapping("/{goodsReceiptNo}")
    public ResponseEntity<?> patchGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId,
                                           @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                           @RequestParam String palletCode, @RequestParam String caseCode,
                                           @Valid @RequestBody UpdateGrHeader updateGrHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updateGrHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            GrHeader createdGrHeader =
                    grheaderService.updateGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID, updateGrHeader);
            return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
//    @PatchMapping("/{refDocNumber}/reverse")
//	public ResponseEntity<?> patchPutAwayHeader(@PathVariable String refDocNumber, @RequestParam String packBarcodes, 
//			@RequestParam String warehouseId, @RequestParam String preInboundNo, @RequestParam String caseCode, 
//			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
//    	grheaderService.updatePutAwayHeader(refDocNumber, packBarcodes, warehouseId, preInboundNo, caseCode, loginUserID);
//		return new ResponseEntity<>(HttpStatus.OK);
//	}

    @ApiOperation(response = GrHeader.class, value = "Delete GrHeader") // label for swagger
    @DeleteMapping("/{goodsReceiptNo}")
    public ResponseEntity<?> deleteGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId,
                                            @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                            @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID) {

        grheaderService.deleteGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
//--------------------------------------------------------------V2-----------------------------------------------------------------------------//

    /**
     * @param searchGrHeader
     * @return
     * @throws Exception
     */
    @ApiOperation(response = GrHeaderV2.class, value = "Search GrHeader V2") // label for swagger
    @PostMapping("/findGrHeader/v2")
    public Stream<GrHeaderV2> findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeader)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchGrHeader.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return grheaderService.findGrHeaderV2(searchGrHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = GrHeaderV2.class, value = "Get a GrHeader V2") // label for swagger
    @GetMapping("/v2/{goodsReceiptNo}")
    public ResponseEntity<?> getGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
                                           @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                           @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                           @RequestParam String palletCode, @RequestParam String caseCode) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            GrHeaderV2 grheader = grheaderService.getGrHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                    palletCode, caseCode);
            log.info("GrHeader : " + grheader);
            return new ResponseEntity<>(grheader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /**
     * @param newGrHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @ApiOperation(response = GrHeaderV2.class, value = "Create GrHeader V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postGrHeaderV2(@Valid @RequestBody GrHeaderV2 newGrHeader, @RequestParam String loginUserID)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(newGrHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            GrHeaderV2 createdGrHeader = grheaderService.createGrHeaderV2(newGrHeader, loginUserID);
            return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = GrHeaderV2.class, value = "Update GrHeader V2") // label for swagger
    @PatchMapping("/v2/{goodsReceiptNo}")
    public ResponseEntity<?> patchGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
                                             @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                             @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                             @RequestParam String palletCode, @RequestParam String caseCode,
                                             @Valid @RequestBody GrHeaderV2 updateGrHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(updateGrHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            GrHeaderV2 createdGrHeader =
                    grheaderService.updateGrHeaderV2(companyCode, languageId, plantId, warehouseId,
                            preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID, updateGrHeader);
            return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = GrHeaderV2.class, value = "Delete GrHeader V2") // label for swagger
    @DeleteMapping("/v2/{goodsReceiptNo}")
    public ResponseEntity<?> deleteGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
                                              @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                              @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                              @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            grheaderService.deleteGrHeaderV2(companyCode, languageId, plantId, warehouseId,
                    preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                    palletCode, caseCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}