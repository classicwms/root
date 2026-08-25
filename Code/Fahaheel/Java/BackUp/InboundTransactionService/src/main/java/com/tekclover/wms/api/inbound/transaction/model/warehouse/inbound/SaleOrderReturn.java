package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class SaleOrderReturn {

	@Valid
	private SOReturnHeader soReturnHeader;
	
	@Valid
	private List<SOReturnLine> soReturnLine;
}
