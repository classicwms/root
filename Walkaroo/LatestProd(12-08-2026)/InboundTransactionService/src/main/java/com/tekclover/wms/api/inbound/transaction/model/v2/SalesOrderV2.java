package com.tekclover.wms.api.inbound.transaction.model.v2;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SalesOrderV2 {
	
	@Valid
	private SalesOrderHeaderV2 salesOrderHeader;
	
	@Valid
	private List<SalesOrderLineV2> salesOrderLine;
}
