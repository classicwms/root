package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class InterWarehouseTransferIn {

	@Valid
	private InterWarehouseTransferInHeader interWarehouseTransferInHeader;
	
	@Valid
	private List<InterWarehouseTransferInLine> interWarehouseTransferInLine;
}
