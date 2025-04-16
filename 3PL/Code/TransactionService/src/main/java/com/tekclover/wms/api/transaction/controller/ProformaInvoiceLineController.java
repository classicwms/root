package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.AddProformaInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.FindProformaInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.UpdateProformaInvoiceLine;
import com.tekclover.wms.api.transaction.service.ProformaInvoiceLineService;
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
@Api(tags = {"ProformaInvoiceLine"}, value = "ProformaInvoiceLine  Operations related to ProformaInvoiceLineController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProformaInvoiceLine ", description = "Operations related to ProformaInvoiceLine ")})
@RequestMapping("/proformaInvoiceLine")
@RestController
public class ProformaInvoiceLineController {

    @Autowired
    ProformaInvoiceLineService proformaInvoiceLineService;

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Get all ProformaInvoiceLine details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ProformaInvoiceLine> ProformaInvoiceLineList = proformaInvoiceLineService.getProformaInvoiceLines();
        return new ResponseEntity<>(ProformaInvoiceLineList, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Get a ProformaInvoiceLine") // label for swagger
    @GetMapping("/{lineNumber}")
    public ResponseEntity<?> getProformaInvoiceLine(@RequestParam String proformaBillNo, @PathVariable Long lineNumber, @RequestParam String partnerCode,
                                                    @RequestParam String languageId, @RequestParam String companyCodeId,
                                                    @RequestParam String plantId, @RequestParam String warehouseId) {
        ProformaInvoiceLine ProformaInvoiceLine =
                proformaInvoiceLineService.getProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber);
        log.info("ProformaInvoiceLine : " + ProformaInvoiceLine);
        return new ResponseEntity<>(ProformaInvoiceLine, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Create ProformaInvoiceLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postProformaInvoiceLine(@Valid @RequestBody AddProformaInvoiceLine newProformaInvoiceLine,
                                                     @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceLine createdProformaInvoiceLine = proformaInvoiceLineService.createProformaInvoiceLine(newProformaInvoiceLine, loginUserID);
        return new ResponseEntity<>(createdProformaInvoiceLine, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Update ProformaInvoiceLine") // label for swagger
    @PatchMapping("/update")
    public ResponseEntity<?> patchProformaInvoiceLine(@Valid @RequestBody List<AddProformaInvoiceLine> updateProformaInvoiceLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<ProformaInvoiceLine> createdProformaInvoiceLine =
                proformaInvoiceLineService.updateProformaInvoiceLine(updateProformaInvoiceLine, loginUserID);
        return new ResponseEntity<>(createdProformaInvoiceLine, HttpStatus.OK);
    }

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Delete ProformaInvoiceLine") // label for swagger
    @DeleteMapping("/{lineNumber}")
    public ResponseEntity<?> deleteProformaInvoiceLine(@PathVariable Long lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String warehouseId, @RequestParam String proformaBillNo, @RequestParam String partnerCode, @RequestParam String loginUserID) {
        proformaInvoiceLineService.deleteProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @ApiOperation(response = ProformaInvoiceLine.class, value = "Search ProformaInvoiceLine") // label for swagger
    @PostMapping("/findProformaInvoiceLine")
    public List<ProformaInvoiceLine> findProformaInvoiceLine(@RequestBody FindProformaInvoiceLine searchProformaInvoiceLine)
            throws Exception {
        return proformaInvoiceLineService.findProformaInvoiceLine(searchProformaInvoiceLine);
    }
}
