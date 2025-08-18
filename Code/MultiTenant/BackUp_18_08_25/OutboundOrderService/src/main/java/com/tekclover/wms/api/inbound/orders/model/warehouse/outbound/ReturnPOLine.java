package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ReturnPOLine {

	@Column(nullable = false)
	@NotNull(message = "Line Reference is mandatory")
	private Long lineReference;								// IB_LINE_NO

	@Column(nullable = false)
	@NotBlank(message = "SKU is mandatory")
	private String sku; 									// ITM_CODE

	private String skuDescription; 							// ITEM_TEXT

	@Column(nullable = false)
	@NotNull(message = "Return Quantity is mandatory")
	private Double returnQty;								// ORD_QTY

	@Column(nullable = false)
	@NotBlank(message = "UOM is mandatory")
	private String uom;										// ORD_UOM

	@Column(nullable = false)
	@NotBlank(message = "Order type is mandatory")
	private String orderType;								// REF_FIELD_1
}
