package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.confirmation;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SalesOrder {
	
	@Valid
	private SalesOrderHeader soHeader;
	
	@Valid
	private List<SalesOrderLine> soLines;
}