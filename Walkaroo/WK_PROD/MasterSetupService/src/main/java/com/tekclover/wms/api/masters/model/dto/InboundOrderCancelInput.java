package com.tekclover.wms.api.masters.model.dto;

import lombok.Data;

@Data
public class InboundOrderCancelInput {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;

	private String refDocNumber;
	private String preInboundNo;
	private String remarks;
	private String referenceField1;
	private String referenceField2;
}