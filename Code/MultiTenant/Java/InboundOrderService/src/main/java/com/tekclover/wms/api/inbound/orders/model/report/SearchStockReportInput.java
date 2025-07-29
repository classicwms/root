package com.tekclover.wms.api.inbound.orders.model.report;

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
}