package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.outbound.*;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2Stream;
import com.tekclover.wms.api.transaction.model.outbound.v2.SearchOutboundHeaderV2;
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

import com.tekclover.wms.api.transaction.service.OutboundHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"OutboundHeader"}, value = "OutboundHeader  Operations related to OutboundHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundHeader ", description = "Operations related to OutboundHeader ")})
@RequestMapping("/outboundheader")
@RestController
public class OutboundHeaderController {

    @Autowired
    OutboundHeaderService outboundheaderService;

    @ApiOperation(response = OutboundHeader.class, value = "Get all OutboundHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<OutboundHeader> outboundheaderList = outboundheaderService.getOutboundHeaders();
        return new ResponseEntity<>(outboundheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Get a OutboundHeader") // label for swagger 
    @GetMapping("/{preOutboundNo}")
    public ResponseEntity<?> getOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String warehouseId,
                                               @RequestParam String refDocNumber, @RequestParam String partnerCode) {
        OutboundHeader outboundheader =
                outboundheaderService.getOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        log.info("OutboundHeader : " + outboundheader);
        return new ResponseEntity<>(outboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader") // label for swagger
    @PostMapping("/findOutboundHeader")
//	public List<OutboundHeader> findOutboundHeader(@RequestBody SearchOutboundHeader searchOutboundHeader)
    public List<OutboundHeader> findOutboundHeader(@RequestBody SearchOutboundHeader searchOutboundHeader, @RequestParam Integer flag)
            throws Exception {
        return outboundheaderService.findOutboundHeader(searchOutboundHeader, flag);
//		return outboundheaderService.findOutboundHeader(searchOutboundHeader);
    }

    //===================================STREAMING=================================================
    @ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader New") // label for swagger
    @PostMapping("/findOutboundHeaderNew")
    public List<OutboundHeaderStream> findOutboundHeaderNew(@RequestBody SearchOutboundHeader searchOutboundHeader, @RequestParam Integer flag)
            throws Exception {
        return outboundheaderService.findOutboundHeadernew(searchOutboundHeader, flag);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Create OutboundHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postOutboundHeader(@Valid @RequestBody AddOutboundHeader newOutboundHeader,
                                                @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        OutboundHeader createdOutboundHeader = outboundheaderService.createOutboundHeader(newOutboundHeader, loginUserID);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Update OutboundHeader") // label for swagger
    @PatchMapping("/{preOutboundNo}")
    public ResponseEntity<?> patchOutboundHeader(@PathVariable String preOutboundNo,
                                                 @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @Valid @RequestBody UpdateOutboundHeader updateOutboundHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        OutboundHeader createdOutboundHeader =
                outboundheaderService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        updateOutboundHeader, loginUserID);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Delete OutboundHeader") // label for swagger
    @DeleteMapping("/{preOutboundNo}")
    public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo,
                                                  @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam String loginUserID) {
        outboundheaderService.deleteOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=======================================================V2============================================================

    @ApiOperation(response = OutboundHeaderV2.class, value = "Get all OutboundHeader details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllOutboundHeader() {
        List<OutboundHeaderV2> outboundheaderList = outboundheaderService.getOutboundHeadersV2();
        return new ResponseEntity<>(outboundheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Get a OutboundHeader") // label for swagger
    @GetMapping("/v2/{preOutboundNo}")
    public ResponseEntity<?> getOutboundHeaderV2(@PathVariable String preOutboundNo, @RequestParam String companyCodeId,
                                                 @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                 @RequestParam String refDocNumber, @RequestParam String partnerCode) {
        OutboundHeaderV2 outboundheader =
                outboundheaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        log.info("OutboundHeader : " + outboundheader);
        return new ResponseEntity<>(outboundheader, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader") // label for swagger
    @PostMapping("/v2/findOutboundHeader")
    public Stream<OutboundHeaderV2> findOutboundHeaderV2(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader)
            throws Exception {
        return outboundheaderService.findOutboundHeaderV2(searchOutboundHeader);
    }

    //Stream
    @ApiOperation(response = OutboundHeaderV2Stream.class, value = "Search OutboundHeader Stream") // label for swagger
    @PostMapping("/v2/findOutboundHeaderStream")
    public List<OutboundHeaderV2Stream> findOutboundHeaderStreamV2(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader)
            throws Exception {
        return outboundheaderService.findOutboundHeadernewV2(searchOutboundHeader);
    }

    //This Method for seperate consignment Tab in Delivery
    @ApiOperation(response = OutboundHeaderV2Stream.class, value = "Search OutboundHeader Stream - Consignment Tab") // label for swagger
    @PostMapping("/v2/findOutboundHeader/delivery")
    public List<OutboundHeaderV2Stream> findOutboundHeaderDelivery(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader)
            throws Exception {
        return outboundheaderService.findOutboundHeaderForDeliveryV2(searchOutboundHeader);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader Rfd") // label for swagger
    @PostMapping("/v2/findOutboundHeaderRfd")
    public List<OutboundHeaderV2Stream> findOutboundHeaderrfdV2(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader)
            throws Exception {
        return outboundheaderService.findOutboundHeaderRfdV2(searchOutboundHeader);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Create OutboundHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postOutboundHeaderV2(@Valid @RequestBody OutboundHeaderV2 newOutboundHeader,
                                                  @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        OutboundHeaderV2 createdOutboundHeader = outboundheaderService.createOutboundHeaderV2(newOutboundHeader, loginUserID);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Update OutboundHeader") // label for swagger
    @PatchMapping("/v2/{preOutboundNo}")
    public ResponseEntity<?> patchOutboundHeaderV2(@PathVariable String preOutboundNo, @RequestParam String companyCodeId,
                                                   @RequestParam String plantId, @RequestParam String languageId,
                                                   @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                   @Valid @RequestBody OutboundHeaderV2 updateOutboundHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        OutboundHeaderV2 createdOutboundHeader =
                outboundheaderService.updateOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        updateOutboundHeader, loginUserID);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Delete OutboundHeader") // label for swagger
    @DeleteMapping("/v2/{preOutboundNo}")
    public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String companyCodeId,
                                                  @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                  @RequestParam String partnerCode, @RequestParam String loginUserID) throws ParseException {
        outboundheaderService.deleteOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}