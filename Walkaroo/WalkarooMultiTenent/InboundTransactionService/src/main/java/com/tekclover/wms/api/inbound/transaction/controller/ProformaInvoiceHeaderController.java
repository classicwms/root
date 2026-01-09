package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.threepl.proformainvoiceheader.AddProformaInvoiceHeader;
import com.tekclover.wms.api.inbound.transaction.model.threepl.proformainvoiceheader.ProformaInvoiceHeader;
import com.tekclover.wms.api.inbound.transaction.model.threepl.proformainvoiceheader.UpdateProformaInvoiceHeader;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.ProformaInvoiceHeaderService;
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
@Api(tags = {"ProformaInvoiceHeader"}, value = "ProformaInvoiceHeader  Operations related to ProformaInvoiceHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ProformaInvoiceHeader ", description = "Operations related to ProformaInvoiceHeader ")})
@RequestMapping("/proformainvoiceheader")
@RestController
public class ProformaInvoiceHeaderController {

    @Autowired
    ProformaInvoiceHeaderService proformaInvoiceHeaderService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

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
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ProformaInvoiceHeader ProformaInvoiceHeader =
                    proformaInvoiceHeaderService.getProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode);
            log.info("ProformaInvoiceHeader : " + ProformaInvoiceHeader);
            return new ResponseEntity<>(ProformaInvoiceHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Create ProformaInvoiceHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postProformaInvoiceHeader(@Valid @RequestBody AddProformaInvoiceHeader newProformaInvoiceHeader,
                                                       @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newProformaInvoiceHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ProformaInvoiceHeader createdProformaInvoiceHeader = proformaInvoiceHeaderService.createProformaInvoiceHeader(newProformaInvoiceHeader, loginUserID);
            return new ResponseEntity<>(createdProformaInvoiceHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Update ProformaInvoiceHeader") // label for swagger
    @PatchMapping("/{proformaBillNo}")
    public ResponseEntity<?> patchProformaInvoiceHeader(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                        @RequestParam String warehouseId, @PathVariable String proformaBillNo, @RequestParam String partnerCode,
                                                        @Valid @RequestBody UpdateProformaInvoiceHeader updateProformaInvoiceHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ProformaInvoiceHeader createdProformaInvoiceHeader =
                    proformaInvoiceHeaderService.updateProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, loginUserID, updateProformaInvoiceHeader);
            return new ResponseEntity<>(createdProformaInvoiceHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ProformaInvoiceHeader.class, value = "Delete ProformaInvoiceHeader") // label for swagger
    @DeleteMapping("/{proformaBillNo}")
    public ResponseEntity<?> deleteProformaInvoiceHeader(@PathVariable String proformaBillNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                         @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String partnerCode, @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            proformaInvoiceHeaderService.deleteProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}
