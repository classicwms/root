package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

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
