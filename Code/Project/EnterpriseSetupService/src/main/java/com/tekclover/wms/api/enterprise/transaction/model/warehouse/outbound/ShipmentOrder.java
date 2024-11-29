package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ShipmentOrder {
	
	@Valid
	private SOHeader soHeader;
	
	@Valid
	private List<SOLine> soLine;
}