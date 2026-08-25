package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

@Data
public class InterWarehouseTransferLine { 
	
	private String sku; 								// ITM_CODE
	private String skuDescription; 						// ITEM_TEXT
	private Long lineReference;							// IB_LINE_NO
	private Double expectedQty;							// ORD_QTY
	private String uom;									// ORD_UOM
	private Double receivedQty;							// ACCEPT_QTY
	private Double damageQty;							// DAMAGE_QTY
	private Double packQty;								// ITM_CASE_QTY
	private String actualReceiptDate;					// EA_DATE
	private String fromWhsId;	 						// PARTNER_CODE
	private String toWhsId;  							// WH_ID
	
}