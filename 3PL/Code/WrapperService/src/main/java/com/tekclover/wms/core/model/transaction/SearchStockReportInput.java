package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class SearchStockReportInput {

	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String itemCode;
	private String manufacturerName;
	private String itemText;
	private String stockTypeText;
	private String partnerCode;
	private String partnerDescription;
	private String partnerId;
}