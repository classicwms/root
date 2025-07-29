package com.tekclover.wms.api.outbound.transaction.model.inbound.staging;

import lombok.Data;

@Data
public class CaseConfirmation {

	private String warehouseId; 
	private String preInboundNo; 
	private String refDocNumber; 
	private String stagingNo; 
	private String palletCode;
	private String caseCode;
	private Long lineNo; 
	private String itemCode;

	//Almailem Field
	private String manufactureCode;
}