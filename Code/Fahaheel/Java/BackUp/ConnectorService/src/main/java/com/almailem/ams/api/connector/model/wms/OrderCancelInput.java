package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

@Data
public class OrderCancelInput {

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