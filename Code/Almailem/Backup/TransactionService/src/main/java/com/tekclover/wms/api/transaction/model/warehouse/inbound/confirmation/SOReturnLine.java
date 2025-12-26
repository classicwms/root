package com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

@Data
public class SOReturnLine { 
	
	private String salesOrderReference;					// REF_FIELD_4
	private String sku; 								// ITM_CODE
	private String skuDescription; 						// ITEM_TEXT
	private Long lineReference;							// IB_LINE_NO
	private Double expectedQty;							// ORD_QTY
	private String uom;									// ORD_UOM
	private Double returnQty;							// ACCEPT_QTY
	private String actualReceiptDate;					// EA_DATE
	private String wareHouseId; 						// WH_ID
}
