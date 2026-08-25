package com.tekclover.wms.api.outbound.transaction.model.outbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchOutboundLineReport {
	
	private String warehouseId;
	private Date startConfirmedOn;
	private Date endConfirmedOn;
	
	private String partnerCode;
	private List<String> soTypeRefField1; 	// REF_FIELD_1
	private String refDocNumber;			// Order Number
}
