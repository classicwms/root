package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ASNV2 {
	
	@Valid
	private ASNHeaderV2 asnHeader;
	
	@Valid
	private List<ASNLineV2> asnLine;
}
