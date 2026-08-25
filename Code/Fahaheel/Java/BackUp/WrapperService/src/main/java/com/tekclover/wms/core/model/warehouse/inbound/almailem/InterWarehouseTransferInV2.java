package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class InterWarehouseTransferInV2 {

	@Valid
	private InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeader;
	
	@Valid
	private List<InterWarehouseTransferInLineV2> interWarehouseTransferInLine;
}
