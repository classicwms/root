package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InterWarehouseTransferInLine {

	@Column(nullable = false)
	@NotNull(message = "Line Reference is mandatory")
	private Long lineReference;								// IB_LINE_NO

	@Column(nullable = false)
	@NotBlank(message = "SKU Code is mandatory")
	private String sku; 									// ITM_CODE

	@Column(nullable = false)
	@NotBlank(message = "SKU Description is mandatory")
	private String skuDescription; 							// ITEM_TEXT

	private String invoiceNumber; 							// INV_NO
	private String containerNumber; 						// CONT_NO
	private String fromWhsId; 								// PARTNER_CODE
	private String supplierPartNumber;						// PARTNER_ITM_CODE
	private String manufacturerName;						// BRND_NM
	private String manufacturerPartNo;						// MFR_PART

	@Column(nullable = false)
	@NotBlank(message = "Expected Date is mandatory")
	private String expectedDate;							// EA_DATE

	@Column(nullable = false)
	@NotNull(message = "Expected Quantity is mandatory")
	private Double expectedQty;								// ORD_QTY

	@Column(nullable = false)
	@NotBlank(message = "UOM is mandatory")
	private String uom;										// ORD_UOM

	@Column(nullable = false)
	@NotNull(message = "Pack Quantity is mandatory")
	private Double packQty;									// ITM_CASE_QTY
}
