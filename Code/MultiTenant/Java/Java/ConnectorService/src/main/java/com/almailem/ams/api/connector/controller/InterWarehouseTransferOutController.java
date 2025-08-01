package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.transferout.FindTransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.FindTransferOutLine;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutLine;
import com.almailem.ams.api.connector.service.InterWarehouseTransferOutService;
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
@Api(tags = {"InterWarehouseTransferOutV2"}, value = "InterWarehouseTransferOutV2 Operations related to InterWarehouseTransferOutV2Controller")
@SwaggerDefinition(tags = {@Tag(name = "InterWarehouseTransferOutV2", description = "Operations related to InterWarehouseTransferOutV2")})
@RequestMapping("/interwarehousetransferoutv2")
@RestController
public class InterWarehouseTransferOutController {

    @Autowired
    InterWarehouseTransferOutService iWhTransferOutV2Service;

    //Get All InterWarehouseTransferOutV2 Details
    @ApiOperation(response = TransferOutHeader.class, value = "Get All InterWarehouseTransferOutV2 Details")
    @GetMapping("")
    public ResponseEntity<?> getAllInterWhTransferOutV2s() {
        List<TransferOutHeader> transferOuts = iWhTransferOutV2Service.getAllInterWhTransferOutV2Details();
        return new ResponseEntity<>(transferOuts, HttpStatus.OK);
    }

    // Find InterWarehouseTransferOut
    @ApiOperation(response = TransferOutHeader.class, value = "Find InterWarehouseTransferOut") // label for Swagger
    @PostMapping("/findInterWarehouseTransferOut")
    public ResponseEntity<?> findInterWarehouseTransferOut(@RequestBody FindTransferOutHeader findTransferOutHeader) throws ParseException {
        List<TransferOutHeader> transferOutHeaders = iWhTransferOutV2Service.findInterWarehouseTransferOut(findTransferOutHeader);
        return new ResponseEntity<>(transferOutHeaders, HttpStatus.OK);
    }

    @ApiOperation(response = TransferOutLine.class, value = "Find InterWarehouseTransferOutLine") // label for Swagger
    @PostMapping("/findInterWarehouseTransferOutLine")
    public ResponseEntity<?> findInterWarehouseTransferOutLine(@RequestBody FindTransferOutLine findTransferOutLine) throws ParseException {
        List<TransferOutLine> transferOutLine = iWhTransferOutV2Service.findInterWarehouseTransferOutLine(findTransferOutLine);
        return new ResponseEntity<>(transferOutLine, HttpStatus.OK);
    }



}
