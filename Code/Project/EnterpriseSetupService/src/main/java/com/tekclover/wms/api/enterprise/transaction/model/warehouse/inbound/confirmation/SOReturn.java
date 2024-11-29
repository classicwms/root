package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SOReturn {

	@Valid
	private SOReturnHeader returnOrderHeader;
	
	@Valid
	private List<SOReturnLine> returnOrderLines;
}