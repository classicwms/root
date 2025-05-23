package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ASN {
	
	@Valid
	private ASNHeader asnHeader;
	
	@Valid
	private List<ASNLine> asnLine;
}