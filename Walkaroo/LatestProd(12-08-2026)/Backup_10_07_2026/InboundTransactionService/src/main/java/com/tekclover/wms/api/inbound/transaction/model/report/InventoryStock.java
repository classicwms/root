package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.Data;

@Data
public class InventoryStock {
	/*
	 * WH_ID
	 * ITM_CODE	
	 * Opening stock	
	 * Inbound Qty	
	 * Outbound Qty	
	 * Stock adjustment Qty	
	 * Closing stock
	 */
	 private String warehouseId;
	 private String itemCode;
	 private Double openingStock;
	 private Double inboundQty;
	 private Double outboundQty;
	 private Double stockAdjustmentQty;
	 private Double closingStock;
}
