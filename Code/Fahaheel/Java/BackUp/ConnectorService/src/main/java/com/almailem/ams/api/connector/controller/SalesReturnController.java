package com.almailem.ams.api.connector.controller;


import com.almailem.ams.api.connector.model.salesreturn.FindSalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.FindSalesReturnLine;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnLine;
import com.almailem.ams.api.connector.repository.SalesReturnHeaderRepository;
import com.almailem.ams.api.connector.service.SalesReturnService;
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
@Api(tags = {"SalesReturn"}, value = "SalesReturn  Operations related to StagingLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SalesReturn ",description = "Operations related to SalesReturn ")})
@RequestMapping("/salesReturn")
@RestController
public class SalesReturnController {

    @Autowired
    private SalesReturnService salesReturnService;

    @ApiOperation(response = SalesReturnHeader.class, value = "Get all Sales Return Header details")
    @GetMapping("")
    public ResponseEntity<?> getAll(){
      List<SalesReturnHeader> salesReturnHeaderRepositoryList = salesReturnService.getAllSalesReturnHeader();
      return new ResponseEntity<>(salesReturnHeaderRepositoryList, HttpStatus.OK);
    }

    // Find SalesReturnHeader
    @ApiOperation(response = SalesReturnHeader.class, value = "Find Sales Return Header") // label for Swagger
    @PostMapping("/findsalesreturnheader")
    public ResponseEntity<?> searchSalesReturnHeader(@RequestBody FindSalesReturnHeader findSalesReturnHeader) throws ParseException {
        List<SalesReturnHeader> salesReturnHeaderList = salesReturnService.findSalesReturnHeader(findSalesReturnHeader);
        return new ResponseEntity<>(salesReturnHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = SalesReturnLine.class, value = "Find Sales Return Line") // label for Swagger
    @PostMapping("/findsalesreturnline")
    public ResponseEntity<?> searchSalesReturnLine(@RequestBody FindSalesReturnLine findSalesReturnLine) throws ParseException {
        List<SalesReturnLine> salesReturnLineList = salesReturnService.findSalesReturnLine(findSalesReturnLine);
        return new ResponseEntity<>(salesReturnLineList, HttpStatus.OK);
    }

    @ApiOperation(response = SalesReturnHeader.class, value = "Update SalesReturnHeader")
    @PatchMapping("/updateSalesReturnHeader")
    public ResponseEntity<?> patchSalesReturnHeader(@RequestBody List<SalesReturnHeader> salesReturnHeaders) {
        List<SalesReturnHeader> salesReturnHeaderList = salesReturnService.updateSalesReturnHeader(salesReturnHeaders);
        return new ResponseEntity<>(salesReturnHeaderList, HttpStatus.OK);
    }

}
