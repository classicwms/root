package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class StockReceipt {
	
	@Valid
	private StockReceiptHeader stockReceiptHeader;
	
	@Valid
	private List<StockReceiptLine> stockReceiptLines;
}
