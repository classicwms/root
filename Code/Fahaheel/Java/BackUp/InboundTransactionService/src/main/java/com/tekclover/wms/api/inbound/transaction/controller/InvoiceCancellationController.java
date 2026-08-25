package com.tekclover.wms.api.inbound.transaction.controller;


import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.SearchSupplierInvoiceHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.SupplierInvoiceHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
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
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"SupplierInvoiceCancel"}, value = "SupplierInvoice  Operations related to SupplierInvoiceCancellation")// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SupplierInvoice ", description = "Operations related to SupplierInvoice ")})
@RequestMapping("/invoice")
@RestController
public class InvoiceCancellationController {

    @Autowired
    InvoiceCancellationService invoiceCancellationService;

    @Autowired
    DbConfigRepository dbConfigRepository;


    @ApiOperation(response = InboundHeaderV2.class, value = "Replace SupplierInvoice")
    @GetMapping("/supplierInvoice/cancellation")
    public ResponseEntity<?> replaceSupplierInvoice(@RequestParam String companyCode, @RequestParam String languageId,
                                                    @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String oldInvoiceNo,
                                                    @RequestParam String newInvoiceNo, @RequestParam String oldPreInboundNo,
                                                    @RequestParam String newPreInboundNo, @RequestParam String loginUserId) throws ParseException {

           WarehouseApiResponse result = invoiceCancellationService.replaceSupplierInvoice(companyCode, languageId, plantId, warehouseId,
                   oldPreInboundNo, oldInvoiceNo, newInvoiceNo, newPreInboundNo, loginUserId);
           return new ResponseEntity<>(result, HttpStatus.OK);
        }

    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Inbound Order Cancellation")
    @PostMapping("/inboundOrderCancellation")
    public ResponseEntity<?> cancelInboundOrder(@RequestBody InboundOrderCancelInput inboundOrderCancelInput,
                                                @RequestParam String loginUserId) throws ParseException {

        log.info("InboundOrderCancelInput details ------> {}", inboundOrderCancelInput);
        List<InboundOrderV2> inboundOrderV2List = null;
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(inboundOrderCancelInput.getCompanyCodeId(),
                inboundOrderCancelInput.getPlantId(),inboundOrderCancelInput.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        PreInboundHeaderEntityV2 result = invoiceCancellationService.inboundOrderCancellation(inboundOrderCancelInput, loginUserId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(response = SupplierInvoiceHeader.class, value = "SearchSupplierInvoiceHeader") // label for swagger
	@PostMapping("/findSupplierInvoiceHeader")
	public Stream<SupplierInvoiceHeader> findSupplierInvoiceHeader(@RequestBody SearchSupplierInvoiceHeader searchSupplierInvoiceHeader)
			throws Exception {
		return invoiceCancellationService.findSupplierInvoiceHeader(searchSupplierInvoiceHeader);
	}

}
