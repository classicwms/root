package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.confirmation;

import lombok.Data;

@Data
public class ASNLine { 
	
	/*
	 * "asnLines": [
			 {
				 "sku": "026821038",
				 "skuDescription": "KRUGER COUGAR DIFFUSER 200ml",
				 "lineReference": 1,
				 "expectedQty": 2,
				 "uom": "PIECE",
				 "receivedQty": 2,
				 "damagedQty": 0,
				 "packQty": 2,
				 "actualReceiptDate": "05:05:2021",
				 "wareHouseId": "110"
			 }
		 ]
	 */
	
	private String sku; 								// ITM_CODE
	private String skuDescription; 						// ITEM_TEXT
	private Long lineReference;							// IB_LINE_NO
	private Double expectedQty;							// ORD_QTY
	private String uom;									// ORD_UOM
	private Double receivedQty;							// ORD_QTY
	private Double damagedQty;							// ORD_QTY
	private Double packQty;								// ITM_CASE_QTY
	private String actualReceiptDate;						// EA_DATE
	private String wareHouseId; 						// WH_ID
}
