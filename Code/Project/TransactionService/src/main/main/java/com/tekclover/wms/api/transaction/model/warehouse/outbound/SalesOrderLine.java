package com.tekclover.wms.api.transaction.model.warehouse.outbound;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SalesOrderLine { 
	
	@NotNull(message = "Line Reference is mandatory")
	private Long lineReference;								// IB_LINE_NO
	
	@NotBlank(message = "SKU is mandatory")
	private String sku; 									// ITM_CODE

	private String skuDescription; 							// ITEM_TEXT

	@NotNull(message = "Ordered Quantity is mandatory")
	private Double orderedQty;								// ORD_QTY
	
	@NotBlank(message = "UOM is mandatory")
	private String uom;										// ORD_UOM
	
	@NotBlank(message = "Order type is mandatory")
	private String orderType;								// REF_FIELD_1
}
