package com.tekclover.wms.core.model.warehouse.inbound.almailem;

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
