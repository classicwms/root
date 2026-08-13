package com.tekclover.wms.core.model.transaction;

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
