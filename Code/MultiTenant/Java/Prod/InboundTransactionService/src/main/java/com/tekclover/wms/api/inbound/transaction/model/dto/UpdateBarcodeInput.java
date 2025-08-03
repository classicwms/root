package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

@Data
public class UpdateBarcodeInput {

	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String itemCode;
	private String manufacturerName;
	private String barcodeId;
	private String loginUserID;

}