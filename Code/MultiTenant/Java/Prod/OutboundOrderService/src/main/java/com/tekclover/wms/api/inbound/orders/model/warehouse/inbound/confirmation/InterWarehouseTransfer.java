package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.confirmation;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class InterWarehouseTransfer {

	@Valid
	private InterWarehouseTransferHeader toHeader;
	
	@Valid
	private List<InterWarehouseTransferLine> toLines;
}
