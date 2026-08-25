package com.tekclover.wms.api.inbound.orders.model.inbound.staging;

import lombok.Data;

@Data
public class AssignHHTUser {

	private String warehouseId; 
	private String preInboundNo; 
	private String refDocNumber; 
	private String stagingNo; 
	private String palletCode; 
	private String caseCode; 
	private Long lineNo; 
	private String itemCode;
}
