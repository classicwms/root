package com.tekclover.wms.api.enterprise.transaction.model.outbound;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOutboundLineReport {
	
	private String warehouseId;
	private Date startConfirmedOn;
	private Date endConfirmedOn;
	
	private String partnerCode;
	private List<String> soTypeRefField1; 	// REF_FIELD_1
	private String refDocNumber;			// Order Number
}