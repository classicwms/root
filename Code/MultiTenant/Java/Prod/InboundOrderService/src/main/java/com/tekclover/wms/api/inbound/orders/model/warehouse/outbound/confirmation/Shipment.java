package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.confirmation;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class Shipment {
	
	@Valid
	private ShipmentHeader toHeader;
	
	@Valid
	private List<ShipmentLine> toLines;
}
