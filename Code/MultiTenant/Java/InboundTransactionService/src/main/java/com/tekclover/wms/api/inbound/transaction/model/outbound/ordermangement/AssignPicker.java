package com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement;

import lombok.Data;

@Data
public class AssignPicker {

	private String warehouseId; 
	private String preOutboundNo; 
	private String refDocNumber; 
	private String partnerCode; 
	private Long lineNumber; 
	private String itemCode; 
	private String proposedStorageBin; 
	private String proposedPackCode;
}
