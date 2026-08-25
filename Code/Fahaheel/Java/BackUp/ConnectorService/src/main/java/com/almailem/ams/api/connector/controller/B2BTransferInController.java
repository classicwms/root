package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.transferin.SearchTransferInHeader;
import com.almailem.ams.api.connector.model.transferin.SearchTransferInLine;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInLine;
import com.almailem.ams.api.connector.service.B2BTransferInService;
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

import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"B2BTransferIn"}, value = "B2BTransferIn Operations related to B2BTransferInController")
//label for Swagger
@SwaggerDefinition(tags = {@Tag(name = "B2BTransferIn", description = "Operations related to B2BTransferIn")})
@RequestMapping("/b2btransferin")
@RestController
public class B2BTransferInController {

    @Autowired
    B2BTransferInService b2BTransferInService;

    /**
     * Get All B2BTransferIn Details
     *
     * @return
     */
    @ApiOperation(response = TransferInHeader.class, value = "Get All B2BTransferIn Details")
    @GetMapping("")
    public ResponseEntity<?> getAllB2BTransferIns() {
        List<TransferInHeader> transferIns = b2BTransferInService.getAllB2BTransferInDetails();
        return new ResponseEntity<>(transferIns, HttpStatus.OK);
    }

    @ApiOperation(response = SearchTransferInHeader.class, value = "Find B2BTransferInHeader details")
    // label for swagger
    @PostMapping("/findB2BTransferInHeader")
    public ResponseEntity<?> findTransferInHeader(@RequestBody SearchTransferInHeader searchTransferInHeader) throws ParseException {
        List<TransferInHeader> transferInHeaderList = b2BTransferInService.findB2BTransferInHeader(searchTransferInHeader);
        return new ResponseEntity<>(transferInHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = TransferInLine.class, value = "Find B2BTransferInLine details")
    // label for swagger
    @PostMapping("/findB2BTransferInLine")
    public ResponseEntity<?> findB2BTransferInLine(@RequestBody SearchTransferInLine searchTransferInLine) throws ParseException {
        List<TransferInLine> transferInLineList = b2BTransferInService.findB2BTransferInLine(searchTransferInLine);
        return new ResponseEntity<>(transferInLineList, HttpStatus.OK);
    }

    @ApiOperation(response = TransferInHeader.class, value = "Update TransferIn Header")
    @PatchMapping("/updateTransferInHeader")
    public ResponseEntity<?> patchTransferInHeader(@RequestBody List<TransferInHeader> transferInHeaders) {
        List<TransferInHeader> transferInHeadersList = b2BTransferInService.updateTransferInHeader(transferInHeaders);
        return new ResponseEntity<>(transferInHeadersList, HttpStatus.OK);
    }
}
