package com.tekclover.wms.core.model.transaction;

import java.util.List;

import lombok.Data;

@Data
public class ReceiptConfimationReport {

	private ReceiptHeader receiptHeader;
	private List<Receipt> receiptList;
}
