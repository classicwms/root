package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.confirmation;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class SOReturn {

	@Valid
	private SOReturnHeader returnOrderHeader;
	
	@Valid
	private List<SOReturnLine> returnOrderLines;
}
