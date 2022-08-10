package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SOReturnLine { 
	
	@NotNull(message = "Line Reference is mandatory")
	private Long lineReference;							// IB_LINE_NO
	
	@NotBlank(message = "SKU Code is mandatory")
	private String sku; 								// ITM_CODE

	@NotBlank(message = "SKU Description is mandatory")
	private String skuDescription; 							// ITEM_TEXT

	private String invoiceNumber; 							// INV_NO
	private String containerNumber; 						// CONT_NO
	
	@NotBlank(message = "StoreID is mandatory")
	private String storeID; 								// PARTNER_CODE
	private String supplierPartNumber;						// PARTNER_ITM_CODE
	private String manufacturerName;						// BRND_NM
	private String manufacturerPartNo;						// MFR_PART
	
	@NotBlank(message = "Expected Date is mandatory")
	private String expectedDate;							// EA_DATE
	
	@NotNull(message = "Expected Quantity is mandatory")
	private Double expectedQty;								// ORD_QTY
	
	@NotBlank(message = "UOM is mandatory")
	private String uom;										// ORD_UOM
	
	@NotNull(message = "Pack Quantity is mandatory")
	private Double packQty;									// ITM_CASE_QTY
	
	@NotBlank(message = "SalesOrderReference is mandatory")
	private String salesOrderReference;						// REF_FIELD_4
}
