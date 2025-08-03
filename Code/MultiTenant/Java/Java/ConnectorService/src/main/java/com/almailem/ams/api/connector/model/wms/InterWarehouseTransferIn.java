package com.almailem.ams.api.connector.model.wms;

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
