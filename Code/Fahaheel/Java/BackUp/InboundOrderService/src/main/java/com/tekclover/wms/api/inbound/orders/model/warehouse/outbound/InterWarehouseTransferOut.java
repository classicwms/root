package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class InterWarehouseTransferOut {
	
	@Valid
	private InterWarehouseTransferOutHeader interWarehouseTransferOutHeader;
	
	@Valid
	private List<InterWarehouseTransferOutLine> interWarehouseTransferOutLine;
}
