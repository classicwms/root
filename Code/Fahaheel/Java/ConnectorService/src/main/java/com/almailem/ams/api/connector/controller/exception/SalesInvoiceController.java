package com.almailem.ams.api.connector.controller.exception;

import com.almailem.ams.api.connector.model.salesinvoice.FindSalesInvoice;
import com.almailem.ams.api.connector.model.salesinvoice.SalesInvoice;
import com.almailem.ams.api.connector.service.SalesInvoiceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"SalesInvoice"}, value = "SalesInvoice Operations related to SalesInvoiceController")
@SwaggerDefinition(tags = {@Tag(name = "SalesInvoice", description = "Operations related to SalesInvoice")})
@RequestMapping("/salesinvoice")
@RestController
public class SalesInvoiceController {

    @Autowired
    SalesInvoiceService salesInvoiceService;

    // FInd SalesInvoice
    @ApiOperation(response = SalesInvoice.class, value = "Find SalesInvoice") // label for Swagger
    @PostMapping("/findSalesInvoice")
    public ResponseEntity<?> searchSalesInvoice(@RequestBody FindSalesInvoice findSalesInvoice) throws ParseException {
        List<SalesInvoice> salesInvoice = salesInvoiceService.findSalesInvoice(findSalesInvoice);
        return new ResponseEntity<>(salesInvoice, HttpStatus.OK);
    }

}
