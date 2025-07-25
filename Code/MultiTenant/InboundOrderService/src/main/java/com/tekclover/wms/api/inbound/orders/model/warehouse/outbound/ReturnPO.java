package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ReturnPO {
	
	@Valid
	private ReturnPOHeader returnPOHeader;
	
	@Valid
	private List<ReturnPOLine> returnPOLine;
}
