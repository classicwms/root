package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.stockreceipt.SearchStockReceiptHeader;
import com.almailem.ams.api.connector.model.stockreceipt.SearchStockReceiptLine;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptHeader;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptLine;
import com.almailem.ams.api.connector.service.StockReceiptService;
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
@Api(tags = {"StockReceipt"}, value = "StockReceipt Operations related to StockReceiptController") //label for Swagger
@SwaggerDefinition(tags = {@Tag(name = "StockReceipt", description = "Operations related to StockReceipt")})
@RequestMapping("/stockreceipt")
@RestController
public class StockReceiptController {

    @Autowired
    StockReceiptService stockReceiptService;

    //Get All StockReceipt Details
    @ApiOperation(response = StockReceiptHeader.class, value = "Get All Stock Receipt Details") //label for Swagger
    @GetMapping("")
    public ResponseEntity<?> getAllStockReceipts() {
        List<StockReceiptHeader> stockReceipts = stockReceiptService.getAllStockReceiptDetails();
        return new ResponseEntity<>(stockReceipts, HttpStatus.OK);
    }

    @ApiOperation(response = SearchStockReceiptHeader.class, value = "Find StockReceiptHeader details")
    // label for swagger
    @PostMapping("/findStockReceiptHeader")
    public ResponseEntity<?> findStockReceiptHeader(@RequestBody SearchStockReceiptHeader searchStockReceiptHeader) throws ParseException {
        List<StockReceiptHeader> stockReceiptHeaderList = stockReceiptService.findStockReceiptHeader(searchStockReceiptHeader);
        return new ResponseEntity<>(stockReceiptHeaderList, HttpStatus.OK);
    }
    @ApiOperation(response = StockReceiptLine.class, value = "Find StockReceiptLine details")
    // label for swagger
    @PostMapping("/findStockReceiptLine")
    public ResponseEntity<?> findStockReceiptLine(@RequestBody SearchStockReceiptLine searchStockReceiptLine) throws ParseException {
        List<StockReceiptLine> stockReceiptLineList = stockReceiptService.findStockReceiptLine(searchStockReceiptLine);
        return new ResponseEntity<>(stockReceiptLineList, HttpStatus.OK);
    }

    @ApiOperation(response = StockReceiptHeader.class, value = "Update StockReceiptHeader Header")
    @PatchMapping("/updateStockReceiptHeader")
    public ResponseEntity<?> patchStockReceiptHeader(@RequestBody List<StockReceiptHeader> stockReceiptHeaders) {
        List<StockReceiptHeader> stockReceiptHeaderList = stockReceiptService.updateStockReceiptHeader(stockReceiptHeaders);
        return new ResponseEntity<>(stockReceiptHeaderList, HttpStatus.OK);
    }

}
