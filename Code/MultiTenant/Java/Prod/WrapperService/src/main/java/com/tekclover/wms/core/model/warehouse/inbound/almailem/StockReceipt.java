package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class StockReceipt {
	
	@Valid
	private StockReceiptHeader stockReceiptHeader;
	
	@Valid
	private List<StockReceiptLine> stockReceiptLine;
}
