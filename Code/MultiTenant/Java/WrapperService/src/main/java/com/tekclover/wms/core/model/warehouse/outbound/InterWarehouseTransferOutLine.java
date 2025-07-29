package com.tekclover.wms.core.model.warehouse.outbound;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InterWarehouseTransferOutLine { 
	
	@NotBlank(message = "Line Reference is mandatory")
	private String lineReference;							// IB_LINE_NO
	
	@NotBlank(message = "SKU Code is mandatory")
	private String skuCode; 								// ITM_CODE

	private String skuDescription; 							// ITEM_TEXT

	@NotNull(message = "Ordered Quantity is mandatory")
	private Double orderedQty;								// ORD_QTY
	
	@NotBlank(message = "UOM is mandatory")
	private String uom;										// ORD_UOM
	
	@NotNull(message = "Order type is mandatory")
	private Double orderType;								// REF_FIELD_1
}
