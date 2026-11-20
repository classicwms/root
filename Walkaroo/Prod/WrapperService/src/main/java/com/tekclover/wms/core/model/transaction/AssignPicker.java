package com.tekclover.wms.core.model.transaction;

import org.springframework.web.bind.annotation.RequestParam;

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
