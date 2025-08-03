package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.confirmation;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ASN {
	
	@Valid
	private ASNHeader asnHeader;
	
	@Valid
	private List<ASNLine> asnLines;
}
