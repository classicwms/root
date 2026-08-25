package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import java.util.List;


@Data
public class StockReceiptHeader {

	private String branchCode;
	private String companyCode;
	private String receiptNo;
private List<StockReceiptLine> stockReceiptLines;
}
