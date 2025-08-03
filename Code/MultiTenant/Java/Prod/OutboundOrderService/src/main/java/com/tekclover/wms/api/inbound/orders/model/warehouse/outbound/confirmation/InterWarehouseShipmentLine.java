package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.confirmation;

import lombok.Data;

@Data
public class InterWarehouseShipmentLine {
	
	/*
	 *  {
			 "sku": "026821038",
			 "skuDescription": "KRUGER COUGAR DIFFUSER 200ml",
			 "lineReference": 1,
			 "orderedQty ": 2,
			 "shippedQty": 2,
			 "deliverydate": "04:06:2021",
			 "FromWhsID": "111",
			 "ToWhsID": "110"
		 }
	 */
	private String sku; 								// ITM_CODE
	private String skuDescription; 						// ITEM_TEXT
	private Long lineReference;							// IB_LINE_NO
	private Double orderedQty;							// ORD_QTY
	private Double shippedQty;							// DLV_QTY
	private String deliveryDate;						// DLV_CNF_ON
	private String fromWhsID;							// WH_ID
	private String toWhsID;								// PARTNER_CODE
}
