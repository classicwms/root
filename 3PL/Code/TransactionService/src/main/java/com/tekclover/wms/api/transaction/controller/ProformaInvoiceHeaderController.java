package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.threepl.invoiceheader.FindInvoice;
import com.tekclover.wms.api.transaction.model.threepl.invoiceheader.Invoice;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ProformaInvoiceHeader"}, value = "ProformaInvoiceHeader  Operations related to ProformaInvoiceHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProformaInvoiceHeader ", description = "Operations related to ProformaInvoiceHeader ")})
@RequestMapping("/proformaInvoiceHeader")
@RestController
public class ProformaInvoiceHeaderController {

    @Autowired
    ProformaInvoiceHeaderService proformaInvoiceHeaderService;

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Get all ProformaInvoiceHeader details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ProformaInvoiceHeader> ProformaInvoiceHeaderList = proformaInvoiceHeaderService.getProformaInvoiceHeaders();
        return new ResponseEntity<>(ProformaInvoiceHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Get a ProformaInvoiceHeader") // label for swagger
    @GetMapping("/{proformaBillNo}")
    public ResponseEntity<?> getProformaInvoiceHeader(@PathVariable String proformaBillNo, @RequestParam String partnerCode,
                                                      @RequestParam String companyCodeId, @RequestParam String plantId,
                                                      @RequestParam String languageId, @RequestParam String warehouseId) {
        ProformaInvoiceHeader ProformaInvoiceHeader =
                proformaInvoiceHeaderService.getProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode);
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
    public ResponseEntity<?> patchProformaInvoiceHeader(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                        @RequestParam String warehouseId, @PathVariable String proformaBillNo, @RequestParam String partnerCode,
                                                        @Valid @RequestBody UpdateProformaInvoiceHeader updateProformaInvoiceHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceHeader createdProformaInvoiceHeader =
                proformaInvoiceHeaderService.updateProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, loginUserID, updateProformaInvoiceHeader);
        return new ResponseEntity<>(createdProformaInvoiceHeader, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Delete ProformaInvoiceHeader") // label for swagger
    @DeleteMapping("/{proformaBillNo}")
    public ResponseEntity<?> deleteProformaInvoiceHeader(@PathVariable String proformaBillNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                         @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String partnerCode, @RequestParam String loginUserID) {
        proformaInvoiceHeaderService.deleteProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Search ProformaInvoiceHeader") // label for swagger
    @PostMapping("/findProformaInvoiceHeader")
    public List<ProformaInvoiceHeader> findProformaInvoiceHeader(@RequestBody FindProformaInvoiceHeader searchProformaInvoiceHeader)
            throws Exception {
        return proformaInvoiceHeaderService.findProformaInvoiceHeader(searchProformaInvoiceHeader);
    }

    @ApiOperation(response = ProformaInvoice.class, value = "Create ProformaInvoiceHeaderAndLine") // label for swagger
    @PostMapping("/headerAndLine")
    public ResponseEntity<?> postProformaInvoiceHeaderAndLine(@Valid @RequestBody List<ProformaInvoice> proformaInvoiceList,
                                                              @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        List<ProformaInvoice> createdProformaInvoiceHeaderAndLine = proformaInvoiceHeaderService.createProformaInvoiceHeaderAndLine(proformaInvoiceList, loginUserID);
        return new ResponseEntity<>(createdProformaInvoiceHeaderAndLine, HttpStatus.OK);
    }

    @ApiOperation(response = Invoice.class, value = "Invoice Execute") // label for swagger
    @PostMapping("/execute")
    public ResponseEntity<?> invoiceExecute(@Valid @RequestBody FindInvoice findInvoice) throws IllegalAccessException, InvocationTargetException, ParseException {
        List<ProformaInvoice> createdProformaInvoiceHeaderAndLine = proformaInvoiceHeaderService.executeInvoice(findInvoice);
        return new ResponseEntity<>(createdProformaInvoiceHeaderAndLine, HttpStatus.OK);
    }

}
