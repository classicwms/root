package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.confirmation;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ReturnPO {
	
	@Valid
	private ReturnPOHeader poHeader;
	
	@Valid
	private List<ReturnPOLine> poLines;
}
