package com.tekclover.wms.api.inbound.transaction.model.preoutbound;

import lombok.Data;

@Data
public class OutboundIntegrationLine { 
	
	private Long lineReference;								// IB_LINE_NO
	private String itemCode; 								// ITM_CODE
	private String itemText; 								// ITEM_TEXT
	private Double orderedQty;								// ORD_QTY
	private String uom;										// ORD_UOM
	private String refField1ForOrderType;					// REF_FIELD_1

}
