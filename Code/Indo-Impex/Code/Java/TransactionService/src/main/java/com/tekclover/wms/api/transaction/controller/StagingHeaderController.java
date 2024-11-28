package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.inbound.staging.AddStagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.SearchStagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.UpdateStagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.SearchStagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.service.StagingHeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"StagingHeader"}, value = "StagingHeader  Operations related to StagingHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StagingHeader ", description = "Operations related to StagingHeader ")})
@RequestMapping("/stagingheader")
@RestController
public class StagingHeaderController {

    @Autowired
    StagingHeaderService stagingheaderService;

    @ApiOperation(response = StagingHeader.class, value = "Get all StagingHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StagingHeader> stagingheaderList = stagingheaderService.getStagingHeaders();
        return new ResponseEntity<>(stagingheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Get a StagingHeader") // label for swagger 
    @GetMapping("/{stagingNo}")
    public ResponseEntity<?> getStagingHeader(@PathVariable String stagingNo, @RequestParam String languageId, @RequestParam String companyCodeId,
                                              @RequestParam String plantId, @RequestParam String warehouseId,
                                              @RequestParam String preInboundNo, @RequestParam String refDocNumber) {
        StagingHeader stagingheader = stagingheaderService.getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo);
        log.info("StagingHeader : " + stagingheader);
        return new ResponseEntity<>(stagingheader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Search StagingHeader") // label for swagger
    @PostMapping("/findStagingHeader")
    public List<StagingHeader> findStagingHeader(@RequestBody SearchStagingHeader searchStagingHeader) throws Exception {
        return stagingheaderService.findStagingHeader(searchStagingHeader);
    }

    //Stream
    @ApiOperation(response = StagingHeader.class, value = "Search StagingHeader New") // label for swagger
    @PostMapping("/findStagingHeaderNew")
    public Stream<StagingHeader> findStagingHeaderNew(@RequestBody SearchStagingHeader searchStagingHeader) throws Exception {
        return stagingheaderService.findStagingHeaderNew(searchStagingHeader);
    }
    //===================================STREAMING=================================================

    @GetMapping(value = "/streaming/findStagingHeader")
    public ResponseEntity<StreamingResponseBody> findStreamStagingHeader() throws Exception {
        StreamingResponseBody responseBody = stagingheaderService.findStreamStagingHeader();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
    }

    @ApiOperation(response = StagingHeader.class, value = "Create StagingHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStagingHeader(@Valid @RequestBody AddStagingHeader newStagingHeader,
                                               @RequestParam String loginUserID) throws Exception {
        StagingHeader createdStagingHeader = stagingheaderService.createStagingHeader(newStagingHeader, loginUserID);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Update StagingHeader") // label for swagger
    @PatchMapping("/{stagingNo}")
    public ResponseEntity<?> patchStagingHeader(@PathVariable String stagingNo, @RequestParam String warehouseId,
                                                @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                @Valid @RequestBody UpdateStagingHeader updateStagingHeader,
                                                @RequestParam String loginUserID) throws Exception {
        StagingHeader createdStagingHeader =
                stagingheaderService.updateStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, loginUserID, updateStagingHeader);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Delete StagingHeader") // label for swagger
    @DeleteMapping("/{stagingNo}")
    public ResponseEntity<?> deleteStagingHeader(@PathVariable String stagingNo, @RequestParam String warehouseId,
                                                 @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                 @RequestParam String loginUserID) {
        stagingheaderService.deleteStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * Barccode Generation
     * ----------------------
     */
    @ApiOperation(response = StagingHeader.class, value = "Get a StagingHeader") // label for swagger 
    @GetMapping("/{numberOfCases}/barcode")
    public ResponseEntity<?> getStagingHeader(@PathVariable Long numberOfCases, @RequestParam String warehouseId) {
        List<String> stagingheader = stagingheaderService.generateNumberRanges(numberOfCases, warehouseId);
        log.info("StagingHeader : " + stagingheader);
        return new ResponseEntity<>(stagingheader, HttpStatus.OK);
    }

    //==========================================================V2============================================================

    @ApiOperation(response = StagingHeaderV2.class, value = "Get a StagingHeader V2") // label for swagger
    @GetMapping("/v2/{stagingNo}")
    public ResponseEntity<?> getStagingHeaderV2(@PathVariable String stagingNo, @RequestParam String languageId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                @RequestParam String refDocNumber) {
        StagingHeader stagingheader = stagingheaderService.getStagingHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo);
        log.info("StagingHeader : " + stagingheader);
        return new ResponseEntity<>(stagingheader, HttpStatus.OK);
    }

    //V2
    @ApiOperation(response = StagingHeaderV2.class, value = "Search StagingHeader V2") // label for swagger
    @PostMapping("/findStagingHeader/v2")
    public Stream<StagingHeaderV2> findStagingHeaderV2(@RequestBody SearchStagingHeaderV2 searchStagingHeader) throws Exception {
        return stagingheaderService.findStagingHeaderV2(searchStagingHeader);
    }

    @ApiOperation(response = StagingHeaderV2.class, value = "Create StagingHeader V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postStagingHeaderV2(@Valid @RequestBody StagingHeaderV2 newStagingHeader,
                                                 @RequestParam String loginUserID) throws Exception {
        StagingHeaderV2 createdStagingHeader = stagingheaderService.createStagingHeaderV2(newStagingHeader, loginUserID);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeaderV2.class, value = "Update StagingHeader V2") // label for swagger
    @PatchMapping("/v2/{stagingNo}")
    public ResponseEntity<?> patchStagingHeaderV2(@PathVariable String stagingNo, @RequestParam String languageId,
                                                  @RequestParam String companyCode, @RequestParam String plantId,
                                                  @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                  @RequestParam String refDocNumber, @Valid @RequestBody StagingHeaderV2 updateStagingHeader,
                                                  @RequestParam String loginUserID) throws Exception {
        StagingHeaderV2 createdStagingHeader =
                stagingheaderService.updateStagingHeaderV2(companyCode, plantId, languageId, warehouseId,
                                                           preInboundNo, refDocNumber, stagingNo, loginUserID, updateStagingHeader);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeaderV2.class, value = "Delete StagingHeader V2") // label for swagger
    @DeleteMapping("/v2/{stagingNo}")
    public ResponseEntity<?> deleteStagingHeaderV2(@PathVariable String stagingNo, @RequestParam String languageId,
                                                   @RequestParam String companyCode, @RequestParam String plantId,
                                                   @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                   @RequestParam String refDocNumber, @RequestParam String loginUserID) throws Exception {
        stagingheaderService.deleteStagingHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}