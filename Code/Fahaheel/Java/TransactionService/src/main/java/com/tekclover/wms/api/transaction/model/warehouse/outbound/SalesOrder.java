package com.tekclover.wms.api.transaction.model.warehouse.outbound;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class SalesOrder {
	
	@Valid
	private SalesOrderHeader salesOrderHeader;
	
	@Valid
	private List<SalesOrderLine> salesOrderLine;
}
