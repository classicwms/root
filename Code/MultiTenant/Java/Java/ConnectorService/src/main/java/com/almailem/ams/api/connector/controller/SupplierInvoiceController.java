package com.almailem.ams.api.connector.controller;

import java.text.ParseException;
import java.util.List;

import com.almailem.ams.api.connector.model.supplierinvoice.SearchSupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SearchSupplierInvoiceLine;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.almailem.ams.api.connector.service.SupplierInvoiceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"SupplierInvoice"}, value = "SupplierInvoice  Operations related to StagingLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SupplierInvoice ",description = "Operations related to SupplierInvoice ")})
@RequestMapping("/supplierinvoice")
@RestController
public class SupplierInvoiceController {

    @Autowired
    private SupplierInvoiceService supplierInvoiceService;

    @ApiOperation(response = SupplierInvoiceHeader.class, value = "Get all Supplier Invoice Header details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<SupplierInvoiceHeader> supplierInvoiceHeaderList = supplierInvoiceService.getAllSupplierInvoiceHeader();
        return new ResponseEntity<>(supplierInvoiceHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = SupplierInvoiceHeader.class, value = "Find Supplier Invoice Header details") // label for swagger
    @PostMapping("/findSupplierInvoiceHeader")
    public  ResponseEntity<?> findSupplierInvoiceHeader(@RequestBody SearchSupplierInvoiceHeader searchSupplierInvoiceHeader) throws ParseException {
        List<SupplierInvoiceHeader>  supplierInvoiceHeaderList = supplierInvoiceService.findSupplierInvoiceHeader(searchSupplierInvoiceHeader);
        return new ResponseEntity<>(supplierInvoiceHeaderList, HttpStatus.OK);
    }


    @ApiOperation(response = SupplierInvoiceLine.class, value = "Find Supplier Invoice Line details") // label for swagger
    @PostMapping("/findSupplierInvoiceLine")
    public  ResponseEntity<?> findSupplierInvoiceLine(@RequestBody SearchSupplierInvoiceLine searchSupplierInvoiceLine) throws ParseException {
        List<SupplierInvoiceLine>  supplierInvoiceLineList = supplierInvoiceService.findSupplierInvoiceLine(searchSupplierInvoiceLine);
        return new ResponseEntity<>(supplierInvoiceLineList, HttpStatus.OK);
    }


}
