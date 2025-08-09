package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.confirmation;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class SalesOrder {
	
	@Valid
	private SalesOrderHeader soHeader;
	
	@Valid
	private List<SalesOrderLine> soLines;
}
