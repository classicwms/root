package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.threepl.proformainvoiceline.AddProformaInvoiceLine;
import com.tekclover.wms.api.inbound.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import com.tekclover.wms.api.inbound.transaction.model.threepl.proformainvoiceline.UpdateProformaInvoiceLine;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.ProformaInvoiceLineService;
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
@Api(tags = {"ProformaInvoiceLine"}, value = "ProformaInvoiceLine  Operations related to ProformaInvoiceLineController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProformaInvoiceLine ", description = "Operations related to ProformaInvoiceLine ")})
@RequestMapping("/proformainvoiceline")
@RestController
public class ProformaInvoiceLineController {

    @Autowired
    ProformaInvoiceLineService proformaInvoiceLineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

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
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ProformaInvoiceLine ProformaInvoiceLine =
                    proformaInvoiceLineService.getProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber);
            log.info("ProformaInvoiceLine : " + ProformaInvoiceLine);
            return new ResponseEntity<>(ProformaInvoiceLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Create ProformaInvoiceLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postProformaInvoiceLine(@Valid @RequestBody AddProformaInvoiceLine newProformaInvoiceLine,
                                                     @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newProformaInvoiceLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ProformaInvoiceLine createdProformaInvoiceLine = proformaInvoiceLineService.createProformaInvoiceLine(newProformaInvoiceLine, loginUserID);
            return new ResponseEntity<>(createdProformaInvoiceLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Update ProformaInvoiceLine") // label for swagger
    @PatchMapping("/{lineNumber}")
    public ResponseEntity<?> patchProformaInvoiceLine(@RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                      @RequestParam String warehouseId, @PathVariable Long lineNumber, @RequestParam String partnerCode,
                                                      @RequestParam String proformaBillNo, @RequestParam String loginUserID,
                                                      @Valid @RequestBody UpdateProformaInvoiceLine updateProformaInvoiceLine)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ProformaInvoiceLine createdProformaInvoiceLine =
                    proformaInvoiceLineService.updateProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber, loginUserID, updateProformaInvoiceLine);
            return new ResponseEntity<>(createdProformaInvoiceLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ProformaInvoiceLine.class, value = "Delete ProformaInvoiceLine") // label for swagger
    @DeleteMapping("/{lineNumber}")
    public ResponseEntity<?> deleteProformaInvoiceLine(@PathVariable Long lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String warehouseId, @RequestParam String proformaBillNo, @RequestParam String partnerCode, @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            proformaInvoiceLineService.deleteProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
