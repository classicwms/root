package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class SearchTransactionError {
	
	private String warehouseId;
	private String companyCodeId;
	private String plantId;
	private String languageId;
}