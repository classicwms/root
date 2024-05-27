package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class InventoryStockReport {
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
	private String itemDescription;
	private Double openingStock;
	private Double inboundQty;
	private Double outboundQty;
	private Double stockAdjustmentQty;
	private Double closingStock;
	private Double systemInventory;
	private Double variance;
}
