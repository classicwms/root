package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class InterWarehouseTransferIn {

	@Valid
	private InterWarehouseTransferInHeader interWarehouseTransferInHeader;
	
	@Valid
	private List<InterWarehouseTransferInLine> interWarehouseTransferInLine;
}