package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class InterWarehouseTransfer {

	@Valid
	private InterWarehouseTransferHeader toHeader;
	
	@Valid
	private List<InterWarehouseTransferLine> toLines;
}