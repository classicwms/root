package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ReturnPO {
	
	@Valid
	private ReturnPOHeader returnPOHeader;
	
	@Valid
	private List<ReturnPOLine> returnPOLine;
}