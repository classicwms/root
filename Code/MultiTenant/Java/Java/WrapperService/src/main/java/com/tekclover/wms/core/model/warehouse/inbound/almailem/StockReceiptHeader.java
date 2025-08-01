package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import java.util.List;


@Data
public class StockReceiptHeader {

	private String branchCode;
	private String companyCode;
	private String receiptNo;
	private String warehouseId;
	private String languageId;
private List<StockReceiptLine> stockReceiptLines;
}
