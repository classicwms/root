package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.confirmation;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class Shipment {
	
	@Valid
	private ShipmentHeader toHeader;
	
	@Valid
	private List<ShipmentLine> toLines;
}