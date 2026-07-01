package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceheader.*;
import com.tekclover.wms.api.transaction.service.ProformaInvoiceHeaderService;
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

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ProformaInvoiceHeader"}, value = "ProformaInvoiceHeader  Operations related to ProformaInvoiceHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProformaInvoiceHeader ",description = "Operations related to ProformaInvoiceHeader ")})
@RequestMapping("/proformainvoiceheader")
@RestController
public class ProformaInvoiceHeaderController {

    @Autowired
    ProformaInvoiceHeaderService proformaInvoiceHeaderService;

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Get all ProformaInvoiceHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ProformaInvoiceHeader> ProformaInvoiceHeaderList = proformaInvoiceHeaderService.getProformaInvoiceHeaders();
        return new ResponseEntity<>(ProformaInvoiceHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Get a ProformaInvoiceHeader") // label for swagger
    @GetMapping("/{proformaBillNo}")
    public ResponseEntity<?> getProformaInvoiceHeader( @PathVariable String proformaBillNo, @RequestParam  String partnerCode,
                                          @RequestParam String warehouseId) {
        ProformaInvoiceHeader ProformaInvoiceHeader =
                proformaInvoiceHeaderService.getProformaInvoiceHeader(warehouseId, proformaBillNo, partnerCode);
        log.info("ProformaInvoiceHeader : " + ProformaInvoiceHeader);
        return new ResponseEntity<>(ProformaInvoiceHeader, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Create ProformaInvoiceHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postProformaInvoiceHeader(@Valid @RequestBody AddProformaInvoiceHeader newProformaInvoiceHeader,
                                             @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceHeader createdProformaInvoiceHeader = proformaInvoiceHeaderService.createProformaInvoiceHeader(newProformaInvoiceHeader, loginUserID);
        return new ResponseEntity<>(createdProformaInvoiceHeader, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Update ProformaInvoiceHeader") // label for swagger
    @PatchMapping("/{proformaBillNo}")
    public ResponseEntity<?> patchProformaInvoiceHeader(@RequestParam String warehouseId, @PathVariable String proformaBillNo, @RequestParam String partnerCode,
                                              @Valid @RequestBody UpdateProformaInvoiceHeader updateProformaInvoiceHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceHeader createdProformaInvoiceHeader =
                proformaInvoiceHeaderService.updateProformaInvoiceHeader(warehouseId,  proformaBillNo, partnerCode,loginUserID, updateProformaInvoiceHeader);
        return new ResponseEntity<>(createdProformaInvoiceHeader, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Delete ProformaInvoiceHeader") // label for swagger
    @DeleteMapping("/{proformaBillNo}")
    public ResponseEntity<?> deleteProformaInvoiceHeader(@PathVariable String proformaBillNo,
                                               @RequestParam String warehouseId, @RequestParam String partnerCode, @RequestParam String loginUserID) {
        proformaInvoiceHeaderService.deleteProformaInvoiceHeader(warehouseId, proformaBillNo, partnerCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
