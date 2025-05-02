package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class StockReportOutput {

	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String itemCode;            // ITM_CODE
	private String manufacturerName;    // MFR_SKU
	private String itemText;            // ITEM_TEXT
	private Double invQty;            // INV_QTY
	private Double allocQty;            // Alloc Qty
	private Double totalQty;                // Total Qty
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String partnerCode;

}