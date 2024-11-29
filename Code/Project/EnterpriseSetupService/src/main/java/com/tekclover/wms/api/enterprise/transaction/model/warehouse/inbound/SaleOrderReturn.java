package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SaleOrderReturn {

	@Valid
	private SOReturnHeader soReturnHeader;
	
	@Valid
	private List<SOReturnLine> soReturnLine;
}