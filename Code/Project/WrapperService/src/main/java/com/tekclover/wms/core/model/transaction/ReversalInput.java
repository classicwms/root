package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class ReversalInput {

//	private String companyCodeId;
//	private String plantId;
//	private String languageId;
	private String warehouseId;

	private String itemCode;
	private String partnerCode;
	private String refDocNumber;
	private String preOutboundNo;
//	private String qcNumber;
	private Long lineReference;
//	private Long orderTypeId;
}