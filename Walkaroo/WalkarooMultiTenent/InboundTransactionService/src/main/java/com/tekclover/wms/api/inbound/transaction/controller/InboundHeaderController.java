package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.*;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundHeaderEntity;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.SearchInboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.InboundHeaderService;
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
@Api(tags = {"InboundHeader"}, value = "InboundHeader  Operations related to InboundHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InboundHeader ", description = "Operations related to InboundHeader ")})
@RequestMapping("/inboundheader")
@RestController
public class InboundHeaderController {

    @Autowired
    InboundHeaderService inboundheaderService;

    @Autowired
    BaseService baseService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = InboundHeader.class, value = "Get all InboundHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InboundHeader> inboundheaderList = inboundheaderService.getInboundHeaders();
        return new ResponseEntity<>(inboundheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Get a InboundHeader") // label for swagger 
    @GetMapping("/{refDocNumber}")
    public ResponseEntity<?> getInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                              @RequestParam String preInboundNo) {
        InboundHeaderEntity inboundheader = inboundheaderService.getInboundHeader(warehouseId, refDocNumber, preInboundNo);
        log.info("InboundHeader : " + inboundheader);
        return new ResponseEntity<>(inboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Get a PreInboundHeader") // label for swagger 
    @GetMapping("/inboundconfirm")
    public ResponseEntity<?> getPreInboundHeader(@RequestParam String warehouseId) {
        List<InboundHeaderEntity> inboundheader = inboundheaderService.getInboundHeaderWithStatusId(warehouseId);
        log.info("InboundHeader : " + inboundheader);
        return new ResponseEntity<>(inboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Search InboundHeader") // label for swagger
    @PostMapping("/findInboundHeader")
    public List<InboundHeader> findInboundHeader(@RequestBody SearchInboundHeader searchInboundHeader)
            throws Exception {
        return inboundheaderService.findInboundHeader(searchInboundHeader);
    }

    //Stream
    @ApiOperation(response = InboundHeader.class, value = "Search InboundHeader New") // label for swagger
    @PostMapping("/findInboundHeaderNew")
    public Stream<InboundHeader> findInboundHeaderNew(@RequestBody SearchInboundHeader searchInboundHeader)
            throws Exception {
        return inboundheaderService.findInboundHeaderNew(searchInboundHeader);
    }

    @ApiOperation(response = InboundHeader.class, value = "Create InboundHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInboundHeader(@Valid @RequestBody AddInboundHeader newInboundHeader,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newInboundHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundHeader createdInboundHeader = inboundheaderService.createInboundHeader(newInboundHeader, loginUserID);
            return new ResponseEntity<>(createdInboundHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeader.class, value = "Create InboundHeader") // label for swagger
    @GetMapping("/replaceASN")
    public ResponseEntity<?> replaceASN(@RequestParam String refDocNumber, @RequestParam String preInboundNo,
                                        @RequestParam String asnNumber)
            throws IllegalAccessException, InvocationTargetException {
        inboundheaderService.replaceASN(refDocNumber, preInboundNo, asnNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Update InboundHeader") // label for swagger
    @PatchMapping("/{refDocNumber}")
    public ResponseEntity<?> patchInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                @RequestParam String preInboundNo, @Valid @RequestBody UpdateInboundHeader updateInboundHeader,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updateInboundHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundHeader createdInboundHeader =
                    inboundheaderService.updateInboundHeader(warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader);
            return new ResponseEntity<>(createdInboundHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeader.class, value = "Inbound Header & Line Confirm") // label for swagger
    @GetMapping("/confirmIndividual")
    public ResponseEntity<?> updateInboundHeaderConfirm(@RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                        @RequestParam String refDocNumber, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        AXApiResponse createdInboundHeaderResponse =
                inboundheaderService.updateInboundHeaderConfirm(warehouseId, preInboundNo, refDocNumber, loginUserID);
        return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Delete InboundHeader") // label for swagger
    @DeleteMapping("/{refDocNumber}")
    public ResponseEntity<?> deleteInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                 @RequestParam String preInboundNo, @RequestParam String loginUserID) {
        inboundheaderService.deleteInboundHeader(warehouseId, refDocNumber, preInboundNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=================================================V2==========================================================
    @ApiOperation(response = InboundHeaderV2.class, value = "Search InboundHeader V2") // label for swagger
    @PostMapping("/findInboundHeader/v2")
    public List<InboundHeaderV2> findInboundHeaderV2(@RequestBody SearchInboundHeaderV2 searchInboundHeader)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchInboundHeader.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return inboundheaderService.findInboundHeaderV2(searchInboundHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Search InboundHeader Stream V2") // label for swagger
    @PostMapping("/findInboundHeader/v2/stream")
    public Stream<InboundHeaderV2> findInboundHeaderStreamV2(@RequestBody SearchInboundHeaderV2 searchInboundHeader)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchInboundHeader.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return inboundheaderService.findInboundHeaderStreamV2(searchInboundHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Create InboundHeader V2") // label for swagger
    @GetMapping("/replaceASN/v2")
    public ResponseEntity<?> replaceASNV2(@RequestParam String refDocNumber, @RequestParam String preInboundNo,
                                          @RequestParam String asnNumber)
            throws IllegalAccessException, InvocationTargetException {
        inboundheaderService.replaceASNV2(refDocNumber, preInboundNo, asnNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Get a InboundHeader") // label for swagger
    @GetMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> getInboundHeaderV2(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                @RequestParam String preInboundNo, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundHeaderEntityV2 inboundheader = inboundheaderService.getInboundHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            log.info("InboundHeader : " + inboundheader);
            return new ResponseEntity<>(inboundheader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Confirm") // label for swagger
    @GetMapping("/v2/confirmIndividual")
    public ResponseEntity<?> updateInboundHeaderConfirmV2(@RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                          @RequestParam String refDocNumber, @RequestParam String companyCode,
                                                          @RequestParam String plantId, @RequestParam String languageId,
                                                          @RequestParam String loginUserID) throws Exception {
//        AXApiResponse createdInboundHeaderResponse = inboundheaderService.updateInboundHeaderConfirmV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            inboundheaderService.updateInboundHeaderConfirmV3(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
            return new ResponseEntity<>(HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Partial Confirm")
    // label for swagger
    @GetMapping("/v2/partialConfirmIndividual")
    public ResponseEntity<?> updatePartialInboundHeaderConfirmV2(@RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                                 @RequestParam String refDocNumber, @RequestParam String companyCode,
                                                                 @RequestParam String plantId, @RequestParam String languageId,
                                                                 @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            AXApiResponse createdInboundHeaderResponse =
                    inboundheaderService.updateInboundHeaderPartialConfirmV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
            return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Partial Confirm New")
    // label for swagger
    @PostMapping("/v2/confirmIndividual/partial")
    public ResponseEntity<?> updatePartialInboundHeaderConfirmNewV2(@RequestBody List<InboundLineV2> inboundLineList, @RequestParam String warehouseId,
                                                                    @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String companyCode,
                                                                    @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            AXApiResponse createdInboundHeaderResponse =
                    inboundheaderService.updateInboundHeaderPartialConfirmNewV2(inboundLineList, companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
            return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Update InboundHeader") // label for swagger
    @PatchMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> patchInboundHeaderV2(@PathVariable String refDocNumber, @RequestParam String companyCode, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId,
                                                  @RequestParam String preInboundNo, @Valid @RequestBody InboundHeaderV2 updateInboundHeader,
                                                  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InboundHeaderV2 createdInboundHeader =
                    inboundheaderService.updateInboundHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader);
            return new ResponseEntity<>(createdInboundHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Delete InboundHeader") // label for swagger
    @DeleteMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> deleteInboundHeaderV2(@PathVariable String refDocNumber, @RequestParam String companyCode, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId,
                                                   @RequestParam String preInboundNo, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            inboundheaderService.deleteInboundHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}