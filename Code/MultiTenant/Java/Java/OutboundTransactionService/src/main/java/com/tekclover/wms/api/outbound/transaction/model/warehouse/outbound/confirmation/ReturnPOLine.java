package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.confirmation;

import lombok.Data;

@Data
public class ReturnPOLine { 
	
	/*
	 * {
		 "sku": "026821038",
		 "skuDescription": "KRUGER COUGAR DIFFUSER 200ml",
		 "lineReference": 1,
		 "ReturnQty": 1,
		 "ShippedQty": 2,
		 "wareHouseID": "110"
		 }
	 */
	
	private String sku; 								// ITM_CODE
	private String skuDescription; 						// ITEM_TEXT
	private Long lineReference;							// IB_LINE_NO
	private Double returnQty;							// ORD_QTY
	private Double shippedQty; 							// DLV_QTY
	private String warehouseID; 						// WH_ID
}
