package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

@Data
public class StoreReturnLine { 
	
	private String sku; 								// ITM_CODE
	private String skuDescription; 						// ITEM_TEXT
	private Long lineReference;							// IB_LINE_NO
	private Double expectedQty;							// EA_DATE
	private String uom;									// ORD_UOM
	private Double receivedQty;							// EA_DATE
	private Double damagedQty;							// ORD_QTY
	private Double packQty;								// ITM_CASE_QTY
	private String actualReceiptDate;					// EA_DATE
	private String storeId; 							// WH_ID
	private String wareHouseId; 						// WH_ID
}