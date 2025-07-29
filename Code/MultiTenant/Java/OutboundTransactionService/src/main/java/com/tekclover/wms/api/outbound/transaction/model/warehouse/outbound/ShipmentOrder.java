package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ShipmentOrder {
	
	@Valid
	private SOHeader soHeader;
	
	@Valid
	private List<SOLine> soLine;
}
