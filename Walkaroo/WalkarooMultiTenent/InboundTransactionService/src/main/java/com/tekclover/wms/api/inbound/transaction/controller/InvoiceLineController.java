package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.threepl.invoiceline.AddInvoiceLine;
import com.tekclover.wms.api.inbound.transaction.model.threepl.invoiceline.InvoiceLine;
import com.tekclover.wms.api.inbound.transaction.model.threepl.invoiceline.UpdateInvoiceLine;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.InvoiceLineService;
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
@Api(tags = {"InvoiceLine"}, value = "InvoiceLine  Operations related to InvoiceLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InvoiceLine ", description = "Operations related to InvoiceLine ")})
@RequestMapping("/invoiceline")
@RestController
public class InvoiceLineController {

    @Autowired
    InvoiceLineService invoiceLineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = InvoiceLine.class, value = "Get all InvoiceLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InvoiceLine> InvoiceLineList = invoiceLineService.getInvoiceLines();
        return new ResponseEntity<>(InvoiceLineList, HttpStatus.OK);
    }

    @ApiOperation(response = InvoiceLine.class, value = "Get a InvoiceLine") // label for swagger
    @GetMapping("/{lineNumber}")//String warehouseId, String invoiceNumber, String partnerCode,Long lineNumber
    public ResponseEntity<?> getInvoiceLine(@RequestParam String invoiceNumber, @PathVariable Long lineNumber, @RequestParam String partnerCode,
                                            @RequestParam String languageId, @RequestParam String companyCodeId,
                                            @RequestParam String plantId, @RequestParam String warehouseId) {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InvoiceLine InvoiceLine =
                    invoiceLineService.getInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, lineNumber);
            log.info("InvoiceLine : " + InvoiceLine);
            return new ResponseEntity<>(InvoiceLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InvoiceLine.class, value = "Create InvoiceLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInvoiceLine(@Valid @RequestBody AddInvoiceLine newInvoiceLine,
                                             @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newInvoiceLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InvoiceLine createdInvoiceLine = invoiceLineService.createInvoiceLine(newInvoiceLine, loginUserID);
            return new ResponseEntity<>(createdInvoiceLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InvoiceLine.class, value = "Update InvoiceLine") // label for swagger
    @PatchMapping("/{lineNumber}")
    public ResponseEntity<?> patchInvoiceLine(@PathVariable Long lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId,
                                              @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String invoiceNumber, @RequestParam String partnerCode,
                                              @Valid @RequestBody UpdateInvoiceLine updateInvoiceLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updateInvoiceLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InvoiceLine createdInvoiceLine =
                    invoiceLineService.updateInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, lineNumber, loginUserID, updateInvoiceLine);
            return new ResponseEntity<>(createdInvoiceLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InvoiceLine.class, value = "Delete InvoiceLine") // label for swagger
    @DeleteMapping("/{lineNumber}")
    public ResponseEntity<?> deleteInvoiceLine(@PathVariable Long lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId,
                                               @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String invoiceNumber,
                                               @RequestParam String partnerCode, @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            invoiceLineService.deleteInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, lineNumber, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
