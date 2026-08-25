package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SaleOrderReturnV2 {

	@Valid
	private SOReturnHeaderV2 soReturnHeader;
	
	@Valid
	private List<SOReturnLineV2> soReturnLine;
}
