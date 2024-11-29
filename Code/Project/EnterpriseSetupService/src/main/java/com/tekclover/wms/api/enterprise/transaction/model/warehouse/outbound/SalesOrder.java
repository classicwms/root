package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SalesOrder {
	
	@Valid
	private SalesOrderHeader salesOrderHeader;
	
	@Valid
	private List<SalesOrderLine> salesOrderLine;
}