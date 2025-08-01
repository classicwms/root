package com.tekclover.wms.api.outbound.transaction.controller;


import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.threepl.invoiceheader.AddInvoiceHeader;
import com.tekclover.wms.api.outbound.transaction.model.threepl.invoiceheader.InvoiceHeader;
import com.tekclover.wms.api.outbound.transaction.model.threepl.invoiceheader.UpdateInvoiceHeader;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.InvoiceHeaderService;
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
@Api(tags = {"InvoiceHeader"}, value = "InvoiceHeader  Operations related to InvoiceHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InvoiceHeader ", description = "Operations related to InvoiceHeader ")})
@RequestMapping("/invoiceheader")
@RestController
public class InvoiceHeaderController {
    @Autowired
    InvoiceHeaderService invoiceHeaderService;

    @Autowired
    DbConfigRepository dbConfigRepository ;
    @ApiOperation(response = InvoiceHeader.class, value = "Get all InvoiceHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InvoiceHeader> InvoiceHeaderList = invoiceHeaderService.getInvoiceHeaders();
        return new ResponseEntity<>(InvoiceHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = InvoiceHeader.class, value = "Get a InvoiceHeader") // label for swagger
    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceHeader(@PathVariable String invoiceNumber, @RequestParam String partnerCode,
                                              @RequestParam String languageId, @RequestParam String companyCodeId,
                                              @RequestParam String plantId, @RequestParam String warehouseId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        InvoiceHeader InvoiceHeader =
                invoiceHeaderService.getInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode);
        log.info("InvoiceHeader : " + InvoiceHeader);
        return new ResponseEntity<>(InvoiceHeader, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = InvoiceHeader.class, value = "Create InvoiceHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInvoiceHeader(@Valid @RequestBody AddInvoiceHeader newInvoiceHeader,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newInvoiceHeader.getCompanyCodeId(), newInvoiceHeader.getPlantId(), newInvoiceHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        InvoiceHeader createdInvoiceHeader = invoiceHeaderService.createInvoiceHeader(newInvoiceHeader, loginUserID);
        return new ResponseEntity<>(createdInvoiceHeader, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = InvoiceHeader.class, value = "Update InvoiceHeader") // label for swagger
    @PatchMapping("/{invoiceNumber}")
    public ResponseEntity<?> patchInvoiceHeader(@PathVariable String invoiceNumber, @RequestParam String languageId, @RequestParam String companyCodeId,
                                                @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String partnerCode,
                                                @Valid @RequestBody UpdateInvoiceHeader updateInvoiceHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        InvoiceHeader createdInvoiceHeader =
                invoiceHeaderService.updateInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, loginUserID, updateInvoiceHeader);
        return new ResponseEntity<>(createdInvoiceHeader, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = InvoiceHeader.class, value = "Delete InvoiceHeader") // label for swagger
    @DeleteMapping("/{invoiceNumber}")
    public ResponseEntity<?> deleteInvoiceHeader(@PathVariable String invoiceNumber, @RequestParam String languageId, @RequestParam String companyCodeId,
                                                 @RequestParam String plantId,
                                                 @RequestParam String warehouseId, @RequestParam String partnerCode, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        invoiceHeaderService.deleteInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
