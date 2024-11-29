package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class InterWarehouseTransferOut {
	
	@Valid
	private InterWarehouseTransferOutHeader interWarehouseTransferOutHeader;
	
	@Valid
	private List<InterWarehouseTransferOutLine> interWarehouseTransferOutLine;
}