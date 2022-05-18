package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class StockMovementReport {

	private String warehouseId;			// WH_ID
	private String itemCode;			// ITM_CODE
	private String manufacturerSKU; 	// MFR_SKU
	private String itemText;			// ITEM_TEXT
	private Double movementQty;			// MOV_QTY
	private String documentType;		// Document type
	private String documentNumber;		// Document Number
	private String customerCode;		// Customer Code
	private String createdOn;			// IM_CTD_ON date
	private String createdTime;			// IM_CTD_ON Time
	private Double balanceOHQty;		// Document Number
	private Double openingStock;		// Opening Stock
}
