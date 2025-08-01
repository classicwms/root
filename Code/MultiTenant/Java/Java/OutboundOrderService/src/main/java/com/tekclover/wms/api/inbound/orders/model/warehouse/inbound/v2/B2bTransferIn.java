package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class B2bTransferIn {
	
	@Valid
	private B2bTransferInHeader b2bTransferInHeader;
	
	@Valid
	private List<B2bTransferInLine> b2bTransferLine;
}
