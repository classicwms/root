package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class ReceiptConfimationReport {
	
	private String supplier;
	private String orderNumber;
	private String containerNo;
	private String orderType;
	
	private String sku; 				// ITM_CODE
	private String description;			// ITEM_TEXT
	private String mfrSku;				// MFR_PART
	private Double expectedQty;			// ORD_QTY
	private Double acceptedQty;			// ACCEPT_QTY
	private Double damagedQty;			// DAMAGE_QTY
	private Double missingORExcess;		// SUM(Accepted + Damaged) - Expected
	private String status;
	
	/*
	 * Total logic
	 */
	private Double expectedQtySum; 		// Sum of Expected Qty in the List
	private Double accxpectedQtySum; 	// Sum of Accepted Qty in the List
	private Double damagedQtySum; 		// Sum of Damaged Qty in the List
	private Double missingORExcessSum; 	// Sum of Missing/Excess in the List

//	private ReceiptHeader receiptHeader;
//	private List<Receipt> receiptList;
}
