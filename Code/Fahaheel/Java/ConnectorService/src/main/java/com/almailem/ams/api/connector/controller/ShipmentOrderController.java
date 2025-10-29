package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.transferout.FindTransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.FindTransferOutLine;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutLine;
import com.almailem.ams.api.connector.service.ShipmentOrderService;
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
@Api(tags = {"ShipmentOrderV2"}, value = "ShipmentOrderV2 Operations related to ShipmentOrderV2Controller")
@SwaggerDefinition(tags = {@Tag(name = "ShipmentOrderV2", description = "Operations related to ShipmentOrderV2")})
@RequestMapping("shipmentorderv2")
@RestController
public class ShipmentOrderController {

    @Autowired
    ShipmentOrderService soV2Service;

    //Get All ShipmentOrderV2 Details
    @ApiOperation(response = TransferOutHeader.class, value = "Get All ShipmentOrderV2 Details") //label for Swagger
    @GetMapping("")
    public ResponseEntity<?> getAllSoV2s() {
        List<TransferOutHeader> transferOuts = soV2Service.getAllSoV2Details();
        return new ResponseEntity<>(transferOuts, HttpStatus.OK);
    }

    // Find ShipmentOrder
    @ApiOperation(response = TransferOutHeader.class, value = "Find ShipmentOrder") // label for Swagger
    @PostMapping("/findShipmentOrder")
    public ResponseEntity<?> findShipmentOrder(@RequestBody FindTransferOutHeader findTransferOutHeader) throws ParseException {
        List<TransferOutHeader> transferOutHeaders = soV2Service.findShipmentOrder(findTransferOutHeader);
        return new ResponseEntity<>(transferOutHeaders, HttpStatus.OK);
    }

    @ApiOperation(response = TransferOutLine.class, value = "Find ShipmentOrderLine") // label for Swagger
    @PostMapping("/findShipmentOrderLine")
    public ResponseEntity<?> findShipmentOrderLine(@RequestBody FindTransferOutLine findTransferOutLine) throws ParseException {
        List<TransferOutLine> transferOutLines = soV2Service.findShipmentOrderLine(findTransferOutLine);
        return new ResponseEntity<>(transferOutLines, HttpStatus.OK);
    }

    @ApiOperation(response = TransferOutHeader.class, value = "Update TransferOutHeader")
    @PatchMapping("/updateTransOutHeader")
    public ResponseEntity<?> patchTransferOutHeader(@RequestBody List<TransferOutHeader> transferOutHeaders) {
        List<TransferOutHeader> transferOutHeadersList = soV2Service.updateTransferOutHeader(transferOutHeaders);
        return new ResponseEntity<>(transferOutHeadersList, HttpStatus.OK);
    }

}
