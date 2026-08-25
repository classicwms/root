package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ShipmentOrderV2 {
	
	@Valid
	private SOHeaderV2 soHeader;
	
	@Valid
	private List<SOLineV2> soLine;
}
