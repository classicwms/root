package com.tekclover.wms.api.inbound.orders.model.dto;

import lombok.Data;

@Data
public class ImBasicData {

	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String itemCode;
	private String manufacturerName;

}