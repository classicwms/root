package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class SearchStockReport {

	private List<String> warehouseId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> itemCode;
	private List<String> manufacturerName;
	private List<String> itemText;
	private String stockTypeText;
}