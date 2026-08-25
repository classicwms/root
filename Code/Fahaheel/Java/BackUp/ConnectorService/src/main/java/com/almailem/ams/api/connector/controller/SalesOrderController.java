package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.picklist.FindPickListHeader;
import com.almailem.ams.api.connector.model.picklist.FindPickListLine;
import com.almailem.ams.api.connector.model.picklist.PickListHeader;
import com.almailem.ams.api.connector.model.picklist.PickListLine;
import com.almailem.ams.api.connector.service.SalesOrderService;
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
@Api(tags = {"SalesOrderV2"}, value = "SalesOrderV2 Operations related to SalesOrderV2Controller") //label for Swagger
@SwaggerDefinition(tags = {@Tag(name = "SalesOrderV2", description = "Operations related to SalesOrderV2")})
@RequestMapping("/salesorderv2")
@RestController
public class SalesOrderController {

    @Autowired
    SalesOrderService salesOrderService;

    //Get All PickList Details
    @ApiOperation(response = PickListHeader.class, value = "Get All PickList Details") //label for Swagger
    @GetMapping("")
    public ResponseEntity<?> getAllSalesOrderV2s() {
        List<PickListHeader> pickLists = salesOrderService.getAllSalesOrderV2Details();
        return new ResponseEntity<>(pickLists, HttpStatus.OK);
    }

    // FInd PickListHeader
    @ApiOperation(response = PickListHeader.class, value = "Find PickListHeader") // label for Swagger
    @PostMapping("/findPickListHeader")
    public ResponseEntity<?> searchPickListHeader(@RequestBody FindPickListHeader findPickListHeader) throws ParseException {
        List<PickListHeader> pickListHeaders = salesOrderService.findPickListHeader(findPickListHeader);
        return new ResponseEntity<>(pickListHeaders, HttpStatus.OK);
    }

    // FInd PickListLine
    @ApiOperation(response = PickListLine.class, value = "Find PickListLine") // label for Swagger
    @PostMapping("/findPickListLine")
    public ResponseEntity<?> searchPickListLine(@RequestBody FindPickListLine findPickListLine) throws ParseException {
        List<PickListLine> pickListLines = salesOrderService.findPickListLine(findPickListLine);
        return new ResponseEntity<>(pickListLines, HttpStatus.OK);
    }

    @ApiOperation(response = PickListHeader.class, value = "Update PickListHeader")
    @PatchMapping("/updatePickListHeader")
    public ResponseEntity<?> patchPickListHeader(@RequestBody List<PickListHeader> pickListHeaders) {
        List<PickListHeader> pickListHeadersList = salesOrderService.updatePickListHeader(pickListHeaders);
        return new ResponseEntity<>(pickListHeadersList, HttpStatus.OK);
    }
}
