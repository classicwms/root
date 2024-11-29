package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ASN {
	
	@Valid
	private ASNHeader asnHeader;
	
	@Valid
	private List<ASNLine> asnLines;
}