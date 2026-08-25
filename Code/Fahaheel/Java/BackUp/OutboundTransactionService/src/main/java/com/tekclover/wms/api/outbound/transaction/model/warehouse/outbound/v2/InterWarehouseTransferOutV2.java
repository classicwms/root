package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class InterWarehouseTransferOutV2 {
	
	@Valid
	private InterWarehouseTransferOutHeaderV2 interWarehouseTransferOutHeader;
	
	@Valid
	private List<InterWarehouseTransferOutLineV2> interWarehouseTransferOutLine;
}
