package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class Receipt {

	private String sku; 		// ITM_CODE
	private String description;	// ITEM_TEXT
	private String mfrSku;		// MFR_PART
	private Double expectedQty;	// ORD_QTY
	private Double acceptedQty;	// ACCEPT_QTY
	private Double damagedQty;	// DAMAGE_QTY
	private Double missingORExcess;	// SUM(Accepted + Damaged) - Expected
	private String status;
}
