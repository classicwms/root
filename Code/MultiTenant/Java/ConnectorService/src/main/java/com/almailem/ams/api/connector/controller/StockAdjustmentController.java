package com.almailem.ams.api.connector.controller;


import com.almailem.ams.api.connector.model.stockadjustment.FindStockAdjustment;
import com.almailem.ams.api.connector.model.stockadjustment.StockAdjustment;
import com.almailem.ams.api.connector.service.StockAdjustmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"StockAdjustment"}, value = "StockAdjustment  Operations related to StagingLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StockAdjustment ",description = "Operations related to StockAdjustment ")})
@RequestMapping("/stockAdjustment")
@RestController
public class StockAdjustmentController {

    @Autowired
    StockAdjustmentService stockAdjustmentService;

    @ApiOperation(response = StockAdjustment.class, value = "Get all Stock Adjustment details")
    @GetMapping("")
    public ResponseEntity<?> getAll(){
        List<StockAdjustment> stockAdjustmentList = stockAdjustmentService.getAllStockAdjustment();
        return new ResponseEntity<>(stockAdjustmentList, HttpStatus.OK);
    }

    // Find StockAdjustment
    @ApiOperation(response = StockAdjustment.class, value = "Find StockAdjustment") // label for Swagger
    @PostMapping("/findStockAdjustment")
    public ResponseEntity<?> searchStockAdjustment(@RequestBody FindStockAdjustment findStockAdjustment) throws ParseException {
        List<StockAdjustment> stockAdjustmentList = stockAdjustmentService.findStockAdjustment(findStockAdjustment);
        return new ResponseEntity<>(stockAdjustmentList, HttpStatus.OK);
    }

}
