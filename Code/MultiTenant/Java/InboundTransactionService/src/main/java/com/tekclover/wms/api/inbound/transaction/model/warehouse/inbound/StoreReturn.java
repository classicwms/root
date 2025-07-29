package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class StoreReturn {
	
	@Valid
	private StoreReturnHeader storeReturnHeader;
	
	@Valid
	private List<StoreReturnLine> storeReturnLine;
}
