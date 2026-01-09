package com.tekclover.wms.api.inbound.transaction.controller;


import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.SearchSupplierInvoiceHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.SupplierInvoiceHeader;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.InvoiceCancellationService;
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

import java.text.ParseException;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"SupplierInvoiceCancel"}, value = "SupplierInvoice  Operations related to SupplierInvoiceCancellation")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SupplierInvoice ", description = "Operations related to SupplierInvoice ")})
@RequestMapping("/invoice")
@RestController
public class InvoiceCancellationController {

    @Autowired
    InvoiceCancellationService invoiceCancellationService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = InboundHeaderV2.class, value = "Replace SupplierInvoice")
    @GetMapping("/supplierInvoice/cancellation")
    public ResponseEntity<?> replaceSupplierInvoice(@RequestParam String companyCode, @RequestParam String languageId,
                                                    @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String oldInvoiceNo,
                                                    @RequestParam String newInvoiceNo, @RequestParam String oldPreInboundNo,
                                                    @RequestParam String newPreInboundNo, @RequestParam String loginUserId) throws ParseException {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            WarehouseApiResponse result = invoiceCancellationService.replaceSupplierInvoice(companyCode, languageId, plantId, warehouseId,
                    oldPreInboundNo, oldInvoiceNo, newInvoiceNo, newPreInboundNo, loginUserId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Inbound Order Cancellation")
    @PostMapping("/inboundOrderCancellation")
    public ResponseEntity<?> cancelInboundOrder(@RequestBody InboundOrderCancelInput inboundOrderCancelInput,
                                                @RequestParam String loginUserId) throws ParseException {

        try {
            String currentDB = baseService.getDataBase(inboundOrderCancelInput.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PreInboundHeaderEntityV2 result = invoiceCancellationService.inboundOrderCancellation(inboundOrderCancelInput, loginUserId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = SupplierInvoiceHeader.class, value = "SearchSupplierInvoiceHeader") // label for swagger
    @PostMapping("/findSupplierInvoiceHeader")
    public Stream<SupplierInvoiceHeader> findSupplierInvoiceHeader(@RequestBody SearchSupplierInvoiceHeader searchSupplierInvoiceHeader)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchSupplierInvoiceHeader.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return invoiceCancellationService.findSupplierInvoiceHeader(searchSupplierInvoiceHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}
