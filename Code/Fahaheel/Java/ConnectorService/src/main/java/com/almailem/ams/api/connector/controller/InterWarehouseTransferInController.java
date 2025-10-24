package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.transferin.SearchTransferInHeader;
import com.almailem.ams.api.connector.model.transferin.SearchTransferInLine;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInLine;
import com.almailem.ams.api.connector.service.InterWarehouseTransferInService;
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
@Api(tags = {"InterWarehouseTransferInV2"}, value = "InterWarehouseTransferInV2 Operations related to InterWarehouseTransferInV2Controller")
@SwaggerDefinition(tags = {@Tag(name = "InterWarehouseTransferInV2", description = "Operations related to InterWarehouseTransferInV2")})
@RequestMapping("/interwarehousetransferinv2")
@RestController
public class InterWarehouseTransferInController {

    @Autowired
    InterWarehouseTransferInService interWhTransferInV2Service;

    /**
     * Get All InterWarehouseTransferInV2 Details
     *
     * @return
     */
    @ApiOperation(response = TransferInHeader.class, value = "Get All InterWarehouseTransferInV2 Details")
    @GetMapping("")
    public ResponseEntity<?> getAllInterWhTransferInV2s() {
        List<TransferInHeader> transferIns = interWhTransferInV2Service.getAllInterWhTransferInV2Details();
        return new ResponseEntity<>(transferIns, HttpStatus.OK);
    }

    //Find InterWareHouseTransferInHeader
    @ApiOperation(response = TransferInHeader.class, value = "Find InterWareHouseTransferInHeader details")
    // label for swagger
    @PostMapping("/findInterWareHouseTransferInHeader")
    public ResponseEntity<?> findInterWareHouseTransferInHeader(@RequestBody SearchTransferInHeader searchTransferInHeader) throws ParseException {
        List<TransferInHeader> transferInHeaderList = interWhTransferInV2Service.findInterWareHouseTransferInHeader(searchTransferInHeader);
        return new ResponseEntity<>(transferInHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = TransferInLine.class, value = "Find InterWareHouseTransferInLine details")
    // label for swagger
    @PostMapping("/findInterWareHouseTransferInLine")
    public ResponseEntity<?> findInterWareHouseTransferInLine(@RequestBody SearchTransferInLine searchTransferInLine) throws ParseException {
        List<TransferInLine> transferInLineList = interWhTransferInV2Service.findInterWareHouseTransferInLine(searchTransferInLine);
        return new ResponseEntity<>(transferInLineList, HttpStatus.OK);
    }

}
