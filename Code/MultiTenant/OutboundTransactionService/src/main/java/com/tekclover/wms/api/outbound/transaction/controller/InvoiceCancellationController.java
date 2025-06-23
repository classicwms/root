package com.tekclover.wms.api.outbound.transaction.controller;


import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.PickListHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.SearchPickListHeader;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.PickListHeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"SupplierInvoiceCancel"}, value = "SupplierInvoice  Operations related to SupplierInvoiceCancellation")// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SupplierInvoice ", description = "Operations related to SupplierInvoice ")})
@RequestMapping("/invoice")
@RestController
public class InvoiceCancellationController {

    @Autowired
    PickListHeaderService pickListHeaderService;
	@Autowired
	DbConfigRepository dbConfigRepository;

    @ApiOperation(response = PickListHeader.class, value = "Search PickListHeader") // label for swagger
	@PostMapping("/findPickListHeader")
	public Stream<PickListHeader> findPickListHeader(@RequestBody SearchPickListHeader searchPickListHeader)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbList(searchPickListHeader.getCompanyCodeId(), searchPickListHeader.getPlantId(), searchPickListHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return pickListHeaderService.findPickListHeader(searchPickListHeader);
	}
finally {
			DataBaseContextHolder.clear();
		}
		}


}
